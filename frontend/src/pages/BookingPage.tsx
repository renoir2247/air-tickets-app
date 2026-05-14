import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { BookingForm } from '../components/BookingForm';
import { useBookingStore } from '../store/bookingStore';

function formatTimeLeft(ms: number): string {
  if (ms <= 0) return '00:00';
  const totalSeconds = Math.floor(ms / 1000);
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
}

export const BookingPage: React.FC = () => {
  const navigate = useNavigate();
  const selectedFlight = useBookingStore((s) => s.selectedFlight);
  const currentBooking = useBookingStore((s) => s.currentBooking);
  const [timeLeft, setTimeLeft] = useState<number | null>(null);

  useEffect(() => {
    if (!currentBooking?.expiresAt) {
      setTimeLeft(null);
      return;
    }

    const expires = new Date(currentBooking.expiresAt).getTime();

    const tick = () => {
      const remaining = expires - Date.now();
      if (remaining <= 0) {
        setTimeLeft(0);
        return;
      }
      setTimeLeft(remaining);
    };

    tick();
    const interval = setInterval(tick, 1000);
    return () => clearInterval(interval);
  }, [currentBooking?.expiresAt]);

  return (
    <div className="mx-auto max-w-2xl">
      <div className="mb-4 flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
        <h2 className="text-xl font-bold text-gray-900 sm:text-2xl">Бронирование</h2>
        {timeLeft !== null && (
          <div
            className={`inline-flex items-center rounded-full px-3 py-1 text-sm font-medium ${
              timeLeft < 60000
                ? 'bg-red-100 text-red-700'
                : 'bg-yellow-100 text-yellow-700'
            }`}
          >
            ⏱ Резерв: {formatTimeLeft(timeLeft)}
          </div>
        )}
      </div>

      {timeLeft === 0 && (
        <div className="mb-4 rounded-lg border border-red-200 bg-red-50 p-4 text-red-800">
          <p className="font-medium">Срок резервирования истёк</p>
          <p className="text-sm">Создайте новое бронирование.</p>
          <button
            onClick={() => navigate('/search')}
            className="mt-2 rounded-md bg-red-600 px-4 py-2 text-sm font-medium text-white hover:bg-red-700"
          >
            Найти рейс
          </button>
        </div>
      )}

      <BookingForm selectedFlight={selectedFlight} />
    </div>
  );
};
