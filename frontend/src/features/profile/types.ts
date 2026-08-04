import type { Auction, Bid } from "@/features/auctions/types";

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export type BidHistoryPage = Page<Bid>;
export type WonAuctionsPage = Page<Auction>;
