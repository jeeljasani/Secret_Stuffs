// src/types/stompjs.d.ts

declare module "stompjs" {
  export interface Message {
      body: string;
      ack: () => void;
      nack: () => void;
  }

  export interface Subscription {
      unsubscribe: () => void;
  }

  export interface Client {
      debug: () => void;
      connect: (
          headers: Record<string, string>,
          onConnect: () => void,
          onError?: (error: string) => void
      ) => void;
      disconnect: (callback: () => void) => void;
      subscribe: (destination: string, callback: (message: Message) => void) => Subscription;
      unsubscribe: (id: string) => void;
      send: (destination: string, headers?: Record<string, string>, body?: string) => void;
  }

  export function over(socket: WebSocket | SockJS): Client;
}