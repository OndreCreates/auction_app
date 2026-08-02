export default function Loading() {
  return (
    <div className="mx-auto max-w-3xl px-6 py-12">
      <div className="h-4 w-20 animate-pulse rounded bg-neutral-800" />
      <div className="mt-4 h-8 w-64 animate-pulse rounded bg-neutral-800" />
      <div className="mt-8 h-28 animate-pulse rounded-xl border border-neutral-800 bg-neutral-900/50" />
      <div className="mt-8 h-10 w-full animate-pulse rounded-md bg-neutral-800" />
    </div>
  );
}
