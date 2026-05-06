import { Component, OnInit } from '@angular/core';
import { InvoiceService } from '@core/services/invoice.service';
import { Invoice } from '@shared/models/invoice.model';

@Component({
  selector: 'app-invoice-list',
  templateUrl: './invoice-list.component.html',
  styleUrls: ['./invoice-list.component.scss']
})
export class InvoiceListComponent implements OnInit {
  invoices: Invoice[] = [];
  displayedColumns: string[] = ['reservationId', 'totalAmount', 'paidAmount', 'balanceDue', 'paymentStatus', 'actions'];
  loading = false;

  constructor(private invoiceService: InvoiceService) { }

  ngOnInit(): void {
    this.loadInvoices();
  }

  loadInvoices(): void {
    this.loading = true;
    this.invoiceService.getAllInvoices().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.invoices = response.data.content || [];
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading invoices', error);
        this.loading = false;
      }
    });
  }

  getPaymentStatusColor(status: string): string {
    const statusColors: any = {
      'UNPAID': 'red',
      'PARTIAL': 'orange',
      'PAID': 'green',
      'REFUNDED': 'gray'
    };
    return statusColors[status] || 'gray';
  }
}
