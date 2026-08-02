import type { AuctionStatus } from "@/features/auctions/types";

const STYLES: Record<AuctionStatus, string> = {
  ACTIVE: "bg-emerald-400/10 text-emerald-400 ring-1 ring-inset ring-emerald-400/30",
  CLOSED: "bg-neutral-500/10 text-neutral-400 ring-1 ring-inset ring-neutral-500/30",
};

export function StatusBadge({ status }: { status: AuctionStatus }) {
  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${STYLES[status]}`}>
      {status === "ACTIVE" ? "Live" : "Closed"}
    </span>
  );
}
