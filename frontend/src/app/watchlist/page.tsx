"use client";

import { useEffect, useState } from "react";
import { AuctionCard } from "@/components/auctions/AuctionCard";
import type { Auction } from "@/features/auctions/types";
import { listWatchlist, removeFromWatchlist } from "@/features/watchlist/api";
import { useAuthToken } from "@/lib/auth";

export default function WatchlistPage() {
  const token = useAuthToken();
  const [auctions, setAuctions] = useState<Auction[] | null>(null);
  const [error, setError] = useState(false);

  useEffect(() => {
    if (!token) {
      setAuctions(null);
      return;
    }
    let cancelled = false;
    listWatchlist(token)
      .then((result) => {
        if (!cancelled) setAuctions(result);
      })
      .catch(() => {
        if (!cancelled) setError(true);
      });
    return () => {
      cancelled = true;
    };
  }, [token]);

  async function handleRemove(auctionId: number) {
    if (!token) return;
    setAuctions((current) => current?.filter((a) => a.id !== auctionId) ?? current);
    try {
      await removeFromWatchlist(token, auctionId);
    } catch {
      // best-effort: refetch on next mount will correct any drift
    }
  }

  return (
    <div className="mx-auto max-w-6xl px-6 py-12">
      <h1 className="font-serif text-2xl text-forest">Watchlist</h1>
      <p className="mt-1 text-sm text-taupe">Lots you&apos;re keeping an eye on.</p>

      {!token && (
        <p className="mt-10 text-sm text-taupe">Sign in with an access token above to see your watchlist.</p>
      )}

      {token && error && (
        <p className="mt-10 text-sm text-taupe">Couldn&apos;t load your watchlist. Is the backend running?</p>
      )}

      {token && !error && auctions === null && <p className="mt-10 text-sm text-taupe">Loading…</p>}

      {token && auctions !== null && auctions.length === 0 && (
        <p className="mt-10 text-sm text-taupe">Nothing here yet — add lots from their detail page.</p>
      )}

      {token && auctions !== null && auctions.length > 0 && (
        <div className="mt-8 grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
          {auctions.map((auction) => (
            <div key={auction.id} className="group/item relative">
              <AuctionCard auction={auction} />
              <button
                onClick={() => handleRemove(auction.id)}
                aria-label="Remove from watchlist"
                className="absolute top-3 right-3 flex h-7 w-7 items-center justify-center rounded-full bg-forest/70 text-cream transition-colors hover:bg-forest"
              >
                ×
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
