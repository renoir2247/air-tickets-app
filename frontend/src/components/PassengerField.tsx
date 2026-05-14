import React from 'react';
import { useFormContext } from 'react-hook-form';
import type { BookingFormData } from '../schemas/booking.schema';

interface PassengerFieldProps {
  index: number;
  onRemove: (index: number) => void;
  canRemove: boolean;
}

export const PassengerField: React.FC<PassengerFieldProps> = ({ index, onRemove, canRemove }) => {
  const {
    register,
    formState: { errors },
  } = useFormContext<BookingFormData>();

  const passengerError = errors.passengers?.[index];

  return (
    <div className="rounded-lg border border-gray-200 bg-gray-50 p-4">
      <div className="mb-3 flex items-center justify-between">
        <h4 className="text-sm font-semibold text-gray-700">Пассажир {index + 1}</h4>
        {canRemove && (
          <button
            type="button"
            onClick={() => onRemove(index)}
            className="text-sm text-red-500 hover:text-red-700"
          >
            Удалить
          </button>
        )}
      </div>

      <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
        <div>
          <label className="mb-1 block text-xs font-medium text-gray-600">Имя</label>
          <input
            {...register(`passengers.${index}.firstName`)}
            type="text"
            placeholder="Иван"
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
          {passengerError?.firstName && (
            <p className="mt-1 text-xs text-red-500">{passengerError.firstName.message}</p>
          )}
        </div>

        <div>
          <label className="mb-1 block text-xs font-medium text-gray-600">Фамилия</label>
          <input
            {...register(`passengers.${index}.lastName`)}
            type="text"
            placeholder="Иванов"
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
          {passengerError?.lastName && (
            <p className="mt-1 text-xs text-red-500">{passengerError.lastName.message}</p>
          )}
        </div>

        <div>
          <label className="mb-1 block text-xs font-medium text-gray-600">Паспорт</label>
          <input
            {...register(`passengers.${index}.passportNumber`)}
            type="text"
            placeholder="1234567890"
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
          {passengerError?.passportNumber && (
            <p className="mt-1 text-xs text-red-500">{passengerError.passportNumber.message}</p>
          )}
        </div>

        <div>
          <label className="mb-1 block text-xs font-medium text-gray-600">Дата рождения</label>
          <input
            {...register(`passengers.${index}.dateOfBirth`)}
            type="date"
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
          {passengerError?.dateOfBirth && (
            <p className="mt-1 text-xs text-red-500">{passengerError.dateOfBirth.message}</p>
          )}
        </div>
      </div>
    </div>
  );
};
