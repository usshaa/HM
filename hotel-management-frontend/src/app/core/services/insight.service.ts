import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '@shared/models/auth.model';

@Injectable({ providedIn: 'root' })
export class InsightService {
  private apiUrl = 'http://localhost:8080/api/insights';

  constructor(private http: HttpClient) {}

  getRoomRecommendations(guestProfile: string): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.apiUrl}/room-recommendations`, { guestProfile });
  }

  getUpsellTips(guestProfile: string): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.apiUrl}/upsell-tips`, { guestProfile });
  }
}
