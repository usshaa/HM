import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '@shared/models/auth.model';
import { RoomExtra } from '@shared/models/housekeeping.model';

@Injectable({ providedIn: 'root' })
export class RoomExtraService {
  private apiUrl = 'http://localhost:8080/api/extras';

  constructor(private http: HttpClient) {}

  getExtrasByReservation(reservationId: number): Observable<ApiResponse<RoomExtra[]>> {
    return this.http.get<ApiResponse<RoomExtra[]>>(`${this.apiUrl}/reservation/${reservationId}`);
  }

  addExtra(extra: Partial<RoomExtra>): Observable<ApiResponse<RoomExtra>> {
    return this.http.post<ApiResponse<RoomExtra>>(this.apiUrl, extra);
  }

  deleteExtra(id: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiUrl}/${id}`);
  }
}
