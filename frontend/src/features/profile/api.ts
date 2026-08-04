import { PUBLIC_API_URL } from "@/lib/config";
import { ApiError } from "@/features/auctions/api";
import type { BidHistoryPage, WonAuctionsPage } from "./types";

function authHeaders(token: string) {
  return { Authorization: `Bearer ${token}` };
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const body = await response.json().catch(() => null);
    throw new ApiError(response.status, body?.message ?? response.statusText);
  }
  return response.json() as Promise<T>;
}

export function getBidHistory(token: string, page = 0, size = 10): Promise<BidHistoryPage> {
  return fetch(`${PUBLIC_API_URL}/profile/bids?page=${page}&size=${size}`, {
    headers: authHeaders(token),
    cache: "no-store",
  }).then((res) => handleResponse<BidHistoryPage>(res));
}

export function getWonAuctions(token: string, page = 0, size = 10): Promise<WonAuctionsPage> {
  return fetch(`${PUBLIC_API_URL}/profile/won?page=${page}&size=${size}`, {
    headers: authHeaders(token),
    cache: "no-store",
  }).then((res) => handleResponse<WonAuctionsPage>(res));
}
