"use client";

import { useEffect, useState } from "react";
import { addToWatchlist, listWatchlist, removeFromWatchlist } from "@/features/watchlist/api";
import { useAuthToken } from "@/lib/auth";

export function WatchlistToggle({ auctionId }: { auctionId: number }) {
  const token = useAuthToken();
  const [watched, setWatched] = useState(false);
  const [pending, setPending] = useState(false);

  useEffect(() => {
    if (!token) {
      setWatched(false);
      return;
    }
    let cancelled = false;
    listWatchlist(token)
      .then((auctions) => {
        if (!cancelled) setWatched(auctions.some((a) => a.id === auctionId));
      })
      .catch(() => {});
    return () => {
      cancelled = true;
    };
  }, [token, auctionId]);

  if (!token) return null;

  async function toggle() {
    if (!token || pending) return;
    setPending(true);
    const next = !watched;
    setWatched(next);
    try {
      if (next) {
        await addToWatchlist(token, auctionId);
      } else {
        await removeFromWatchlist(token, auctionId);
      }
    } catch {
      setWatched(!next);
    } finally {
      setPending(false);
    }
  }

  return (
    <button
      onClick={toggle}
      disabled={pending}
      className={`rounded-sm border px-4 py-2 text-sm tracking-wide transition-colors disabled:opacity-50 ${
        watched
          ? "border-gold bg-gold text-forest hover:bg-gold-dark"
          : "border-taupe/30 text-taupe hover:border-gold hover:text-forest"
      }`}
    >
      {watched ? "★ Watching" : "☆ Add to watchlist"}
    </button>
  );
}
