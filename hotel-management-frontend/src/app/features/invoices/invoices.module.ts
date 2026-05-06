import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';

import { InvoiceListComponent } from './invoice-list/invoice-list.component';

const routes: Routes = [
  { path: '', component: InvoiceListComponent }
];

@NgModule({
  declarations: [InvoiceListComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    MatTableModule,
    MatButtonModule,
    MatCardModule
  ]
})
export class InvoicesModule { }
