import { Component, OnInit } from '@angular/core';
import { JobService } from '../services/job.service';

@Component({
  selector: 'app-worker-dashboard',
  templateUrl: './worker-dashboard.component.html',
  styleUrls: ['./worker-dashboard.component.css']
})
export class WorkerDashboardComponent implements OnInit {
  jobs: any[] = [];

  constructor(private jobService: JobService) {}

  ngOnInit() {
    this.jobService.getAllJobs().subscribe({
      next: (jobs) => this.jobs = jobs,
      error: (err) => console.error(err)
    });
  }

  applyForJob(jobId: number) {
    this.jobService.applyForJob(jobId).subscribe({
      next: () => alert('Application submitted'),
      error: (err) => alert('Application failed: ' + err.error)
    });
  }
}