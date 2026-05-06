import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatPaginatorModule } from '@angular/material/paginator';
import { UserManagerComponent } from './user-manager/user-manager.component';
import { AuditLogComponent } from './audit-logs/audit-log.component';

const routes: Routes = [
  { path: '', redirectTo: 'users', pathMatch: 'full' },
  { path: 'users', component: UserManagerComponent },
  { path: 'audit-logs', component: AuditLogComponent }
];

@NgModule({
  declarations: [UserManagerComponent, AuditLogComponent],
  imports: [
    CommonModule, RouterModule.forChild(routes), FormsModule, ReactiveFormsModule,
    MatTableModule, MatButtonModule, MatIconModule, MatCardModule, MatDialogModule,
    MatFormFieldModule, MatInputModule, MatSelectModule, MatSnackBarModule,
    MatSlideToggleModule, MatPaginatorModule
  ]
})
export class AdminModule {}
