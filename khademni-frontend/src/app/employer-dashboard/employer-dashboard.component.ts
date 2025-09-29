import { Component, OnInit } from '@angular/core';
import { JobService } from '../services/job.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-employer-dashboard',
  templateUrl: './employer-dashboard.component.html',
  styleUrls: ['./employer-dashboard.component.css']
})
export class EmployerDashboardComponent implements OnInit {
  job = { title: '', description: '', status: 'open' };
  jobs: any[] = [];
  applications: any[] = [];
  selectedJobId: number | null = null;

  constructor(
    private jobService: JobService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loadJobs();
  }

  loadJobs() {
    this.jobService.getAllJobs().subscribe({
      next: (jobs) => {
        // Filter jobs by current employer
        this.jobs = jobs;
        console.log('Loaded jobs:', jobs);
      },
      error: (err) => {
        console.error('Error loading jobs:', err);
        alert('Failed to load jobs');
      }
    });
  }

  postJob() {
    if (!this.job.title || !this.job.description) {
      alert('Please fill in all fields');
      return;
    }

    this.jobService.createJob(this.job).subscribe({
      next: (job) => {
        alert('Job posted successfully!');
        this.job = { title: '', description: '', status: 'open' };
        this.loadJobs();
      },
      error: (err) => {
        console.error('Job creation error:', err);
        const errorMsg = err.error?.message || err.error || 'Job creation failed';
        alert('Job creation failed: ' + errorMsg);
      }
    });
  }

  viewApplications(jobId: number) {
    this.selectedJobId = jobId;
    this.applications = [];
    
    console.log('Fetching applications for job:', jobId);
    
    this.jobService.getApplications(jobId).subscribe({
      next: (applications) => {
        this.applications = applications;
        console.log('Applications loaded:', applications);
        
        if (applications.length === 0) {
          alert('No applications yet for this job');
        }
      },
      error: (err) => {
        console.error('Error loading applications:', err);
        
        // Better error message extraction
        let errorMsg = 'Failed to load applications';
        
        if (err.status === 403) {
          errorMsg = 'You are not authorized to view these applications';
        } else if (err.status === 404) {
          errorMsg = 'Job not found';
        } else if (err.error) {
          if (typeof err.error === 'string') {
            errorMsg = err.error;
          } else if (err.error.message) {
            errorMsg = err.error.message;
          }
        } else if (err.message) {
          errorMsg = err.message;
        }
        
        alert(errorMsg);
      }
    });
  }
  deleteJob(jobId: number) {
  if (confirm('Are you sure you want to delete this job?')) {
    this.jobService.deleteJob(jobId).subscribe({
      next: () => {
        // Remove deleted job from list without reloading
        this.jobs = this.jobs.filter(job => job.id !== jobId);
        alert('Job deleted successfully');
      },
      error: (err) => {
        console.error('Error deleting job:', err);
        const errorMsg = err.error?.message || err.error || 'Job deletion failed';
        alert(errorMsg);
      }
    });
  }
}

  


  logout() {
    if (confirm('Are you sure you want to logout?')) {
      this.authService.logout();
    }
  }
}