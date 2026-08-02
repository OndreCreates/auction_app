import { AuctionCard } from "@/components/auctions/AuctionCard";
import { listAuctions } from "@/features/auctions/api";

export default async function HomePage() {
  let auctions;
  try {
    auctions = await listAuctions();
  } catch {
    return (
      <div className="mx-auto max-w-5xl px-6 py-16">
        <p className="text-sm text-red-400">
          Couldn&apos;t reach the auction service. Is the backend running?
        </p>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-5xl px-6 py-12">
      <h1 className="text-2xl font-semibold tracking-tight text-neutral-50">Auctions</h1>
      <p className="mt-1 text-sm text-neutral-400">{auctions.length} listed</p>

      {auctions.length === 0 ? (
        <p className="mt-10 text-sm text-neutral-500">No auctions yet.</p>
      ) : (
        <div className="mt-8 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {auctions.map((auction) => (
            <AuctionCard key={auction.id} auction={auction} />
          ))}
        </div>
      )}
    </div>
  );
}
