import { API_URL, PUBLIC_API_URL } from "@/lib/config";
import type { Auction, Bid, Category } from "./types";

export class ApiError extends Error {
  constructor(
    public status: number,
    message: string,
  ) {
    super(message);
    this.name = "ApiError";
  }
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const body = await response.json().catch(() => null);
    throw new ApiError(response.status, body?.message ?? response.statusText);
  }
  return response.json() as Promise<T>;
}

export function listAuctions(category?: string): Promise<Auction[]> {
  const query = category ? `?category=${encodeURIComponent(category)}` : "";
  return fetch(`${API_URL}/auctions${query}`, { cache: "no-store" }).then((res) => handleResponse<Auction[]>(res));
}

export function getAuction(id: number): Promise<Auction> {
  return fetch(`${API_URL}/auctions/${id}`, { cache: "no-store" }).then((res) => handleResponse<Auction>(res));
}

export function listCategories(): Promise<Category[]> {
  return fetch(`${API_URL}/categories`, { cache: "no-store" }).then((res) => handleResponse<Category[]>(res));
}

export function placeBid(token: string, auctionId: number, amount: number): Promise<Bid> {
  return fetch(`${PUBLIC_API_URL}/auctions/${auctionId}/bids`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({ amount }),
  }).then((res) => handleResponse<Bid>(res));
}
