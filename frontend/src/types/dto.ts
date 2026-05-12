export enum BookingStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  CANCELLED = 'CANCELLED',
}

export enum PaymentMethod {
  CREDIT_CARD = 'CREDIT_CARD',
  DEBIT_CARD = 'DEBIT_CARD',
  BANK_TRANSFER = 'BANK_TRANSFER',
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  FAILED = 'FAILED',
}

export enum FlightStatus {
  SCHEDULED = 'SCHEDULED',
  DELAYED = 'DELAYED',
  CANCELLED = 'CANCELLED',
  COMPLETED = 'COMPLETED',
}

export interface PassengerDto {
  firstName: string;
  lastName: string;
  passportNumber: string;
  dateOfBirth: string; // yyyy-MM-dd
}

export interface PassengerRequest {
  firstName: string;
  lastName: string;
  passportNumber: string;
  dateOfBirth: string; // yyyy-MM-dd
}

export interface BookingRequest {
  flightId: number;
  passengers: PassengerRequest[];
}

export interface BookingDTO {
  id: number;
  bookingReference: string;
  status: BookingStatus;
  totalAmount: number;
  createdAt: string;
  expiresAt: string;
  flightId: number;
  flightNumber: string;
  passengerCount: number;
  passengers?: PassengerDTO[];
  tickets?: TicketDTO[];
}

export interface PassengerDTO {
  id: number;
  firstName: string;
  lastName: string;
  passportNumber: string;
  dateOfBirth: string;
  seatNumber?: string;
}

export interface TicketDTO {
  id: number;
  ticketNumber: string;
  passengerName: string;
  seatNumber: string;
  status: string;
}

export interface PaymentDTO {
  id: number;
  transactionId: string;
  amount: number;
  paymentMethod: PaymentMethod;
  status: PaymentStatus;
  createdAt: string;
}

export interface PaymentInitRequest {
  bookingId: number;
  paymentMethod: PaymentMethod;
}

export interface PaymentWebhookDTO {
  transactionId: string;
  status: PaymentStatus;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
}

export interface FlightDto {
  id: number;
  flightNumber: string;
  origin: string;
  destination: string;
  departureTime: string;
  arrivalTime: string;
  availableSeats: number;
  price: number;
  status: FlightStatus;
}

export interface BookingStatusDTO {
  id: number;
  status: string;
  expiresAt: string;
}

export interface ReportDTO {
  from: string;
  to: string;
  totalBookings: number;
  confirmedBookings: number;
  pendingBookings: number;
  cancelledBookings: number;
  totalRevenue: number;
}
