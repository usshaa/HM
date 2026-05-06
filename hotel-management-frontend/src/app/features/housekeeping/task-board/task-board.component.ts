import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HousekeepingService } from '@core/services/housekeeping.service';
import { HousekeepingTask, TaskStatus, TaskType, Priority } from '@shared/models/housekeeping.model';

@Component({
  selector: 'app-task-board',
  templateUrl: './task-board.component.html',
  styleUrls: ['./task-board.component.scss']
})
export class TaskBoardComponent implements OnInit {
  pendingTasks: HousekeepingTask[] = [];
  inProgressTasks: HousekeepingTask[] = [];
  completedTasks: HousekeepingTask[] = [];
  showForm = false;
  newTask: Partial<HousekeepingTask> = { taskType: TaskType.DAILY_SERVICE, priority: Priority.MEDIUM };
  TaskType = TaskType;
  Priority = Priority;

  constructor(private housekeepingService: HousekeepingService, private snackBar: MatSnackBar) {}

  ngOnInit(): void { this.loadTasks(); }

  loadTasks(): void {
    this.housekeepingService.getTasks().subscribe(res => {
      if (res.success && res.data) {
        const tasks = res.data.content || res.data || [];
        this.pendingTasks = tasks.filter((t: HousekeepingTask) => t.status === TaskStatus.PENDING);
        this.inProgressTasks = tasks.filter((t: HousekeepingTask) => t.status === TaskStatus.IN_PROGRESS);
        this.completedTasks = tasks.filter((t: HousekeepingTask) => t.status === TaskStatus.COMPLETED);
      }
    });
  }

  moveTask(task: HousekeepingTask, newStatus: string): void {
    this.housekeepingService.updateTaskStatus(task.id, newStatus).subscribe(res => {
      if (res.success) { this.snackBar.open('Task updated!', 'Close', { duration: 2000 }); this.loadTasks(); }
    });
  }

  createTask(): void {
    if (!this.newTask.roomId || !this.newTask.assignedToId) return;
    this.newTask.scheduledDate = Date.now();
    this.housekeepingService.createTask(this.newTask).subscribe(res => {
      if (res.success) { this.snackBar.open('Task created!', 'Close', { duration: 2000 }); this.showForm = false; this.loadTasks(); }
    });
  }

  getPriorityColor(priority: string): string {
    const colors: { [key: string]: string } = { LOW: '#4caf50', MEDIUM: '#ff9800', HIGH: '#f44336', URGENT: '#9c27b0' };
    return colors[priority] || '#757575';
  }
}
