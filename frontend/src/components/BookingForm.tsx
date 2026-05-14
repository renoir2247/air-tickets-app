import React, { useState } from 'react';
import { FormProvider, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate } from 'react-router-dom';
import { bookingFormSchema, type BookingFormData } from '../schemas/booking.schema';
import { PassengerField } from './PassengerField';
import { Loader } from './Loader';
import { createBooking } from '../api/booking.api';
import { useBookingStore } from '../store/bookingStore';
import { useToastStore } from '../store/toastStore';
import type { FlightDto } from '../types/dto';
import { AxiosError } from 'axios';

interface BookingFormProps {
  selectedFlight: FlightDto | null;
}

export const BookingForm: React.FC<BookingFormProps> = ({ selectedFlight }) => {
  const navigate = useNavigate();
  const setCurrentBooking = useBookingStore((s) => s.setCurrentBooking);
  const addToast = useToastStore((s) => s.addToast);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const methods = useForm<BookingFormData>({
    resolver: zodResolver(bookingFormSchema),
    defaultValues: {
      flightId: selectedFlight?.id ?? 0,
      passengers: [{ firstName: '', lastName: '', passportNumber: '', dateOfBirth: '' }],
    },
    mode: 'onChange',
  });

  const {
    handleSubmit,
    watch,
    setValue,
    formState: { errors, isValid },
  } = methods;

  const passengers = watch('passengers');

  const addPassenger = () => {
    const current = passengers ?? [];
    if (current.length >= 9) return;
    setValue('passengers', [...current, { firstName: '', lastName: '', passportNumber: '', dateOfBirth: '' }]);
  };

  const removePassenger = (index: number) => {
    const current = passengers ?? [];
    if (current.length <= 1) return;
    const updated = current.filter((_, i) => i !== index);
    setValue('passengers', updated);
  };

  const onSubmit = async (data: BookingFormData) => {
    if (!selectedFlight) {
      setError('Сначала выберите рейс на странице поиска');
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const booking = await createBooking({
        flightId: selectedFlight.id,
        passengers: data.passengers,
      });
      setCurrentBooking(booking);
      addToast('Бронирование создано! Резерв действует 15 минут.', 'success');
      navigate(`/payment/${booking.id}`);
    } catch (err) {
      let msg = 'Не удалось создать бронирование';
      if (err instanceof AxiosError) {
        if (err.response?.status === 409) {
          msg = 'Недостаточно свободных мест. Попробуйте другой рейс.';
        } else if (err.response?.status === 400) {
          const data = err.response.data as { message?: string };
          msg = data?.message ?? 'Проверьте введённые данные';
        } else if (err.response?.status === 401) {
          msg = 'Сессия истекла. Войдите заново.';
        }
      } else if (err instanceof Error) {
        msg = err.message;
      }
      setError(msg);
      addToast(msg, 'error');
    } finally {
      setLoading(false);
    }
  };

  if (!selectedFlight) {
    return (
      <div className="rounded-lg border border-yellow-200 bg-yellow-50 p-6 text-center text-yellow-800">
        <p className="mb-2 font-medium">Рейс не выбран</p>
        <p className="text-sm">Перейдите на страницу поиска и выберите рейс для бронирования.</p>
        <button
          onClick={() => navigate('/search')}
          className="mt-4 rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
        >
          Перейти к поиску
        </button>
      </div>
    );
  }

  return (
    <FormProvider {...methods}>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div className="rounded-lg border border-blue-200 bg-blue-50 p-4">
          <h3 className="font-semibold text-blue-900">Выбранный рейс</h3>
          <p className="mt-1 text-sm text-blue-800">
            {selectedFlight.flightNumber}: {selectedFlight.origin} → {selectedFlight.destination}
          </p>
          <p className="text-sm text-blue-800">
            {new Date(selectedFlight.departureTime).toLocaleString('ru-RU')} —{' '}
            {selectedFlight.price.toLocaleString('ru-RU')} ₽
          </p>
        </div>

        <div className="space-y-3">
          {passengers?.map((_, index) => (
            <PassengerField
              key={index}
              index={index}
              onRemove={removePassenger}
              canRemove={(passengers?.length ?? 0) > 1}
            />
          ))}
        </div>

        {errors.passengers && !Array.isArray(errors.passengers) && (
          <p className="text-sm text-red-500">{errors.passengers.message}</p>
        )}

        <button
          type="button"
          onClick={addPassenger}
          disabled={(passengers?.length ?? 0) >= 9}
          className="w-full rounded-md border-2 border-dashed border-gray-300 py-2 text-sm font-medium text-gray-600 transition hover:border-blue-400 hover:text-blue-600 disabled:opacity-50 sm:w-auto sm:px-6"
        >
          + Добавить пассажира ({passengers?.length ?? 0}/9)
        </button>

        {error && (
          <div className="rounded-md bg-red-50 p-3 text-sm text-red-700">
            {error}
          </div>
        )}

        <div className="pt-2">
          <button
            type="submit"
            disabled={loading || !isValid}
            className="flex w-full items-center justify-center rounded-md bg-blue-600 px-6 py-2.5 text-sm font-medium text-white transition hover:bg-blue-700 disabled:opacity-60 sm:w-auto"
          >
            {loading ? <Loader size="sm" /> : 'Забронировать'}
          </button>
        </div>
      </form>
    </FormProvider>
  );
};
