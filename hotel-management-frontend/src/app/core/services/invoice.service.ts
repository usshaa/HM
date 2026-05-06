import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Invoice } from '@shared/models/invoice.model';
import { ApiResponse } from '@shared/models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {
  private apiUrl = 'http://localhost:8080/api/invoices';

  constructor(private http: HttpClient) { }

  generateInvoice(reservationId: number): Observable<ApiResponse<Invoice>> {
    return this.http.post<ApiResponse<Invoice>>(
      `${this.apiUrl}/generate/${reservationId}`,
      {}
    );
  }

  recordPayment(invoiceId: number, amount: number): Observable<ApiResponse<Invoice>> {
    return this.http.post<ApiResponse<Invoice>>(
      `${this.apiUrl}/${invoiceId}/pay`,
      {},
      { params: new HttpParams().set('amount', amount.toString()) }
    );
  }

  getInvoiceByReservation(reservationId: number): Observable<ApiResponse<Invoice>> {
    return this.http.get<ApiResponse<Invoice>>(
      `${this.apiUrl}/reservation/${reservationId}`
    );
  }

  getAllInvoices(page: number = 0, size: number = 20): Observable<ApiResponse<any>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<any>>(this.apiUrl, { params });
  }
}
