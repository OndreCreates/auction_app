import { notFound } from "next/navigation";
import { AuctionDetail } from "@/components/auctions/AuctionDetail";
import { ApiError, getAuction } from "@/features/auctions/api";

export default async function AuctionDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  const auctionId = Number(id);

  try {
    const auction = await getAuction(auctionId);
    return <AuctionDetail initialAuction={auction} />;
  } catch (error) {
    if (error instanceof ApiError && error.status === 404) {
      notFound();
    }
    throw error;
  }
}
