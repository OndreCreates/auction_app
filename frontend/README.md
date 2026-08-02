# Auction frontend

Next.js UI for the auction backend: browse auctions, view live price updates over
STOMP/WebSocket, and place bids.

## Setup

```bash
npm install
cp .env.example .env.local   # points at the backend, defaults work for local dev
npm run dev
```

Requires the backend running at `NEXT_PUBLIC_API_URL` (default `http://localhost:8090`).

## Auth

Placing a bid requires a bearer token issued by `identity_server_app`. There is no
login flow here - paste an access token in the header to sign in. See
`identity_server_app`'s own demo client for the real OAuth2/PKCE flow.
