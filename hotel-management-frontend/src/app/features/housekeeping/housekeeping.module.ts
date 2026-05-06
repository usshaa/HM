import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { TaskBoardComponent } from './task-board/task-board.component';
import { MaintenanceListComponent } from './maintenance/maintenance-list.component';

const routes: Routes = [
  { path: '', redirectTo: 'tasks', pathMatch: 'full' },
  { path: 'tasks', component: TaskBoardComponent },
  { path: 'maintenance', component: MaintenanceListComponent }
];

@NgModule({
  declarations: [TaskBoardComponent, MaintenanceListComponent],
  imports: [
    CommonModule, RouterModule.forChild(routes), FormsModule, ReactiveFormsModule,
    MatTableModule, MatButtonModule, MatIconModule, MatCardModule, MatChipsModule,
    MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatSnackBarModule
  ]
})
export class HousekeepingModule {}
