"use client";

import { useSyncExternalStore } from "react";

const TOKEN_KEY = "auction-app.access-token";
const listeners = new Set<() => void>();

// Real login goes through identity_server_app's own OAuth2/PKCE flow (see its
// demo-client) - out of scope here. This app expects a token pasted in by hand,
// which is enough to prove the bearer-auth wiring end to end.
export function getToken(): string | null {
  if (typeof window === "undefined") return null;
  return window.localStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string): void {
  window.localStorage.setItem(TOKEN_KEY, token);
  listeners.forEach((listener) => listener());
}

export function clearToken(): void {
  window.localStorage.removeItem(TOKEN_KEY);
  listeners.forEach((listener) => listener());
}

function subscribe(listener: () => void): () => void {
  listeners.add(listener);
  return () => listeners.delete(listener);
}

export function useAuthToken(): string | null {
  return useSyncExternalStore(subscribe, getToken, () => null);
}
