import axios from "axios";
import SockJS from "sockjs-client";
import Stomp, { Client, Subscription, Message } from "stompjs";

const VITE_WEBSOCKET_BASE_URL = import.meta.env.VITE_WEBSOCKET_BASE_URL;

interface ChatMessage {
  id:number;
  senderId: string;
  recipientId: string;
  content: string;
  timestamp:string
}

class ChatService {
  private stompClient: Client | null = null;
  private isConnected = false;
  private socket: SockJS | null = null;
  private subscriptions: Map<string, Subscription> = new Map();
  private callbacks: Map<string, (message: ChatMessage) => void> = new Map();
  private serverUrl = `${VITE_WEBSOCKET_BASE_URL}/ws`;
  private connectPromise: Promise<void> | null = null;
  private reconnectInterval = 5000;
  private isReconnecting = false; // Prevent multiple reconnection attempts

  private ensureConnected(): Promise<void> {
    if (!this.isConnected && !this.connectPromise) {
      this.connectPromise = new Promise((resolve) => {
        this.connect(() => {
          resolve();
          this.connectPromise = null;
        });
      });
    }
    return this.connectPromise || Promise.resolve();
  }

  connect(onConnectCallback?: () => void) {
    if (this.isConnected) {
      console.log("Already connected to WebSocket server.");
      return;
    }

    this.socket = new SockJS(this.serverUrl);
    this.stompClient = Stomp.over(this.socket);
    this.stompClient.debug = () => {}; // Override the debug function to suppress logs

    this.stompClient.connect(
      {},
      () => {
        this.isConnected = true;
        this.isReconnecting = false;
        console.log("Connected to WebSocket server");

        if (onConnectCallback) onConnectCallback();

        // Resubscribe to any previously registered destinations
        this.callbacks.forEach((callback, destination) => {
          this.subscribe(destination, callback);
        });
      },
      (error) => {
        console.error("WebSocket connection error:", error);
        this.isConnected = false;
        this.reconnect();
      }
    );

    this.socket.onclose = () => {
      this.isConnected = false;
      console.log("Disconnected from WebSocket server");
      this.reconnect();
    };
  }

  disconnect() {
    if (this.stompClient && this.isConnected) {
      this.stompClient.disconnect(() => {
        this.isConnected = false;
        console.log("Disconnected from WebSocket server");
      });
    }
    if (this.socket) {
      this.socket.close();
    }
    // Clean up all subscriptions on disconnect
    this.subscriptions.clear();
    this.callbacks.clear();
  }

  private reconnect() {
    if (this.isReconnecting) return;
    this.isReconnecting = true;
    setTimeout(() => {
      console.log("Attempting to reconnect to WebSocket...");
      this.connect();
    }, this.reconnectInterval);
  }

  async sendMessage(destination: string, message: ChatMessage): Promise<void> {
    await this.ensureConnected();
    if (this.stompClient && this.isConnected) {
      this.stompClient.send(destination, {}, JSON.stringify(message));
      console.log("Message sent to destination:", destination);
    } else {
      console.warn("Unable to send message. WebSocket is not connected.");
    }
  }

  async subscribe(
    destination: string,
    callback: (message: ChatMessage) => void
  ): Promise<Subscription | undefined> {
    await this.ensureConnected();
    if (this.stompClient && this.isConnected) {
      // Avoid duplicating subscriptions to the same destination
      if (this.subscriptions.has(destination)) {
        console.log(`Already subscribed to ${destination}`);
        return this.subscriptions.get(destination);
      }

      const subscription = this.stompClient.subscribe(destination, (message: Message) => {
        callback(JSON.parse(message.body) as ChatMessage);
      });
      this.subscriptions.set(destination, subscription);
      this.callbacks.set(destination, callback);
      console.log(`Subscribed to ${destination}`);
      return subscription;
    } else {
      console.warn("WebSocket is not connected. Subscription deferred.");
      this.callbacks.set(destination, callback); // Store callback for future connection
      return undefined;
    }
  }

  unsubscribe(destination: string) {
    const subscription = this.subscriptions.get(destination);
    if (subscription) {
      subscription.unsubscribe();
      this.subscriptions.delete(destination);
      this.callbacks.delete(destination);
      console.log(`Unsubscribed from ${destination}`);
    } else {
      console.warn(`No subscription found for ${destination}`);
    }
  }

  async getRecipientsBySender(senderId: string): Promise<string[]> {
    try {
      const response = await axios.get(`${VITE_WEBSOCKET_BASE_URL}/recipients/${senderId}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching recipients for sender ${senderId}:`, error);
      return [];
    }
  }

  async getUserProfile(email: string): Promise<{ firstName: string; lastName: string; profileImageURL: string }> {
    try {
      const response = await axios.get(`${VITE_WEBSOCKET_BASE_URL}/api/users/${email}`);
      const { firstName, lastName, profileImageURL } = response.data.data;
      if (!firstName || !lastName || profileImageURL === undefined) {
        throw new Error('Invalid API response structure');
      }
      return { firstName, lastName, profileImageURL };
    } catch (error) {
      console.error(`Error fetching user profile for ${email}:`, error);
      return { firstName: '', lastName: '', profileImageURL: '' };
    }
  }

  async checkChatExists(senderId: string, recipientId: string): Promise<boolean> {
    try {
      const response = await axios.get(`${VITE_WEBSOCKET_BASE_URL}/exists/${senderId}/${recipientId}`);
      return response.data;
    } catch (error) {
      console.error(`Error checking chat existence for ${senderId} and ${recipientId}:`, error);
      return false
    }
  }

  async getChatMessages(senderId: string, recipientId: string): Promise<ChatMessage[]> {
    try {
      const response = await axios.get(`${VITE_WEBSOCKET_BASE_URL}/messages/${senderId}/${recipientId}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching chat messages between ${senderId} and ${recipientId}:`, error);
      return [];
    }
  }
}

const chatService = new ChatService();
export default chatService;