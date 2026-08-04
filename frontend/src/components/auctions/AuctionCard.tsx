import Link from "next/link";
import type { Auction } from "@/features/auctions/types";
import { formatPrice } from "@/lib/format";
import { Crest } from "@/components/layout/Crest";
import { StatusBadge } from "./StatusBadge";

export function AuctionCard({ auction }: { auction: Auction }) {
  const image = auction.imageUrls[0];

  return (
    <Link
      href={`/auctions/${auction.id}`}
      className="group flex flex-col overflow-hidden rounded-xl border border-taupe/15 bg-white/40 transition-colors hover:border-gold"
    >
      <div className="relative aspect-[4/3] w-full overflow-hidden bg-forest/5">
        {image ? (
          // eslint-disable-next-line @next/next/no-img-element -- image URLs come from
          // whatever host the seller pasted in; not worth an open remotePatterns allowlist.
          <img
            src={image}
            alt={auction.title}
            className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105"
          />
        ) : (
          <div className="flex h-full items-center justify-center text-forest/15">
            <Crest className="h-12 w-12" />
          </div>
        )}
        <div className="absolute top-3 left-3">
          <StatusBadge status={auction.status} />
        </div>
      </div>

      <div className="flex flex-1 flex-col gap-3 p-5">
        <h2 className="font-serif text-lg text-forest group-hover:text-gold-dark">{auction.title}</h2>

        {auction.description && <p className="line-clamp-2 text-sm text-taupe">{auction.description}</p>}

        <div className="mt-auto flex items-end justify-between pt-2">
          <div>
            <div className="text-xs tracking-wide text-taupe uppercase">Current bid</div>
            <div className="text-xl font-semibold text-forest">{formatPrice(auction.currentPrice)}</div>
          </div>
          {auction.verified && (
            <span className="text-xs tracking-wide text-gold-dark uppercase">Verified</span>
          )}
        </div>
      </div>
    </Link>
  );
}
