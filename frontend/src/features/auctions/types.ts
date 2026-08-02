export type AuctionStatus = "ACTIVE" | "CLOSED";

export interface Auction {
  id: number;
  title: string;
  description: string | null;
  sellerId: string;
  startingPrice: number;
  minIncrement: number;
  currentPrice: number;
  status: AuctionStatus;
  startTime: string;
  endTime: string;
}

export interface Bid {
  id: number;
  auctionId: number;
  bidderId: string;
  amount: number;
  createdAt: string;
}

export interface AuctionClosedMessage {
  auctionId: number;
  status: AuctionStatus;
  finalPrice: number;
}
