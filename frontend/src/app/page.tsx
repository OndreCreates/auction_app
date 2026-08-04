import Link from "next/link";
import { AuctionCard } from "@/components/auctions/AuctionCard";
import { listAuctions, listCategories } from "@/features/auctions/api";

export default async function HomePage({
  searchParams,
}: {
  searchParams: Promise<{ category?: string }>;
}) {
  const { category } = await searchParams;

  let auctions;
  let categories;
  try {
    [auctions, categories] = await Promise.all([listAuctions(category), listCategories()]);
  } catch {
    return (
      <div className="mx-auto max-w-6xl px-6 py-16">
        <p className="text-sm text-taupe">Couldn&apos;t reach the auction service. Is the backend running?</p>
      </div>
    );
  }

  return (
    <div>
      <section className="border-b border-taupe/15 bg-cream">
        <div className="mx-auto max-w-6xl px-6 py-16 text-center sm:py-20">
          <p className="text-xs tracking-[0.3em] text-gold uppercase">Est. curated auctions</p>
          <h1 className="mt-4 font-serif text-3xl leading-tight text-forest sm:text-5xl">
            Exceptional pieces,
            <br className="hidden sm:block" /> live to the highest bidder
          </h1>
          <p className="mx-auto mt-4 max-w-xl text-sm text-taupe sm:text-base">
            Watches, fine art, motorcars and more — bid in real time, verified provenance included.
          </p>
        </div>
      </section>

      <div className="mx-auto max-w-6xl px-6 py-12">
        <div className="flex flex-wrap items-center gap-2">
          <Link
            href="/"
            className={`rounded-full border px-4 py-1.5 text-sm transition-colors ${
              !category
                ? "border-gold bg-gold text-forest"
                : "border-taupe/25 text-taupe hover:border-gold hover:text-forest"
            }`}
          >
            All
          </Link>
          {categories.map((cat) => (
            <Link
              key={cat.id}
              href={`/?category=${cat.slug}`}
              className={`rounded-full border px-4 py-1.5 text-sm capitalize transition-colors ${
                category === cat.slug
                  ? "border-gold bg-gold text-forest"
                  : "border-taupe/25 text-taupe hover:border-gold hover:text-forest"
              }`}
            >
              {cat.name}
            </Link>
          ))}
        </div>

        <div className="mt-8 flex items-baseline justify-between">
          <h2 className="font-serif text-xl text-forest">Current lots</h2>
          <p className="text-sm text-taupe">{auctions.length} listed</p>
        </div>

        {auctions.length === 0 ? (
          <p className="mt-10 text-sm text-taupe">No auctions in this category yet.</p>
        ) : (
          <div className="mt-6 grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
            {auctions.map((auction) => (
              <AuctionCard key={auction.id} auction={auction} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
