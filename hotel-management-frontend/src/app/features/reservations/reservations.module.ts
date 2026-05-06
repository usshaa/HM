import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';

import { ReservationListComponent } from './reservation-list/reservation-list.component';

const routes: Routes = [
  { path: '', component: ReservationListComponent }
];

@NgModule({
  declarations: [ReservationListComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    MatTableModule,
    MatButtonModule,
    MatCardModule
  ]
})
export class ReservationsModule { }
