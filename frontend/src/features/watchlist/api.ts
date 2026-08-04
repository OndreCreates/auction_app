import { PUBLIC_API_URL } from "@/lib/config";
import { ApiError } from "@/features/auctions/api";
import type { Auction } from "@/features/auctions/types";

function authHeaders(token: string) {
  return { Authorization: `Bearer ${token}` };
}

async function handleResponse<T>(response: Response, parseJson: boolean): Promise<T> {
  if (!response.ok) {
    const body = await response.json().catch(() => null);
    throw new ApiError(response.status, body?.message ?? response.statusText);
  }
  return (parseJson ? response.json() : undefined) as Promise<T>;
}

export function listWatchlist(token: string): Promise<Auction[]> {
  return fetch(`${PUBLIC_API_URL}/watchlist`, {
    headers: authHeaders(token),
    cache: "no-store",
  }).then((res) => handleResponse<Auction[]>(res, true));
}

export function addToWatchlist(token: string, auctionId: number): Promise<void> {
  return fetch(`${PUBLIC_API_URL}/watchlist/${auctionId}`, {
    method: "POST",
    headers: authHeaders(token),
  }).then((res) => handleResponse<void>(res, false));
}

export function removeFromWatchlist(token: string, auctionId: number): Promise<void> {
  return fetch(`${PUBLIC_API_URL}/watchlist/${auctionId}`, {
    method: "DELETE",
    headers: authHeaders(token),
  }).then((res) => handleResponse<void>(res, false));
}
