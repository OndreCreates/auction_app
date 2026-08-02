import Link from "next/link";
import type { Auction } from "@/features/auctions/types";
import { formatPrice } from "@/lib/format";
import { StatusBadge } from "./StatusBadge";

export function AuctionCard({ auction }: { auction: Auction }) {
  return (
    <Link
      href={`/auctions/${auction.id}`}
      className="group flex flex-col gap-3 rounded-xl border border-neutral-800 bg-neutral-900/50 p-5 transition-colors hover:border-neutral-700 hover:bg-neutral-900"
    >
      <div className="flex items-start justify-between gap-3">
        <h2 className="text-base font-medium text-neutral-100 group-hover:text-white">{auction.title}</h2>
        <StatusBadge status={auction.status} />
      </div>

      {auction.description && (
        <p className="line-clamp-2 text-sm text-neutral-400">{auction.description}</p>
      )}

      <div className="mt-auto flex items-end justify-between pt-2">
        <div>
          <div className="text-xs text-neutral-500">Current bid</div>
          <div className="text-xl font-semibold text-neutral-50">{formatPrice(auction.currentPrice)}</div>
        </div>
      </div>
    </Link>
  );
}
