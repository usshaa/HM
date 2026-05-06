import { Component, OnInit } from '@angular/core';
import { InsightService } from '@core/services/insight.service';
import { AuthService } from '@core/services/auth.service';

@Component({
  selector: 'app-insights',
  templateUrl: './insights.component.html',
  styleUrls: ['./insights.component.scss']
})
export class InsightsComponent implements OnInit {
  recommendations: any[] = [];
  upsellTips: any[] = [];
  loadingRec = false;
  loadingTips = false;

  constructor(private insightService: InsightService, private authService: AuthService) {}

  ngOnInit(): void { this.loadRecommendations(); this.loadUpsellTips(); }

  loadRecommendations(): void {
    this.loadingRec = true;
    const user = this.authService.getCurrentUser();
    const profile = user ? `Guest: ${user.name}, Email: ${user.email}` : 'General guest';
    this.insightService.getRoomRecommendations(profile).subscribe({
      next: res => {
        this.loadingRec = false;
        if (res.success && res.data) {
          try { this.recommendations = JSON.parse(res.data); } catch { this.recommendations = [{ title: 'AI Recommendation', description: res.data, category: 'GENERAL' }]; }
        }
      },
      error: () => { this.loadingRec = false; }
    });
  }

  loadUpsellTips(): void {
    this.loadingTips = true;
    this.insightService.getUpsellTips('Business traveler').subscribe({
      next: res => {
        this.loadingTips = false;
        if (res.success && res.data) {
          try { this.upsellTips = JSON.parse(res.data); } catch { this.upsellTips = [{ title: 'Tip', description: res.data, priority: 'MEDIUM' }]; }
        }
      },
      error: () => { this.loadingTips = false; }
    });
  }

  getCategoryIcon(cat: string): string {
    const icons: { [k: string]: string } = { ROOM_UPGRADE: 'upgrade', AMENITY: 'spa', ACTIVITY: 'directions_run' };
    return icons[cat] || 'lightbulb';
  }
}
