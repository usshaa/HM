import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { InsightsComponent } from './insights.component';

const routes: Routes = [{ path: '', component: InsightsComponent }];

@NgModule({
  declarations: [InsightsComponent],
  imports: [CommonModule, RouterModule.forChild(routes), MatCardModule, MatButtonModule, MatIconModule, MatProgressSpinnerModule]
})
export class InsightsModule {}
