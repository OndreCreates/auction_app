export function Crest({ className }: { className?: string }) {
  return (
    <svg
      viewBox="0 0 32 32"
      fill="none"
      className={className}
      aria-hidden="true"
    >
      <path
        d="M6 6c3 2 4.5 6 4.5 12s-1.5 10-4.5 12"
        stroke="currentColor"
        strokeWidth="1.1"
        strokeLinecap="round"
      />
      <path d="M7 8c2 1 3 2.5 3 4.5" stroke="currentColor" strokeWidth="0.9" strokeLinecap="round" />
      <path d="M6.5 13c2.2 0.6 3.4 2 3.6 4" stroke="currentColor" strokeWidth="0.9" strokeLinecap="round" />
      <path d="M6.5 18c2.2 0.6 3.6 2 4 4" stroke="currentColor" strokeWidth="0.9" strokeLinecap="round" />
      <path d="M7.5 23c1.8 0.5 3 1.6 3.5 3" stroke="currentColor" strokeWidth="0.9" strokeLinecap="round" />

      <path
        d="M26 6c-3 2-4.5 6-4.5 12s1.5 10 4.5 12"
        stroke="currentColor"
        strokeWidth="1.1"
        strokeLinecap="round"
      />
      <path d="M25 8c-2 1-3 2.5-3 4.5" stroke="currentColor" strokeWidth="0.9" strokeLinecap="round" />
      <path d="M25.5 13c-2.2 0.6-3.4 2-3.6 4" stroke="currentColor" strokeWidth="0.9" strokeLinecap="round" />
      <path d="M25.5 18c-2.2 0.6-3.6 2-4 4" stroke="currentColor" strokeWidth="0.9" strokeLinecap="round" />
      <path d="M24.5 23c-1.8 0.5-3 1.6-3.5 3" stroke="currentColor" strokeWidth="0.9" strokeLinecap="round" />

      <circle cx="16" cy="16" r="2.2" stroke="currentColor" strokeWidth="1" />
    </svg>
  );
}
