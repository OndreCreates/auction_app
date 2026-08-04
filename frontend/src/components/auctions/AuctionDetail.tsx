"use client";

import Link from "next/link";
import { useState } from "react";
import type { Auction, AuctionClosedMessage, Bid } from "@/features/auctions/types";
import { useAuctionSocket } from "@/features/auctions/useAuctionSocket";
import { formatDateTime, formatPrice } from "@/lib/format";
import { BidForm } from "./BidForm";
import { StatusBadge } from "./StatusBadge";
import { WatchlistToggle } from "./WatchlistToggle";
import { Crest } from "@/components/layout/Crest";

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

  const image = auction.imageUrls[0];

  return (
    <div className="mx-auto max-w-3xl px-6 py-12">
      <Link href="/" className="text-sm text-taupe transition-colors hover:text-gold-dark">
        ← Auctions
      </Link>

      <div className="mt-4 aspect-[16/9] w-full overflow-hidden rounded-xl bg-forest/5">
        {image ? (
          // eslint-disable-next-line @next/next/no-img-element
          <img src={image} alt={auction.title} className="h-full w-full object-cover" />
        ) : (
          <div className="flex h-full items-center justify-center text-forest/15">
            <Crest className="h-16 w-16" />
          </div>
        )}
      </div>

      <div className="mt-6 flex items-start justify-between gap-4">
        <div>
          <h1 className="font-serif text-3xl text-forest">{auction.title}</h1>
          {auction.verified && (
            <span className="mt-1 inline-block text-xs tracking-wide text-gold-dark uppercase">
              ✓ Verified provenance
            </span>
          )}
        </div>
        <StatusBadge status={auction.status} />
      </div>

      {auction.description && <p className="mt-3 text-taupe">{auction.description}</p>}
      {auction.provenance && (
        <p className="mt-3 border-l-2 border-gold/50 pl-4 text-sm text-taupe italic">{auction.provenance}</p>
      )}

      <dl className="mt-8 grid grid-cols-2 gap-6 rounded-xl border border-taupe/15 bg-white/40 p-6 sm:grid-cols-4">
        <div>
          <dt className="text-xs tracking-wide text-taupe uppercase">Current bid</dt>
          <dd className="mt-1 text-2xl font-semibold text-forest">{formatPrice(auction.currentPrice)}</dd>
        </div>
        <div>
          <dt className="text-xs tracking-wide text-taupe uppercase">Starting price</dt>
          <dd className="mt-1 text-lg text-forest">{formatPrice(auction.startingPrice)}</dd>
        </div>
        <div>
          <dt className="text-xs tracking-wide text-taupe uppercase">Min increment</dt>
          <dd className="mt-1 text-lg text-forest">{formatPrice(auction.minIncrement)}</dd>
        </div>
        <div>
          <dt className="text-xs tracking-wide text-taupe uppercase">Ends</dt>
          <dd className="mt-1 text-lg text-forest">{formatDateTime(auction.endTime)}</dd>
        </div>
      </dl>

      <div className="mt-6">
        <WatchlistToggle auctionId={auction.id} />
      </div>

      <div className="mt-8">
        <BidForm auction={auction} />
      </div>

      {recentBids.length > 0 && (
        <div className="mt-8">
          <h2 className="text-sm font-medium tracking-wide text-taupe uppercase">Recent bids</h2>
          <ul className="mt-2 divide-y divide-taupe/15 rounded-xl border border-taupe/15">
            {recentBids.map((bid) => (
              <li key={bid.id} className="flex items-center justify-between px-4 py-3 text-sm">
                <span className="text-taupe">{bid.bidderId}</span>
                <span className="font-medium text-forest">{formatPrice(bid.amount)}</span>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}
