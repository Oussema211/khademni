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
  loading: boolean = false;
  errorMessage: string = '';

  constructor(
    private jobService: JobService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    console.log('Employer Dashboard initialized');
    console.log('Token present:', !!localStorage.getItem('token'));
    this.loadJobs();
  }

  loadJobs() {
    this.loading = true;
    this.errorMessage = '';
    
    this.jobService.getAllJobs().subscribe({
      next: (jobs) => {
        console.log('✅ Jobs loaded successfully:', jobs);
        this.jobs = jobs;
        this.loading = false;
      },
      error: (err) => {
        console.error('❌ Error loading jobs:', err);
        this.errorMessage = err.message || 'Failed to load jobs';
        this.loading = false;
        alert('Failed to load jobs: ' + this.errorMessage);
      }
    });
  }

  postJob() {
    if (!this.job.title || !this.job.description) {
      alert('Please fill in all fields');
      return;
    }

    console.log('Posting job:', this.job);
    this.loading = true;

    this.jobService.createJob(this.job).subscribe({
      next: (job) => {
        console.log('✅ Job posted successfully:', job);
        alert('Job posted successfully!');
        this.job = { title: '', description: '', status: 'open' };
        this.loading = false;
        this.loadJobs();
      },
      error: (err) => {
        console.error('❌ Job creation error:', err);
        this.loading = false;
        alert('Job creation failed: ' + err.message);
      }
    });
  }

  viewApplications(jobId: number) {
    this.selectedJobId = jobId;
    this.applications = [];
    this.loading = true;
    
    console.log('Fetching applications for job:', jobId);
    
    this.jobService.getApplications(jobId).subscribe({
      next: (applications) => {
        console.log('✅ Applications loaded:', applications);
        this.applications = applications;
        this.loading = false;
        
        if (applications.length === 0) {
          alert('No applications yet for this job');
        }
      },
      error: (err) => {
        console.error('❌ Error loading applications:', err);
        this.loading = false;
        alert('Failed to load applications: ' + err.message);
      }
    });
  }

  deleteJob(jobId: number) {
    if (confirm('Are you sure you want to delete this job?')) {
      console.log('Deleting job:', jobId);
      this.loading = true;

      this.jobService.deleteJob(jobId).subscribe({
        next: () => {
          console.log('✅ Job deleted successfully');
          this.jobs = this.jobs.filter(job => job.id !== jobId);
          this.loading = false;
          alert('Job deleted successfully');
        },
        error: (err) => {
          console.error('❌ Error deleting job:', err);
          this.loading = false;
          alert('Job deletion failed: ' + err.message);
        }
      });
    }
  }

  logout() {
    if (confirm('Are you sure you want to logout?')) {
      console.log('Logging out...');
      this.authService.logout();
    }
  }
}