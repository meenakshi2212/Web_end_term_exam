import { NoteType, TagType } from '../types';

const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080/api';

export async function login(username: string, password: string): Promise<any> {
  try {
    const response = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    });
    return response.ok ? response.json() : null;
  } catch (error) {
    return null;
  }
}

export async function signup(payload: any): Promise<any> {
  try {
    const response = await fetch(`${API_BASE}/auth/signup`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    return response.json();
  } catch (error) {
    return { error: 'Network error' };
  }
}

export async function shareNote(noteId: number, username: string): Promise<any> {
  try {
    const response = await fetch(`${API_BASE}/share`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ noteId, username }),
    });
    return response.json();
  } catch (error) {
    return { error: 'Network error' };
  }
}

export async function fetchNotes(ownerId?: number): Promise<NoteType[]> {
  const url = ownerId ? `${API_BASE}/notes?ownerId=${ownerId}` : `${API_BASE}/notes`;
  const response = await fetch(url);
  return response.ok ? response.json() : [];
}

export async function fetchTags(): Promise<TagType[]> {
  const response = await fetch(`${API_BASE}/tags`);
  return response.ok ? response.json() : [];
}

export async function createNote(payload: any): Promise<NoteType | null> {
  const response = await fetch(`${API_BASE}/notes`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  return response.ok ? response.json() : null;
}

export async function updateNote(id: number, payload: any) {
  const response = await fetch(`${API_BASE}/notes/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  return response.ok ? response.json() : null;
}
