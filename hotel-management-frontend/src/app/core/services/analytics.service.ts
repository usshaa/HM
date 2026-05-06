import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '@shared/models/auth.model';
import { OccupancyData, RevenueData, KpiData, TrendData, BookingSourceData } from '@shared/models/analytics.model';

@Injectable({ providedIn: 'root' })
export class AnalyticsService {
  private apiUrl = 'http://localhost:8080/api/analytics';

  constructor(private http: HttpClient) {}

  getOccupancy(period: string = 'daily'): Observable<ApiResponse<OccupancyData[]>> {
    return this.http.get<ApiResponse<OccupancyData[]>>(`${this.apiUrl}/occupancy?period=${period}`);
  }

  getRevenue(month?: number, year?: number): Observable<ApiResponse<RevenueData>> {
    let params = new HttpParams();
    if (month) params = params.set('month', month.toString());
    if (year) params = params.set('year', year.toString());
    return this.http.get<ApiResponse<RevenueData>>(`${this.apiUrl}/revenue`, { params });
  }

  getKpi(month?: number, year?: number): Observable<ApiResponse<KpiData>> {
    let params = new HttpParams();
    if (month) params = params.set('month', month.toString());
    if (year) params = params.set('year', year.toString());
    return this.http.get<ApiResponse<KpiData>>(`${this.apiUrl}/kpi`, { params });
  }

  getTrend(months: number = 6): Observable<ApiResponse<TrendData>> {
    return this.http.get<ApiResponse<TrendData>>(`${this.apiUrl}/trend?months=${months}`);
  }

  getBookingSources(): Observable<ApiResponse<BookingSourceData[]>> {
    return this.http.get<ApiResponse<BookingSourceData[]>>(`${this.apiUrl}/booking-sources`);
  }
}
