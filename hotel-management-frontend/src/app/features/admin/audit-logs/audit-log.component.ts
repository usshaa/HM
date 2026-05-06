import { Component, OnInit } from '@angular/core';
import { AdminService } from '@core/services/admin.service';
import { AuditLog } from '@shared/models/housekeeping.model';

@Component({
  selector: 'app-audit-log',
  templateUrl: './audit-log.component.html',
  styleUrls: ['./audit-log.component.scss']
})
export class AuditLogComponent implements OnInit {
  logs: AuditLog[] = [];
  displayedColumns = ['id', 'userName', 'action', 'entityType', 'entityId', 'timestamp'];

  constructor(private adminService: AdminService) {}

  ngOnInit(): void { this.loadLogs(); }

  loadLogs(): void {
    this.adminService.getAuditLogs().subscribe(res => {
      if (res.success && res.data) { this.logs = res.data.content || res.data || []; }
    });
  }

  formatDate(ts: number): string { return ts ? new Date(ts).toLocaleString() : '-'; }
}
