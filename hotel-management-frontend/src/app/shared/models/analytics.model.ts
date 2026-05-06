export interface OccupancyData {
  period: string;
  occupancyRate: number;
  totalRooms: number;
  occupiedRooms: number;
}

export interface RevenueData {
  month: string;
  year: number;
  totalRevenue: number;
  revenueByRoomType: { [key: string]: number };
  revenueByPaymentMode: { [key: string]: number };
}

export interface KpiData {
  averageDailyRate: number;
  revPar: number;
  averageStayDuration: number;
  occupancyRate: number;
  totalBookings: number;
  totalCheckIns: number;
  totalCheckOuts: number;
}

export interface TrendData {
  labels: string[];
  occupancyRates: number[];
  revenues: number[];
}

export interface BookingSourceData {
  source: string;
  count: number;
  percentage: number;
}
