export interface Invoice {
  id: number;
  reservationId: number;
  roomCharges: number;
  extraCharges: number;
  discountAmount: number;
  taxAmount: number;
  totalAmount: number;
  paidAmount: number;
  balanceDue: number;
  paymentMode: PaymentMode;
  paymentStatus: PaymentStatus;
  notes?: string;
  generatedAt: number;
}

export enum PaymentMode {
  CASH = 'CASH',
  CARD = 'CARD',
  UPI = 'UPI',
  SPLIT = 'SPLIT'
}

export enum PaymentStatus {
  UNPAID = 'UNPAID',
  PARTIAL = 'PARTIAL',
  PAID = 'PAID',
  REFUNDED = 'REFUNDED'
}
