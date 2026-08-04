export default function Loading() {
  return (
    <div className="mx-auto max-w-6xl px-6 py-12">
      <div className="h-7 w-40 animate-pulse rounded bg-taupe/15" />
      <div className="mt-2 h-4 w-20 animate-pulse rounded bg-taupe/15" />

      <div className="mt-8 grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
        {Array.from({ length: 6 }).map((_, i) => (
          <div key={i} className="h-36 animate-pulse rounded-xl border border-taupe/15 bg-taupe/5" />
        ))}
      </div>
    </div>
  );
}
