"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { StatusBadge } from "@/components/auctions/StatusBadge";
import { getBidHistory, getWonAuctions } from "@/features/profile/api";
import type { BidHistoryPage, WonAuctionsPage } from "@/features/profile/types";
import { useAuthToken } from "@/lib/auth";
import { formatDateTime, formatPrice } from "@/lib/format";

type Tab = "bids" | "won";
const PAGE_SIZE = 10;

export default function ProfilePage() {
  const token = useAuthToken();
  const [tab, setTab] = useState<Tab>("bids");
  const [page, setPage] = useState(0);
  const [bids, setBids] = useState<BidHistoryPage | null>(null);
  const [won, setWon] = useState<WonAuctionsPage | null>(null);
  const [error, setError] = useState(false);

  useEffect(() => {
    setPage(0);
  }, [tab]);

  useEffect(() => {
    if (!token) return;
    setError(false);
    if (tab === "bids") {
      getBidHistory(token, page, PAGE_SIZE)
        .then(setBids)
        .catch(() => setError(true));
    } else {
      getWonAuctions(token, page, PAGE_SIZE)
        .then(setWon)
        .catch(() => setError(true));
    }
  }, [token, tab, page]);

  if (!token) {
    return (
      <div className="mx-auto max-w-4xl px-6 py-12">
        <h1 className="font-serif text-2xl text-forest">Profile</h1>
        <p className="mt-10 text-sm text-taupe">Sign in with an access token above to see your activity.</p>
      </div>
    );
  }

  const activePage = tab === "bids" ? bids : won;

  return (
    <div className="mx-auto max-w-4xl px-6 py-12">
      <h1 className="font-serif text-2xl text-forest">Profile</h1>
      <p className="mt-1 text-sm text-taupe">Your bidding history and winnings.</p>

      <div className="mt-8 flex gap-2 border-b border-taupe/15">
        {(["bids", "won"] as const).map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`px-4 py-2 text-sm tracking-wide uppercase transition-colors ${
              tab === t
                ? "border-b-2 border-gold text-forest"
                : "border-b-2 border-transparent text-taupe hover:text-forest"
            }`}
          >
            {t === "bids" ? "Bid history" : "Won auctions"}
          </button>
        ))}
      </div>

      {error && <p className="mt-10 text-sm text-taupe">Couldn&apos;t load your activity. Is the backend running?</p>}

      {!error && activePage === null && <p className="mt-10 text-sm text-taupe">Loading…</p>}

      {!error && activePage !== null && activePage.content.length === 0 && (
        <p className="mt-10 text-sm text-taupe">
          {tab === "bids" ? "You haven't placed any bids yet." : "No wins yet — keep bidding."}
        </p>
      )}

      {!error && tab === "bids" && bids !== null && bids.content.length > 0 && (
        <ul className="mt-6 divide-y divide-taupe/15 rounded-xl border border-taupe/15">
          {bids.content.map((bid) => (
            <li key={bid.id} className="flex items-center justify-between px-4 py-3 text-sm">
              <div>
                <Link href={`/auctions/${bid.auctionId}`} className="text-forest hover:text-gold-dark">
                  Auction #{bid.auctionId}
                </Link>
                <div className="text-xs text-taupe">{formatDateTime(bid.createdAt)}</div>
              </div>
              <span className="font-medium text-forest">{formatPrice(bid.amount)}</span>
            </li>
          ))}
        </ul>
      )}

      {!error && tab === "won" && won !== null && won.content.length > 0 && (
        <ul className="mt-6 divide-y divide-taupe/15 rounded-xl border border-taupe/15">
          {won.content.map((auction) => (
            <li key={auction.id} className="flex items-center justify-between px-4 py-3 text-sm">
              <div className="flex items-center gap-3">
                <Link href={`/auctions/${auction.id}`} className="text-forest hover:text-gold-dark">
                  {auction.title}
                </Link>
                <StatusBadge status={auction.status} />
              </div>
              <span className="font-medium text-forest">{formatPrice(auction.currentPrice)}</span>
            </li>
          ))}
        </ul>
      )}

      {activePage !== null && activePage.totalPages > 1 && (
        <div className="mt-6 flex items-center justify-between text-sm text-taupe">
          <button
            onClick={() => setPage((p) => Math.max(0, p - 1))}
            disabled={page === 0}
            className="rounded-sm border border-taupe/30 px-3 py-1.5 transition-colors hover:border-gold hover:text-forest disabled:cursor-not-allowed disabled:opacity-40"
          >
            Previous
          </button>
          <span>
            Page {activePage.number + 1} of {activePage.totalPages}
          </span>
          <button
            onClick={() => setPage((p) => Math.min(activePage.totalPages - 1, p + 1))}
            disabled={page >= activePage.totalPages - 1}
            className="rounded-sm border border-taupe/30 px-3 py-1.5 transition-colors hover:border-gold hover:text-forest disabled:cursor-not-allowed disabled:opacity-40"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}
