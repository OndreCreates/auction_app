export default function Loading() {
  return (
    <div className="mx-auto max-w-3xl px-6 py-12">
      <div className="h-4 w-20 animate-pulse rounded bg-taupe/15" />
      <div className="mt-4 aspect-[16/9] w-full animate-pulse rounded-xl bg-taupe/10" />
      <div className="mt-6 h-8 w-64 animate-pulse rounded bg-taupe/15" />
      <div className="mt-8 h-28 animate-pulse rounded-xl border border-taupe/15 bg-taupe/5" />
      <div className="mt-8 h-10 w-full animate-pulse rounded-sm bg-taupe/10" />
    </div>
  );
}
