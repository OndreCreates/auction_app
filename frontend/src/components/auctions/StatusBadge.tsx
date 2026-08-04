import type { AuctionStatus } from "@/features/auctions/types";

const STYLES: Record<AuctionStatus, string> = {
  ACTIVE: "bg-gold/15 text-gold-dark ring-1 ring-inset ring-gold/40",
  CLOSED: "bg-taupe/10 text-taupe ring-1 ring-inset ring-taupe/30",
};

export function StatusBadge({ status }: { status: AuctionStatus }) {
  return (
    <span
      className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium tracking-wide uppercase ${STYLES[status]}`}
    >
      {status === "ACTIVE" ? "Live" : "Closed"}
    </span>
  );
}
