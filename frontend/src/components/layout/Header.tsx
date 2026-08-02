"use client";

import Link from "next/link";
import { useState } from "react";
import { clearToken, setToken, useAuthToken } from "@/lib/auth";

export function Header() {
  const token = useAuthToken();
  const [draft, setDraft] = useState("");

  return (
    <header className="border-b border-neutral-800">
      <div className="mx-auto flex max-w-5xl items-center justify-between gap-4 px-6 py-5">
        <Link href="/" className="text-lg font-semibold tracking-tight text-neutral-50">
          Auction<span className="text-amber-400">.</span>
        </Link>

        {token ? (
          <div className="flex items-center gap-3 text-sm text-neutral-400">
            <span className="hidden sm:inline">Signed in</span>
            <button
              onClick={clearToken}
              className="rounded-md border border-neutral-700 px-3 py-1.5 text-neutral-200 transition-colors hover:bg-neutral-800"
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
              className="w-40 rounded-md border border-neutral-700 bg-neutral-900 px-3 py-1.5 text-sm text-neutral-200 placeholder:text-neutral-500 focus:border-amber-400 focus:outline-none sm:w-64"
            />
            <button
              type="submit"
              className="rounded-md bg-amber-400 px-3 py-1.5 text-sm font-medium text-neutral-950 transition-colors hover:bg-amber-300"
            >
              Sign in
            </button>
          </form>
        )}
      </div>
    </header>
  );
}
