"use client";

import Link from "next/link";
import { useState } from "react";
import type { Auction, AuctionClosedMessage, Bid } from "@/features/auctions/types";
import { useAuctionSocket } from "@/features/auctions/useAuctionSocket";
import { formatDateTime, formatPrice } from "@/lib/format";
import { BidForm } from "./BidForm";
import { StatusBadge } from "./StatusBadge";

export function AuctionDetail({ initialAuction }: { initialAuction: Auction }) {
  const [auction, setAuction] = useState(initialAuction);
  const [recentBids, setRecentBids] = useState<Bid[]>([]);

  useAuctionSocket(auction.id, {
    onBid: (bid) => {
      setAuction((current) => ({ ...current, currentPrice: bid.amount }));
      setRecentBids((current) => [bid, ...current].slice(0, 5));
    },
    onClosed: (message: AuctionClosedMessage) => {
      setAuction((current) => ({ ...current, status: message.status, currentPrice: message.finalPrice }));
    },
  });

  return (
    <div className="mx-auto max-w-3xl px-6 py-12">
      <Link href="/" className="text-sm text-neutral-500 transition-colors hover:text-neutral-300">
        ← Auctions
      </Link>

      <div className="mt-4 flex items-start justify-between gap-4">
        <h1 className="text-2xl font-semibold tracking-tight text-neutral-50">{auction.title}</h1>
        <StatusBadge status={auction.status} />
      </div>

      {auction.description && <p className="mt-3 text-neutral-400">{auction.description}</p>}

      <dl className="mt-8 grid grid-cols-2 gap-6 rounded-xl border border-neutral-800 bg-neutral-900/50 p-6 sm:grid-cols-4">
        <div>
          <dt className="text-xs text-neutral-500">Current bid</dt>
          <dd className="mt-1 text-2xl font-semibold text-neutral-50">{formatPrice(auction.currentPrice)}</dd>
        </div>
        <div>
          <dt className="text-xs text-neutral-500">Starting price</dt>
          <dd className="mt-1 text-lg text-neutral-300">{formatPrice(auction.startingPrice)}</dd>
        </div>
        <div>
          <dt className="text-xs text-neutral-500">Min increment</dt>
          <dd className="mt-1 text-lg text-neutral-300">{formatPrice(auction.minIncrement)}</dd>
        </div>
        <div>
          <dt className="text-xs text-neutral-500">Ends</dt>
          <dd className="mt-1 text-lg text-neutral-300">{formatDateTime(auction.endTime)}</dd>
        </div>
      </dl>

      <div className="mt-8">
        <BidForm auction={auction} />
      </div>

      {recentBids.length > 0 && (
        <div className="mt-8">
          <h2 className="text-sm font-medium text-neutral-400">Recent bids</h2>
          <ul className="mt-2 divide-y divide-neutral-800 rounded-xl border border-neutral-800">
            {recentBids.map((bid) => (
              <li key={bid.id} className="flex items-center justify-between px-4 py-3 text-sm">
                <span className="text-neutral-400">{bid.bidderId}</span>
                <span className="font-medium text-neutral-100">{formatPrice(bid.amount)}</span>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}
