import Link from "next/link";

export default function NotFound() {
  return (
    <div className="mx-auto flex max-w-5xl flex-col items-center px-6 py-24 text-center">
      <p className="text-sm font-medium text-amber-400">404</p>
      <h1 className="mt-2 text-xl font-semibold text-neutral-50">Auction not found</h1>
      <p className="mt-2 text-sm text-neutral-400">It may have been removed, or the link is wrong.</p>
      <Link
        href="/"
        className="mt-6 rounded-md border border-neutral-700 px-4 py-2 text-sm text-neutral-200 transition-colors hover:bg-neutral-800"
      >
        Back to auctions
      </Link>
    </div>
  );
}
