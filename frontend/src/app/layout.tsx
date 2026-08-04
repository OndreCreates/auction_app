import type { Metadata } from "next";
import { Montserrat, Playfair_Display } from "next/font/google";
import { Header } from "@/components/layout/Header";
import "./globals.css";

const playfair = Playfair_Display({
  variable: "--font-playfair",
  subsets: ["latin"],
});

const montserrat = Montserrat({
  variable: "--font-montserrat",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Auction House",
  description: "Live auction bidding, built on a Spring Boot backend.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html
      lang="en"
      className={`${playfair.variable} ${montserrat.variable} h-full antialiased`}
    >
      <body className="min-h-full flex flex-col bg-cream text-forest">
        <Header />
        <main className="flex-1">{children}</main>
      </body>
    </html>
  );
}
