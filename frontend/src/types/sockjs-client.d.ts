// src/types/sockjs-client.d.ts

declare module "sockjs-client" {
  import { EventEmitter } from "events";

  interface SockJSEvent {
      data: string;
  }

  interface SockJSCloseEvent {
      code: number;
      reason: string;
      wasClean: boolean;
  }

  class SockJS extends EventEmitter {
      constructor(url: string, _reserved?: unknown, options?: object);
      send(data: string): void;
      close(code?: number, reason?: string): void;
      onopen: () => void;
      onmessage: (e: SockJSEvent) => void;
      onclose: (e: SockJSCloseEvent) => void;
      protocol: string;
      readyState: number;
      extensions: string;
  }

  export = SockJS;
}