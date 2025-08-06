import { Component, OnInit } from '@angular/core';
import { JobService } from '../services/job.service';

@Component({
  selector: 'app-employer-dashboard',
  templateUrl: './employer-dashboard.component.html',
  styleUrls: ['./employer-dashboard.component.css']
})
export class EmployerDashboardComponent implements OnInit {
  job = { title: '', description: '', status: 'open' };
  jobs: any[] = [];
  applications: any[] = [];

  constructor(private jobService: JobService) {}

  ngOnInit() {
    this.jobService.getAllJobs().subscribe({
      next: (jobs) => this.jobs = jobs,
      error: (err) => console.error(err)
    });
  }

  postJob() {
    this.jobService.createJob(this.job).subscribe({
      next: (job) => {
        this.jobs.push(job);
        this.job = { title: '', description: '', status: 'open' };
      },
      error: (err) => alert('Job creation failed: ' + err.error)
    });
  }

  viewApplications(jobId: number) {
    this.jobService.getApplications(jobId).subscribe({
      next: (applications) => this.applications = applications,
      error: (err) => alert('Failed to load applications: ' + err.error)
    });
  }
}