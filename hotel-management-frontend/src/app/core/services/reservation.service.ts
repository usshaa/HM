import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reservation } from '@shared/models/reservation.model';
import { ApiResponse } from '@shared/models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private apiUrl = 'http://localhost:8080/api/reservations';

  constructor(private http: HttpClient) { }

  createReservation(reservation: Reservation): Observable<ApiResponse<Reservation>> {
    return this.http.post<ApiResponse<Reservation>>(this.apiUrl, reservation);
  }

  confirmReservation(reservationId: number): Observable<ApiResponse<Reservation>> {
    return this.http.patch<ApiResponse<Reservation>>(
      `${this.apiUrl}/${reservationId}/confirm`,
      {}
    );
  }

  checkIn(reservationId: number, roomId: number): Observable<ApiResponse<Reservation>> {
    return this.http.post<ApiResponse<Reservation>>(
      `${this.apiUrl}/${reservationId}/checkin`,
      {},
      { params: new HttpParams().set('roomId', roomId.toString()) }
    );
  }

  checkOut(reservationId: number): Observable<ApiResponse<Reservation>> {
    return this.http.post<ApiResponse<Reservation>>(
      `${this.apiUrl}/${reservationId}/checkout`,
      {}
    );
  }

  getAllReservations(page: number = 0, size: number = 20): Observable<ApiResponse<any>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<any>>(this.apiUrl, { params });
  }

  getGuestReservations(guestId: number, page: number = 0, size: number = 20): Observable<ApiResponse<any>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/guest/${guestId}`, { params });
  }
}
