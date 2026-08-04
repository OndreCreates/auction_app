# Auction App

Real-time aukční dům postavený na Spring Bootu 4 a Next.js. Backend obsluhuje
vytváření aukcí a dražení s optimistickým zamykáním proti souběžným bidům,
automatické uzavírání aukcí po vypršení času, živé přenosy přes WebSocket/STOMP,
kategorie a rozšířený produktový model, watchlist s notifikacemi na nový bid
a profil s historií bidů a výher. Frontend je Next.js klient s brand identitou
(kategorie, watchlist, profil, živé ceny) nad tímto API.

Portfolio projekt zaměřený na jádro problému, který dražení skutečně řeší:
souběžnost. Kdokoliv umí naklikat formulář na CRUD; ukázat, že dva bidy
narazí do sebe a systém to ustojí bez ztraceného zápisu, je to, co má cenu
u pohovoru obhajovat.

## Architektura

```
┌──────────────┐   REST + WebSocket    ┌──────────────────┐      JDBC      ┌───────┐
│  Next.js UI  │ ─────────────────────►│  Spring Boot API  │ ─────────────►│ MySQL │
│ (frontend/)  │◄───────────────────── │   (auction-app)   │◄───────────── │       │
└──────────────┘   STOMP /topic/*      └──────┬───────┬────┘                └───────┘
                                               │       │
                              JWKS (JWT validace)     │ REST (fire-and-forget)
                                               ▼       ▼
                             ┌──────────────────────┐  ┌───────────────────────────┐
                             │  identity_server_app  │  │ notification_center_app   │
                             └──────────────────────┘  └───────────────────────────┘
                                  (samostatný projekt)      (samostatný projekt)
```

- **auction-app** (backend) — REST API pro aukce, bidy, kategorie, watchlist
  a profil; optimistické zamykání, scheduler pro automatické uzavírání, STOMP
  broadcast nového bidu / uzavření aukce, OAuth2 Resource Server ověřující JWT
  proti `identity_server_app`.
- **frontend/** — Next.js 16 (App Router, TypeScript, Tailwind). Domovská
  stránka s filtrem kategorií, detail s formulářem pro bid a watchlist
  přepínačem, stránka watchlistu, profil s historií bidů a výher, živé ceny
  přes `@stomp/stompjs`.
- **MySQL** — jediné úložiště, migrace přes Flyway (`V1`–`V6`).
- **identity_server_app** — samostatný OAuth2/OIDC provider (vlastní repo), tady
  se pouze validuje jím vydaný JWT proti jeho JWKS endpointu.
- **notification_center_app** — samostatný projekt (vlastní repo); dostává
  fire-and-forget REST volání při novém bidu na sledované aukci. Jeho výpadek
  nikdy nesmí shodit bid endpoint, viz E2E ověření níže.

## Tech stack

Backend: Java 17, Spring Boot 4.0.7 (Spring Security 7 / Spring Data JPA /
Spring WebSocket), MySQL 8.4, Flyway, Maven.
Frontend: Next.js 16, React 19, TypeScript, Tailwind CSS 4, `@stomp/stompjs`.

## Spuštění

### Docker Compose (celý stack)

```bash
docker compose up --build
```

Spustí MySQL, backend (`:8090`) a frontend (`:3002` — `:3000` je na tomhle
stroji obsazený `identity_server_app`'s demo klientem, `:3001` admin panelem
`incident_management_system_app`). Backend čeká, až je MySQL healthy; migrace
se aplikují automaticky při startu.

Přihlašování (JWT) jde přes samostatně běžící `identity_server_app`. Pokud
neběží na `http://localhost:9000`, přepiš `JWKS_URI` v `docker-compose.yml`
nebo přes `.env`. Bez platného tokenu jde aukce jen prohlížet (`GET`), ne
zakládat ani nabízet.

### Bez Dockeru

```bash
docker compose up -d mysql          # jen databáze
./mvnw spring-boot:run              # backend na :8090

cd frontend
cp .env.example .env.local
npm install && npm run dev          # frontend na :3000
```

### Testy

```bash
./mvnw test              # backend - vyžaduje běžící MySQL (docker compose up -d mysql)
cd frontend && npm run build   # typecheck + produkční build
```

## Přihlášení / bidování ve frontendu

Frontend nemá vlastní login flow — `identity_server_app` má na to vlastní
demo klienta s plným OAuth2/PKCE flow, duplikovat ho tady by bylo zbytečné.
Místo toho jde v hlavičce vložit rovnou access token (Bearer JWT) získaný
odjinud. Bez tokenu se dá aukce jen prohlížet.

## Zajímavá návrhová rozhodnutí

- **Optimistické zamykání, ne pesimistické.** `Auction.version` (`@Version`)
  řeší souběžné bidy bez zamykání řádků po celou dobu transakce. Concurrency
  test (10 vláken, stejná částka, jedna aukce) odhalil, že MySQL/InnoDB pod
  tímhle tlakem občas vrátí deadlock (`CannotAcquireLockException`), ne jen
  verzní konflikt (`OptimisticLockingFailureException`) — obě dvě dědí ze
  společné `ConcurrencyFailureException`, takže handler chytá tenhle
  společný předek, ne jen ten "hezčí" případ. Bez toho by deadlock skončil
  jako neošetřená 500, ne jako čistá 409.
- **Broadcast odděleně od zápisu bidu přes `ApplicationEventPublisher` +
  `@TransactionalEventListener(AFTER_COMMIT)`.** Publikace na WebSocket topic
  běží až po commitu, takže (a) prohraný bid nikdy nevyvolá broadcast, protože
  se jeho transakce vůbec nezacommitne, a (b) pád brokeru/WebSocketu nemůže
  shodit request, který jinak uspěl.
- **`sellerId`/`bidderId` jsou e-mail (`VARCHAR`), ne numerické ID.**
  `identity_server_app` dává do JWT `sub` claimu e-mail, žádné číselné ID
  uživatele nikdy nevydává — stejná konvence jako v
  `incident_management_system_app`. Sloupce prošly migrací z `BIGINT` na
  `VARCHAR` až ve Fázi 5, kdy se placeholder ID nahrazovalo reálným
  principálem z tokenu.
- **`GET` je veřejné, `POST` vyžaduje token.** Prohlížení aukcí nemá důvod
  být za loginem; zabezpečené je jen vytváření aukcí a nabízení.
- **CORS i WebSocket origin musí být nastavené zvlášť, a jako seznam, ne jedna
  hodnota.** Spring Security `cors()` řeší jen REST volání; STOMP endpoint
  (`registerStompEndpoints`) má vlastní `setAllowedOrigins` a bez něj defaultně
  odmítá handshake z jiného originu — frontend proti backendu na `:8090` je
  vždycky jiný origin, takže bez tohohle by živé aktualizace cen tiše
  nefungovaly. Oba seznamy origins zahrnují `:3000` (`npm run dev`) i `:3002`
  (docker-compose), protože se liší podle toho, kde zrovna běžící port
  `identity_server_app`'s demo klienta uvolní.
- **Next.js standalone output + `HOSTNAME=0.0.0.0` v Dockerfile.** Docker
  injektuje `HOSTNAME` jako ID kontejneru; Next na tuhle hodnotu binduje server
  místo na všechna rozhraní, takže bez přepsání by frontend v kontejneru
  neposlouchal na publikovaném portu.
- **`NEXT_PUBLIC_*` proměnné se zapékají do JS bundlu při buildu**, ne při
  startu kontejneru — nastavovat je v `docker-compose.yml` přes `environment`
  by bylo tiché no-op. Server-side fetch (Server Components) proto používá
  samostatnou, ne-veřejnou `API_URL` (v Dockeru `http://backend:8090`),
  zatímco browser vždy potřebuje veřejně dostupnou adresu.
- **Spring Boot 4 / Jackson 3.** `start.spring.io` v době psaní už nenabízí
  Spring Boot 3.x větev (`compatibility range is >=4.0.0`), takže backend jede
  na Boot 4.0.7 se Spring Security 7 a Jacksonem 3 (`tools.jackson.*`) jako
  primární JSON knihovnou. Jediný viditelný dopad na tenhle projekt: STOMP
  test klient potřebuje `JacksonJsonMessageConverter` (Jackson 3), ne starý
  `MappingJackson2MessageConverter`.

## Známá omezení (záměrná)

- **Žádný login flow ve frontendu** — viz výše, řešeno vkládáním tokenu ručně.
- **Žádná stránka pro založení aukce ve frontendu** — backend to umí
  (`POST /auctions`), UI na to podle zadání nebylo v plánu.
- **Seznam aukcí bez stránkování** — v pořádku pro pár desítek aukcí v dev
  prostředí, na produkční škále by chtělo stránkování/cursor. Profil (historie
  bidů, výhry) stránkování má, protože je to tam explicitní požadavek fáze.
- **JWT signing key rotace, MFA a podobné bezpečnostní featury** jsou v
  gesci `identity_server_app`, ne tohohle projektu.

Vyřazeno z rozsahu vědomě, ne opomenutím — nepřidává technickou hloubku k tomu,
co má projekt demonstrovat (real-time + concurrency), a každé je samo o sobě
jiná doména:

- **Escrow / platby** — fintech doména sama o sobě (PCI compliance, držení
  cizích peněz, 3rd party integrace).
- **Invite-only přístup / membership** — komplikuje auth, který už řeší
  `identity_server_app`, bez nového technického příběhu.
- **Doprava a pojištění** — logistická doména, nulová souvislost s
  real-time/concurrency tématem projektu.
- **Zákaznická podpora VIP 24/7** — jiná doména (ticketing systém), navíc
  neposkytnutelná v portfolio demu.

## E2E ověření

Ručně ověřené scénáře (viz `git log` pro odpovídající commity a automatizované
testy pro každou fázi):

- **Souběžné bidy** — `BidConcurrencyTest` pouští 10 vláken na stejnou aukci se
  stejnou částkou; přesně jedno uspěje, zbytek dostane 409 (verzní konflikt
  nebo deadlock, viz návrhová rozhodnutí výše). Auction `currentPrice` a počet
  uložených bidů odpovídá jen vítězi.
- **Uzavření aukce** — `AuctionCloseSchedulerTest` s fixním `Clock` ověřuje, že
  scheduler zavře expirované aukce (včetně té bez jediného bidu, kde cena
  zůstane na `startingPrice`) a nechá běžící netknuté.
- **Live broadcast** — `BidBroadcastIntegrationTest` se připojí jako skutečný
  STOMP klient, odešle bid přes `BidService` a ověří doručení zprávy na
  `/topic/auctions/{id}`; ručně ověřeno i ve skutečném prohlížeči (viz commit
  historie Fáze 6 — oprava CORS/origin bugu na WebSocket endpointu, který by
  jinak živé aktualizace potichu shodil).
- **401/409 v UI** — bid formulář ve frontendu byl ručně proklikaný s
  neplatným tokenem (401, jasná hláška) a s částkou pod minimem (400,
  client-side validace bez spoléhání na nativní HTML validaci prohlížeče).
- **Výpadek `notification_center_app` nezpůsobí selhání bid endpointu** —
  `WatchlistNotificationListenerTest` ověřuje, že chyba v `NotificationClient`
  neshodí listener; `RestNotificationClient` sám každou chybu za jednotlivého
  příjemce loguje a polyká (`catch (RestClientException)`), stejně jako
  `BidBroadcastListener` u WebSocketu. Ručně ověřeno i vypnutím
  `notification.api-key` — bid projde, jen se zaloguje "skipping watchlist
  notification".
- **`docker compose up --build`** — backend a frontend image se sestaví a
  nastartují čistě proti MySQL v témže compose souboru; ověřeno v prohlížeči
  proti kontejnerizovanému frontendu (`http://localhost:3002`) včetně SSR
  fetch přes interní síť (`http://backend:8090`), CORS pro REST i WebSocket
  handshake pro STOMP. Cestou se ukázaly a opravily tři reálné Docker
  problémy: standalone Next.js server bindující na injektované `HOSTNAME`
  místo všech rozhraní, `NEXT_PUBLIC_*` proměnné nastavené v compose
  `environment` jako tiché no-op (zapékají se při buildu, ne při startu), a
  port `:3000` trvale obsazený jiným během projektem na stejném stroji.
