import React, { useEffect, useRef, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { initPayment, sendWebhook } from '../api/payment.api';
import { getBookingById, getBookingStatus } from '../api/booking.api';
import { Loader } from '../components/Loader';
import { useToastStore } from '../store/toastStore';
import { useBookingStore } from '../store/bookingStore';
import { PaymentMethod, PaymentStatus } from '../types/dto';
import type { BookingDTO } from '../types/dto';
import { AxiosError } from 'axios';

function formatCardNumber(value: string): string {
  const v = value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
  const parts: string[] = [];
  for (let i = 0; i < v.length; i += 4) {
    parts.push(v.substring(i, i + 4));
  }
  return parts.join(' ');
}

function validateCardNumber(val: string): boolean {
  const digits = val.replace(/\s/g, '');
  return /^\d{16}$/.test(digits);
}

function validateExpiry(val: string): boolean {
  return /^(0[1-9]|1[0-2])\s\/\s\d{2}$/.test(val);
}

function validateCvv(val: string): boolean {
  return /^\d{3}$/.test(val);
}

function validateEmail(val: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val);
}

export const PaymentPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const addToast = useToastStore((s) => s.addToast);
  const clearBooking = useBookingStore((s) => s.clearBooking);
  const [booking, setBooking] = useState<BookingDTO | null>(null);
  const [method, setMethod] = useState<PaymentMethod>(PaymentMethod.CREDIT_CARD);
  const [loading, setLoading] = useState(false);
  const [polling, setPolling] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const [cardNumber, setCardNumber] = useState('');
  const [expiry, setExpiry] = useState('');
  const [cvv, setCvv] = useState('');
  const [email, setEmail] = useState('');

  const pollRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const bookingId = Number(id);

  useEffect(() => {
    const fetchBooking = async () => {
      try {
        const b = await getBookingById(bookingId);
        setBooking(b);
        if (b.status === 'CONFIRMED') {
          setSuccess(true);
        }
      } catch {
        setError('Бронирование не найдено');
      }
    };
    fetchBooking();

    return () => {
      if (pollRef.current) clearInterval(pollRef.current);
    };
  }, [bookingId]);

  const startPolling = () => {
    if (pollRef.current) clearInterval(pollRef.current);
    setPolling(true);
    pollRef.current = setInterval(async () => {
      try {
        const status = await getBookingStatus(bookingId);
        if (status.status === 'CONFIRMED') {
          if (pollRef.current) clearInterval(pollRef.current);
          setPolling(false);
          setSuccess(true);
          const b = await getBookingById(bookingId);
          setBooking(b);
          addToast('Оплата прошла успешно! Билеты сформированы.', 'success');
        } else if (status.status === 'CANCELLED') {
          if (pollRef.current) clearInterval(pollRef.current);
          setPolling(false);
          setError('Бронирование было отменено');
        }
      } catch {
        // игнорируем ошибки polling
      }
    }, 2000);
  };

  const handlePay = async () => {
    setError(null);

    if (!validateCardNumber(cardNumber)) {
      setError('Введите корректный номер карты (16 цифр)');
      return;
    }
    if (!validateExpiry(expiry)) {
      setError('Введите корректный срок действия (MM / YY)');
      return;
    }
    if (!validateCvv(cvv)) {
      setError('Введите корректный CVV/CVC (3 цифры)');
      return;
    }
    if (!validateEmail(email)) {
      setError('Введите корректный email');
      return;
    }

    setLoading(true);

    // Эмуляция процессинга на фронтенде
    setTimeout(async () => {
      const cleanNumber = cardNumber.replace(/\s/g, '');
      // Тестовая карта для имитации ошибки
      if (cleanNumber === '4000000000000002') {
        setLoading(false);
        setError('Оплата не удалась. Банк отклонил транзакцию. Попробуйте другую карту.');
        addToast('Оплата не удалась: банк отклонил транзакцию', 'error');
        return;
      }

      try {
        const payment = await initPayment({ bookingId, paymentMethod: method });
        await sendWebhook({ transactionId: payment.transactionId, status: PaymentStatus.PAID });
        startPolling();
      } catch (err) {
        let msg = 'Ошибка при обработке платежа';
        if (err instanceof AxiosError && err.response?.data) {
          const data = err.response.data as { message?: string };
          msg = data.message ?? msg;
        } else if (err instanceof Error) {
          msg = err.message;
        }
        setError(msg);
        addToast(msg, 'error');
        setLoading(false);
      }
    }, 1500);
  };

  if (success && booking) {
    return (
      <div className="mx-auto max-w-lg space-y-4">
        <div className="rounded-lg border border-green-200 bg-green-50 p-6 text-center">
          <div className="mb-2 text-4xl">✓</div>
          <h2 className="text-xl font-bold text-green-900">Оплата прошла успешно!</h2>
          <p className="mt-1 text-sm text-green-800">
            Бронирование <span className="font-mono font-medium">{booking.bookingReference}</span> подтверждено.
          </p>
        </div>

        <div className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm">
          <h3 className="mb-3 font-semibold text-gray-900">Билеты</h3>
          {booking.tickets && booking.tickets.length > 0 ? (
            <div className="space-y-2">
              {booking.tickets.map((ticket) => (
                <div
                  key={ticket.id}
                  className="flex items-center justify-between rounded-md border border-gray-100 bg-gray-50 px-3 py-2"
                >
                  <div>
                    <p className="text-sm font-medium text-gray-900">{ticket.passengerName}</p>
                    <p className="text-xs text-gray-500">{ticket.seatNumber ? `Место: ${ticket.seatNumber}` : 'Место будет назначено при регистрации'}</p>
                  </div>
                  <span className="font-mono text-xs text-gray-600">{ticket.ticketNumber}</span>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-sm text-gray-500">Билеты формируются...</p>
          )}
        </div>

        <button
          onClick={() => {
            clearBooking();
            navigate('/search');
          }}
          className="w-full rounded-md bg-blue-600 px-4 py-2.5 text-sm font-medium text-white transition hover:bg-blue-700"
        >
          Новое бронирование
        </button>
      </div>
    );
  }

  if (!booking) {
    return (
      <div className="rounded-lg border border-yellow-200 bg-yellow-50 p-6 text-center text-yellow-800">
        <p className="font-medium">Бронирование не найдено</p>
        <button
          onClick={() => navigate('/search')}
          className="mt-4 rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
        >
          Найти рейс
        </button>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-lg space-y-4">
      <h2 className="text-xl font-bold text-gray-900 sm:text-2xl">Оплата бронирования</h2>

      <div className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm">
        <div className="flex flex-col gap-1 text-sm text-gray-700 sm:flex-row sm:justify-between">
          <span>Бронирование:</span>
          <span className="font-mono font-medium">{booking.bookingReference}</span>
        </div>
        <div className="flex flex-col gap-1 text-sm text-gray-700 sm:flex-row sm:justify-between">
          <span>Сумма:</span>
          <span className="font-medium">{Number(booking.totalAmount).toLocaleString('ru-RU')} ₽</span>
        </div>
        <div className="flex flex-col gap-1 text-sm text-gray-700 sm:flex-row sm:justify-between">
          <span>Статус:</span>
          <span
            className={`inline-block rounded-full px-2 py-0.5 text-xs font-medium ${
              booking.status === 'PENDING'
                ? 'bg-yellow-100 text-yellow-700'
                : booking.status === 'CONFIRMED'
                ? 'bg-green-100 text-green-700'
                : 'bg-red-100 text-red-700'
            }`}
          >
            {booking.status}
          </span>
        </div>
      </div>

      <div className="space-y-4 rounded-lg border border-gray-200 bg-white p-4 shadow-sm">
        <div>
          <label className="mb-1 block text-sm font-medium text-gray-700">Способ оплаты</label>
          <select
            value={method}
            onChange={(e) => setMethod(e.target.value as PaymentMethod)}
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          >
            <option value={PaymentMethod.CREDIT_CARD}>Банковская карта</option>
            <option value={PaymentMethod.DEBIT_CARD}>Дебетовая карта</option>
            <option value={PaymentMethod.BANK_TRANSFER}>Банковский перевод</option>
          </select>
        </div>

        <div>
          <label className="mb-1 block text-sm font-medium text-gray-700">Номер карты</label>
          <input
            type="text"
            inputMode="numeric"
            maxLength={19}
            value={cardNumber}
            onChange={(e) => setCardNumber(formatCardNumber(e.target.value))}
            placeholder="0000 0000 0000 0000"
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Срок действия</label>
            <input
              type="text"
              inputMode="numeric"
              maxLength={7}
              value={expiry}
              onChange={(e) => {
                let val = e.target.value.replace(/\D/g, '');
                if (val.length >= 2) {
                  val = val.slice(0, 2) + ' / ' + val.slice(2, 4);
                }
                setExpiry(val);
              }}
              placeholder="MM / YY"
              className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">CVV2 / CVC2</label>
            <input
              type="password"
              inputMode="numeric"
              maxLength={3}
              value={cvv}
              onChange={(e) => setCvv(e.target.value.replace(/\D/g, ''))}
              placeholder="123"
              className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
            />
          </div>
        </div>

        <div>
          <label className="mb-1 block text-sm font-medium text-gray-700">Email для чека</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="example@mail.ru"
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
        </div>

        <button
          onClick={handlePay}
          disabled={loading}
          className="flex w-full items-center justify-center rounded-md bg-blue-600 px-4 py-2.5 text-sm font-medium text-white transition hover:bg-blue-700 disabled:opacity-60"
        >
          {loading ? (
            <>
              <Loader size="sm" />
              <span className="ml-2">Обработка платежа...</span>
            </>
          ) : (
            'Оплатить'
          )}
        </button>

        {polling && (
          <div className="flex items-center gap-2 text-sm text-blue-700">
            <Loader size="sm" />
            Ожидание подтверждения платежа...
          </div>
        )}
      </div>

      {error && (
        <div className="rounded-md bg-red-50 p-3 text-sm text-red-700">{error}</div>
      )}
    </div>
  );
};
