import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { getToken, clearToken } from '../api/client';

interface AuthState {
  username: string | null;
  roles: string[];
  isAuthenticated: boolean;
  setAuth: (username: string, roles: string[]) => void;
  logout: () => void;
  hasRole: (role: string) => boolean;
}

function parseJwt(token: string): { sub?: string; roles?: string[] } | null {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch {
    return null;
  }
}

function initState(): { username: string | null; roles: string[]; isAuthenticated: boolean } {
  const token = getToken();
  if (!token) return { username: null, roles: [], isAuthenticated: false };
  const payload = parseJwt(token);
  if (!payload) return { username: null, roles: [], isAuthenticated: false };
  return {
    username: payload.sub ?? null,
    roles: payload.roles ?? [],
    isAuthenticated: true,
  };
}

const initial = initState();

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      username: initial.username,
      roles: initial.roles,
      isAuthenticated: initial.isAuthenticated,
      setAuth: (username, roles) => set({ username, roles, isAuthenticated: true }),
      logout: () => {
        clearToken();
        set({ username: null, roles: [], isAuthenticated: false });
      },
      hasRole: (role) => get().roles.includes(role),
    }),
    {
      name: 'aviacassa-auth',
      storage: {
        getItem: (name) => {
          const str = localStorage.getItem(name);
          return str ? JSON.parse(str) : null;
        },
        setItem: (name, value) => localStorage.setItem(name, JSON.stringify(value)),
        removeItem: (name) => localStorage.removeItem(name),
      },
    }
  )
);
