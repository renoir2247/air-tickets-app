import React, { useEffect, useState } from 'react';
import { getBookings } from '../api/booking.api';
import { getSalesReport } from '../api/report.api';
import { Loader } from '../components/Loader';
import { useAuthStore } from '../store/authStore';
import { useToastStore } from '../store/toastStore';
import type { BookingDTO, ReportDTO } from '../types/dto';
import { AxiosError } from 'axios';

export const ReportsPage: React.FC = () => {
  const hasRole = useAuthStore((s) => s.hasRole);
  const addToast = useToastStore((s) => s.addToast);
  const isAdmin = hasRole('ROLE_ADMIN');

  const [bookings, setBookings] = useState<BookingDTO[]>([]);
  const [report, setReport] = useState<ReportDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [from, setFrom] = useState('');
  const [to, setTo] = useState('');

  useEffect(() => {
    const fetch = async () => {
      try {
        const data = await getBookings();
        setBookings(data);
      } catch {
        setError('Не удалось загрузить бронирования');
      } finally {
        setLoading(false);
      }
    };
    fetch();
  }, []);

  const loadReport = async () => {
    if (!from || !to) {
      addToast('Укажите период', 'warning');
      return;
    }
    setLoading(true);
    try {
      const data = await getSalesReport(from, to);
      setReport(data);
    } catch (err) {
      let msg = 'Не удалось загрузить отчёт';
      if (err instanceof AxiosError && err.response?.status === 403) {
        msg = 'Доступ запрещён: только для администраторов';
      } else if (err instanceof AxiosError && err.response?.data) {
        const d = err.response.data as { message?: string };
        msg = d.message ?? msg;
      }
      setError(msg);
      addToast(msg, 'error');
    } finally {
      setLoading(false);
    }
  };

  const totalAmount = bookings.reduce((sum, b) => sum + Number(b.totalAmount), 0);
  const confirmedCount = bookings.filter((b) => b.status === 'CONFIRMED').length;
  const pendingCount = bookings.filter((b) => b.status === 'PENDING').length;

  return (
    <div className="space-y-4">
      <h2 className="text-xl font-bold text-gray-900 sm:text-2xl">Отчёты</h2>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <div className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm">
          <p className="text-sm text-gray-500">Всего бронирований</p>
          <p className="text-2xl font-bold text-gray-900">{bookings.length}</p>
        </div>
        <div className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm">
          <p className="text-sm text-gray-500">Подтверждено</p>
          <p className="text-2xl font-bold text-green-700">{confirmedCount}</p>
        </div>
        <div className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm">
          <p className="text-sm text-gray-500">Ожидает оплаты</p>
          <p className="text-2xl font-bold text-yellow-700">{pendingCount}</p>
        </div>
        <div className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm">
          <p className="text-sm text-gray-500">Общая выручка</p>
          <p className="text-2xl font-bold text-blue-700">{totalAmount.toLocaleString('ru-RU')} ₽</p>
        </div>
      </div>

      {isAdmin && (
        <div className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm">
          <h3 className="mb-3 font-semibold text-gray-900">Отчёт о продажах (админ)</h3>
          <div className="grid gap-3 sm:grid-cols-3">
            <div>
              <label className="mb-1 block text-xs font-medium text-gray-600">С</label>
              <input
                type="date"
                value={from}
                onChange={(e) => setFrom(e.target.value)}
                className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="mb-1 block text-xs font-medium text-gray-600">По</label>
              <input
                type="date"
                value={to}
                onChange={(e) => setTo(e.target.value)}
                className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
              />
            </div>
            <div className="flex items-end">
              <button
                onClick={loadReport}
                disabled={loading}
                className="flex w-full items-center justify-center rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-blue-700 disabled:opacity-60"
              >
                {loading ? <Loader size="sm" /> : 'Сформировать'}
              </button>
            </div>
          </div>

          {report && (
            <div className="mt-4 grid gap-3 sm:grid-cols-3">
              <div className="rounded-md bg-gray-50 p-3">
                <p className="text-xs text-gray-500">Всего за период</p>
                <p className="text-lg font-bold">{report.totalBookings}</p>
              </div>
              <div className="rounded-md bg-gray-50 p-3">
                <p className="text-xs text-gray-500">Подтверждено</p>
                <p className="text-lg font-bold text-green-700">{report.confirmedBookings}</p>
              </div>
              <div className="rounded-md bg-gray-50 p-3">
                <p className="text-xs text-gray-500">Выручка за период</p>
                <p className="text-lg font-bold text-blue-700">
                  {Number(report.totalRevenue).toLocaleString('ru-RU')} ₽
                </p>
              </div>
            </div>
          )}
        </div>
      )}

      {!isAdmin && (
        <div className="rounded-lg border border-yellow-200 bg-yellow-50 p-4 text-sm text-yellow-800">
          Расширенный отчёт по продажам доступен только администраторам.
        </div>
      )}

      {loading && <Loader className="py-12" />}

      {error && (
        <div className="rounded-md bg-red-50 p-4 text-sm text-red-700">{error}</div>
      )}

      {!loading && !error && (
        <div className="overflow-x-auto rounded-lg border border-gray-200 bg-white shadow-sm">
          <table className="min-w-full text-left text-sm">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 font-medium text-gray-700">№ брони</th>
                <th className="px-4 py-3 font-medium text-gray-700">Рейс</th>
                <th className="px-4 py-3 font-medium text-gray-700">Пассажиров</th>
                <th className="px-4 py-3 font-medium text-gray-700">Сумма</th>
                <th className="px-4 py-3 font-medium text-gray-700">Статус</th>
                <th className="px-4 py-3 font-medium text-gray-700">Создано</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {bookings.map((b) => (
                <tr key={b.id} className="hover:bg-gray-50">
                  <td className="px-4 py-3 font-mono">{b.bookingReference}</td>
                  <td className="px-4 py-3">{b.flightNumber}</td>
                  <td className="px-4 py-3">{b.passengerCount}</td>
                  <td className="px-4 py-3">{Number(b.totalAmount).toLocaleString('ru-RU')} ₽</td>
                  <td className="px-4 py-3">
                    <span
                      className={`inline-block rounded-full px-2 py-0.5 text-xs font-medium ${
                        b.status === 'CONFIRMED'
                          ? 'bg-green-100 text-green-700'
                          : b.status === 'PENDING'
                          ? 'bg-yellow-100 text-yellow-700'
                          : 'bg-red-100 text-red-700'
                      }`}
                    >
                      {b.status}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-gray-500">
                    {new Date(b.createdAt).toLocaleDateString('ru-RU')}
                  </td>
                </tr>
              ))}
              {bookings.length === 0 && (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-gray-500">
                    Бронирований пока нет
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};
