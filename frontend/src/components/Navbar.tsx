import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';

export const Navbar: React.FC = () => {
  const navigate = useNavigate();
  const [mobileOpen, setMobileOpen] = useState(false);
  const { isAuthenticated, username, hasRole, logout } = useAuthStore();
  const isAdmin = hasRole('ROLE_ADMIN');

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const navLinks = [
    { to: '/', label: 'Главная', public: true },
    { to: '/search', label: 'Поиск', public: false },
    { to: '/booking', label: 'Бронирование', public: false },
    { to: '/reports', label: 'Отчёты', public: false },
  ];

  const visibleLinks = navLinks.filter((link) => link.public || isAuthenticated);

  return (
    <nav className="bg-blue-700 text-white shadow-md">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="flex h-14 items-center justify-between">
          <Link to="/" className="text-lg font-bold tracking-wide sm:text-xl">
            Авиакасса
          </Link>

          <div className="hidden items-center space-x-6 md:flex">
            {visibleLinks.map((link) => (
              <Link
                key={link.to}
                to={link.to}
                className="rounded-md px-2 py-1 text-sm font-medium transition hover:bg-blue-600"
              >
                {link.label}
              </Link>
            ))}
            {isAuthenticated && (
              <span className="text-xs text-blue-200">
                {username} {isAdmin ? '(admin)' : '(cashier)'}
              </span>
            )}
            {isAuthenticated ? (
              <button
                onClick={handleLogout}
                className="rounded-md bg-red-500 px-3 py-1 text-sm font-medium transition hover:bg-red-600"
              >
                Выйти
              </button>
            ) : (
              <Link
                to="/"
                className="rounded-md bg-green-500 px-3 py-1 text-sm font-medium transition hover:bg-green-600"
              >
                Войти
              </Link>
            )}
          </div>

          <button
            className="md:hidden"
            onClick={() => setMobileOpen((v) => !v)}
            aria-label="Меню"
          >
            <svg className="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              {mobileOpen ? (
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              ) : (
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
              )}
            </svg>
          </button>
        </div>
      </div>

      {mobileOpen && (
        <div className="space-y-1 border-t border-blue-600 px-4 pb-3 pt-2 md:hidden">
          {visibleLinks.map((link) => (
            <Link
              key={link.to}
              to={link.to}
              onClick={() => setMobileOpen(false)}
              className="block rounded-md px-3 py-2 text-base font-medium hover:bg-blue-600"
            >
              {link.label}
            </Link>
          ))}
          {isAuthenticated && (
            <div className="px-3 py-1 text-xs text-blue-200">
              {username} {isAdmin ? '(admin)' : '(cashier)'}
            </div>
          )}
          {isAuthenticated ? (
            <button
              onClick={() => {
                setMobileOpen(false);
                handleLogout();
              }}
              className="block w-full rounded-md px-3 py-2 text-left text-base font-medium text-red-200 hover:bg-blue-600"
            >
              Выйти
            </button>
          ) : (
            <Link
              to="/"
              onClick={() => setMobileOpen(false)}
              className="block rounded-md px-3 py-2 text-base font-medium text-green-200 hover:bg-blue-600"
            >
              Войти
            </Link>
          )}
        </div>
      )}
    </nav>
  );
};
