"use client";

import { useEffect, useRef } from "react";
import { Client, type IMessage } from "@stomp/stompjs";
import { WS_URL } from "@/lib/config";
import type { AuctionClosedMessage, Bid } from "./types";

function isAuctionClosedMessage(payload: unknown): payload is AuctionClosedMessage {
  return typeof payload === "object" && payload !== null && "finalPrice" in payload;
}

interface UseAuctionSocketOptions {
  onBid: (bid: Bid) => void;
  onClosed: (message: AuctionClosedMessage) => void;
}

// Subscribes to the auction's STOMP topic for the lifetime of the component.
// Callbacks are read through refs so passing a fresh inline function each render
// doesn't tear down and reopen the WebSocket connection.
export function useAuctionSocket(auctionId: number, { onBid, onClosed }: UseAuctionSocketOptions) {
  const onBidRef = useRef(onBid);
  const onClosedRef = useRef(onClosed);

  useEffect(() => {
    onBidRef.current = onBid;
    onClosedRef.current = onClosed;
  });

  useEffect(() => {
    const client = new Client({
      brokerURL: WS_URL,
      reconnectDelay: 5000,
    });

    client.onConnect = () => {
      client.subscribe(`/topic/auctions/${auctionId}`, (message: IMessage) => {
        const payload = JSON.parse(message.body);
        if (isAuctionClosedMessage(payload)) {
          onClosedRef.current(payload);
        } else {
          onBidRef.current(payload as Bid);
        }
      });
    };

    client.activate();

    return () => {
      client.deactivate();
    };
  }, [auctionId]);
}
