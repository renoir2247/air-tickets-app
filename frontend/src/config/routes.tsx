import React from 'react';
import { createBrowserRouter, Navigate, Outlet } from 'react-router-dom';
import { Layout } from '../components/Layout';
import { HomePage } from '../pages/HomePage';
import { SearchPage } from '../pages/SearchPage';
import { BookingPage } from '../pages/BookingPage';
import { PaymentPage } from '../pages/PaymentPage';
import { ReportsPage } from '../pages/ReportsPage';
import { getToken } from '../api/client';

const ProtectedRoute: React.FC = () => {
  const token = getToken();
  return token ? <Outlet /> : <Navigate to="/" replace />;
};

export const router = createBrowserRouter([
  {
    path: '/',
    element: <Layout />,
    children: [
      { index: true, element: <HomePage /> },
      {
        element: <ProtectedRoute />,
        children: [
          { path: 'search', element: <SearchPage /> },
          { path: 'booking', element: <BookingPage /> },
          { path: 'payment/:id', element: <PaymentPage /> },
          { path: 'reports', element: <ReportsPage /> },
        ],
      },
    ],
  },
]);
