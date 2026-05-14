import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { setToken } from '../api/client';
import { login } from '../api/auth.api';
import { Loader } from '../components/Loader';
import { useAuthStore } from '../store/authStore';
import { useToastStore } from '../store/toastStore';

export const HomePage: React.FC = () => {
  const navigate = useNavigate();
  const setAuth = useAuthStore((s) => s.setAuth);
  const addToast = useToastStore((s) => s.addToast);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const response = await login({ username, password });
      setToken(response.token);

      // Парсим роли из JWT payload
      const payload = JSON.parse(
        atob(response.token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/'))
      );
      const roles: string[] = payload.roles ?? [];
      setAuth(username, roles);

      addToast(`Добро пожаловать, ${username}!`, 'success');
      navigate('/search');
    } catch {
      setError('Неверное имя пользователя или пароль');
      addToast('Неверное имя пользователя или пароль', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mx-auto max-w-md">
      <div className="rounded-lg bg-white p-6 shadow-md sm:p-8">
        <h1 className="mb-1 text-2xl font-bold text-gray-900">Авиакасса</h1>
        <p className="mb-6 text-sm text-gray-500">Войдите для бронирования билетов</p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Логин</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
              placeholder="cashier"
            />
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Пароль</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
              placeholder="cashier123"
            />
          </div>

          {error && (
            <div className="rounded-md bg-red-50 p-3 text-sm text-red-700">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="flex w-full items-center justify-center rounded-md bg-blue-600 px-4 py-2.5 text-sm font-medium text-white transition hover:bg-blue-700 disabled:opacity-60"
          >
            {loading ? <Loader size="sm" /> : 'Войти'}
          </button>
        </form>

        <div className="mt-4 rounded-md bg-gray-50 p-3 text-xs text-gray-600">
          <p className="font-medium">Демо-данные:</p>
          <p>cashier / cashier123 (ROLE_USER)</p>
          <p>admin / admin123 (ROLE_ADMIN)</p>
        </div>
      </div>
    </div>
  );
};
