import { Component, OnInit } from '@angular/core';
import { ChartConfiguration } from 'chart.js';
import { AnalyticsService } from '@core/services/analytics.service';
import { RoomService } from '@core/services/room.service';
import { ReservationService } from '@core/services/reservation.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  occupancyRate = 0;
  revenueToday = 0;
  availableRooms = 0;
  pendingCheckouts = 0;
  totalRooms = 0;
  loading = true;

  barChartData: ChartConfiguration<'bar'>['data'] = { labels: [], datasets: [] };
  barChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true, plugins: { legend: { display: true } },
    scales: { y: { beginAtZero: true, title: { display: true, text: 'Revenue (AED)' } } }
  };

  pieChartData: ChartConfiguration<'pie'>['data'] = { labels: [], datasets: [] };
  pieChartOptions: ChartConfiguration<'pie'>['options'] = { responsive: true, plugins: { legend: { position: 'bottom' } } };

  lineChartData: ChartConfiguration<'line'>['data'] = { labels: [], datasets: [] };
  lineChartOptions: ChartConfiguration<'line'>['options'] = {
    responsive: true,
    scales: { y: { beginAtZero: true, max: 100, title: { display: true, text: 'Occupancy %' } } }
  };

  constructor(private analyticsService: AnalyticsService, private roomService: RoomService, private reservationService: ReservationService) {}

  ngOnInit(): void {
    this.loadKpis();
    this.loadTrendChart();
    this.loadBookingSources();
    this.loadRevenueChart();
  }

  loadKpis(): void {
    this.analyticsService.getOccupancy('daily').subscribe({
      next: res => { if (res.success && res.data && res.data.length > 0) { this.occupancyRate = res.data[0].occupancyRate; this.totalRooms = res.data[0].totalRooms; this.availableRooms = res.data[0].totalRooms - res.data[0].occupiedRooms; } this.loading = false; },
      error: () => { this.loading = false; this.occupancyRate = 75; this.availableRooms = 8; this.totalRooms = 20; }
    });
    this.analyticsService.getKpi().subscribe({
      next: res => { if (res.success && res.data) { this.revenueToday = res.data.averageDailyRate; this.pendingCheckouts = res.data.totalCheckOuts; } },
      error: () => { this.revenueToday = 15000; this.pendingCheckouts = 5; }
    });
  }

  loadTrendChart(): void {
    this.analyticsService.getTrend(6).subscribe({
      next: res => {
        if (res.success && res.data) {
          this.lineChartData = {
            labels: res.data.labels,
            datasets: [{ data: res.data.occupancyRates, label: 'Occupancy Rate %', borderColor: '#1a237e', backgroundColor: 'rgba(26,35,126,0.1)', fill: true, tension: 0.4 }]
          };
        }
      },
      error: () => this.setMockLineChart()
    });
  }

  loadBookingSources(): void {
    this.analyticsService.getBookingSources().subscribe({
      next: res => {
        if (res.success && res.data) {
          this.pieChartData = {
            labels: res.data.map(d => d.source),
            datasets: [{ data: res.data.map(d => d.count), backgroundColor: ['#1a237e', '#4caf50', '#ff9800'] }]
          };
        }
      },
      error: () => this.setMockPieChart()
    });
  }

  loadRevenueChart(): void {
    this.analyticsService.getTrend(6).subscribe({
      next: res => {
        if (res.success && res.data) {
          this.barChartData = {
            labels: res.data.labels,
            datasets: [{ data: res.data.revenues, label: 'Revenue (AED)', backgroundColor: 'rgba(26,35,126,0.7)', borderRadius: 6 }]
          };
        }
      },
      error: () => this.setMockBarChart()
    });
  }

  private setMockLineChart(): void {
    this.lineChartData = { labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'], datasets: [{ data: [65, 72, 68, 78, 82, 75], label: 'Occupancy %', borderColor: '#1a237e', fill: true, tension: 0.4 }] };
  }
  private setMockPieChart(): void {
    this.pieChartData = { labels: ['Direct', 'Portal', 'Walk-in'], datasets: [{ data: [45, 35, 20], backgroundColor: ['#1a237e', '#4caf50', '#ff9800'] }] };
  }
  private setMockBarChart(): void {
    this.barChartData = { labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'], datasets: [{ data: [32000, 28000, 35000, 41000, 38000, 45000], label: 'Revenue (AED)', backgroundColor: 'rgba(26,35,126,0.7)', borderRadius: 6 }] };
  }
}
