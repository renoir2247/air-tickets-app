import React from 'react';
import { Outlet } from 'react-router-dom';
import { Navbar } from './Navbar';
import { ErrorBoundary } from './ErrorBoundary';
import { ToastContainer } from './ToastContainer';

export const Layout: React.FC = () => {
  return (
    <div className="flex min-h-screen flex-col bg-gray-50">
      <Navbar />
      <main className="mx-auto w-full max-w-7xl flex-1 px-4 py-6 sm:px-6 lg:px-8">
        <ErrorBoundary>
          <Outlet />
        </ErrorBoundary>
      </main>
      <ToastContainer />
      <footer className="bg-white py-4 text-center text-sm text-gray-500 shadow-inner">
        Авиакасса © {new Date().getFullYear()}
      </footer>
    </div>
  );
};
