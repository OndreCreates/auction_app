"use client";

import { useState, type FormEvent } from "react";
import { ApiError, placeBid } from "@/features/auctions/api";
import type { Auction } from "@/features/auctions/types";
import { useAuthToken } from "@/lib/auth";
import { formatPrice } from "@/lib/format";

export function BidForm({ auction }: { auction: Auction }) {
  const token = useAuthToken();
  const minBid = auction.currentPrice + auction.minIncrement;

  const [amount, setAmount] = useState(() => minBid.toFixed(2));
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  if (auction.status !== "ACTIVE") {
    return <p className="text-sm text-neutral-500">This auction is closed.</p>;
  }

  if (!token) {
    return (
      <p className="text-sm text-neutral-500">Sign in with an access token above to place a bid.</p>
    );
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    setSuccess(false);

    const parsed = Number(amount);
    if (!Number.isFinite(parsed) || parsed < minBid) {
      setError(`Bid must be at least ${formatPrice(minBid)}`);
      return;
    }

    setSubmitting(true);
    try {
      await placeBid(token as string, auction.id, parsed);
      setSuccess(true);
    } catch (err) {
      if (err instanceof ApiError && err.status === 409) {
        setError("Someone else just placed a bid first. Check the updated price and try again.");
      } else if (err instanceof ApiError && err.status === 401) {
        setError("Your access token is missing or invalid.");
      } else if (err instanceof ApiError) {
        setError(err.message);
      } else {
        setError("Something went wrong. Please try again.");
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} noValidate className="flex flex-col gap-3 sm:flex-row sm:items-start">
      <div className="flex-1">
        <input
          type="number"
          step="0.01"
          value={amount}
          onChange={(event) => setAmount(event.target.value)}
          className="w-full rounded-md border border-neutral-700 bg-neutral-900 px-3 py-2 text-neutral-100 focus:border-amber-400 focus:outline-none"
        />
        <p className="mt-1.5 text-xs text-neutral-500">Minimum bid: {formatPrice(minBid)}</p>
        {error && <p className="mt-2 text-sm text-red-400">{error}</p>}
        {success && <p className="mt-2 text-sm text-emerald-400">Bid placed.</p>}
      </div>
      <button
        type="submit"
        disabled={submitting}
        className="rounded-md bg-amber-400 px-5 py-2 font-medium text-neutral-950 transition-colors hover:bg-amber-300 disabled:cursor-not-allowed disabled:opacity-50"
      >
        {submitting ? "Placing…" : "Place bid"}
      </button>
    </form>
  );
}
