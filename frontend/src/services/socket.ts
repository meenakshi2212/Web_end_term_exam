import { Client, Message, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const SOCKET_URL = import.meta.env.VITE_API_BASE ? import.meta.env.VITE_API_BASE.replace('/api', '') : 'http://localhost:8080';
let client: Client | null = null;

export function connectRealtime(onMessage: (payload: any) => void): (() => void) {
  if (client && client.active) {
    return () => client?.deactivate();
  }
  client = new Client({
    webSocketFactory: () => new SockJS(`${SOCKET_URL}/ws/notes`),
    reconnectDelay: 5000,
    debug: () => {},
  });

  let subscription: StompSubscription | null = null;
  client.onConnect = () => {
    subscription = client!.subscribe('/topic/notes', (message: Message) => {
      const body = message.body ? JSON.parse(message.body) : {};
      onMessage(body);
    });
  };
  client.activate();

  return () => {
    subscription?.unsubscribe();
    client?.deactivate();
  };
}

export function sendNoteUpdate(payload: any) {
  if (client?.active) {
    client.publish({ destination: '/app/notes/update', body: JSON.stringify(payload) });
  }
}
