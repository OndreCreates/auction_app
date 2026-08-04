import Link from "next/link";

export default function NotFound() {
  return (
    <div className="mx-auto flex max-w-5xl flex-col items-center px-6 py-24 text-center">
      <p className="text-sm font-medium tracking-widest text-gold-dark uppercase">404</p>
      <h1 className="mt-2 font-serif text-2xl text-forest">Auction not found</h1>
      <p className="mt-2 text-sm text-taupe">It may have been removed, or the link is wrong.</p>
      <Link
        href="/"
        className="mt-6 rounded-sm border border-taupe/30 px-4 py-2 text-sm text-forest transition-colors hover:border-gold hover:text-gold-dark"
      >
        Back to auctions
      </Link>
    </div>
  );
}
