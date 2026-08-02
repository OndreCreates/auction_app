// Server Components fetch from inside the container network in Docker Compose, so they need
// the internal service hostname (e.g. http://backend:8090), not what the browser can reach.
export const API_URL = process.env.API_URL ?? "http://localhost:8090";

// Client-side code runs in the user's browser, which can only reach the backend via its
// published host port - never the Docker Compose internal service name.
export const PUBLIC_API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8090";
export const WS_URL = process.env.NEXT_PUBLIC_WS_URL ?? "ws://localhost:8090/ws";
