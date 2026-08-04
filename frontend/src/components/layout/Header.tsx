"use client";

import Link from "next/link";
import { useState } from "react";
import { clearToken, setToken, useAuthToken } from "@/lib/auth";
import { Crest } from "./Crest";

const NAV_LINKS = [
  { href: "/", label: "Auctions" },
  { href: "/watchlist", label: "Watchlist" },
  { href: "/profile", label: "Profile" },
];

export function Header() {
  const token = useAuthToken();
  const [draft, setDraft] = useState("");

  return (
    <header className="bg-forest text-cream">
      <div className="mx-auto flex max-w-6xl flex-wrap items-center justify-between gap-4 px-6 py-4">
        <Link href="/" className="flex items-center gap-2.5 text-gold">
          <Crest className="h-7 w-7" />
          <span className="font-serif text-xl tracking-[0.15em] text-cream uppercase">Auction</span>
        </Link>

        <nav className="flex items-center gap-6 text-sm tracking-wide text-cream/70">
          {NAV_LINKS.map((link) => (
            <Link key={link.href} href={link.href} className="transition-colors hover:text-gold">
              {link.label}
            </Link>
          ))}
        </nav>

        {token ? (
          <div className="flex items-center gap-3 text-sm text-cream/70">
            <span className="hidden sm:inline">Signed in</span>
            <button
              onClick={clearToken}
              className="rounded-sm border border-cream/25 px-3 py-1.5 text-cream transition-colors hover:border-gold hover:text-gold"
            >
              Sign out
            </button>
          </div>
        ) : (
          <form
            onSubmit={(e) => {
              e.preventDefault();
              const value = draft.trim();
              if (value) {
                setToken(value);
                setDraft("");
              }
            }}
            className="flex items-center gap-2"
          >
            <input
              value={draft}
              onChange={(e) => setDraft(e.target.value)}
              placeholder="Paste access token to bid"
              className="w-40 rounded-sm border border-cream/25 bg-forest-light px-3 py-1.5 text-sm text-cream placeholder:text-cream/40 focus:border-gold focus:outline-none sm:w-64"
            />
            <button
              type="submit"
              className="rounded-sm bg-gold px-3 py-1.5 text-sm font-medium text-forest transition-colors hover:bg-gold-dark"
            >
              Sign in
            </button>
          </form>
        )}
      </div>
    </header>
  );
}
