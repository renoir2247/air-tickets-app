import { z } from 'zod';

export const passengerSchema = z.object({
  firstName: z
    .string()
    .min(1, 'Имя обязательно')
    .max(50, 'Имя слишком длинное'),
  lastName: z
    .string()
    .min(1, 'Фамилия обязательна')
    .max(50, 'Фамилия слишком длинная'),
  passportNumber: z
    .string()
    .min(5, 'Номер паспорта минимум 5 символов')
    .max(20, 'Номер паспорта слишком длинный')
    .regex(/^[A-Za-z0-9]+$/, 'Только буквы и цифры'),
  dateOfBirth: z
    .string()
    .regex(/^\d{4}-\d{2}-\d{2}$/, 'Формат: ГГГГ-ММ-ДД')
    .refine((val) => {
      const d = new Date(val);
      return !isNaN(d.getTime()) && d < new Date();
    }, 'Некорректная дата рождения'),
});

export const bookingFormSchema = z.object({
  flightId: z.number().min(1, 'Выберите рейс'),
  passengers: z
    .array(passengerSchema)
    .min(1, 'Добавьте хотя бы одного пассажира')
    .max(9, 'Максимум 9 пассажиров'),
});

export type BookingFormData = z.infer<typeof bookingFormSchema>;
export type PassengerFormData = z.infer<typeof passengerSchema>;
