import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class JobService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  // Get all open jobs (for workers)
  getAllJobs(): Observable<any> {
    return this.http.get(`${this.apiUrl}/api/jobs`);
  }

  // NEW: Get jobs posted by the logged-in employer
  getMyJobs(): Observable<any> {
    return this.http.get(`${this.apiUrl}/api/jobs/my-jobs`, { headers: this.getHeaders() });
  }

  createJob(job: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/jobs`, job, { headers: this.getHeaders() });
  }

  applyForJob(jobId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/jobs/${jobId}/apply`, {}, { headers: this.getHeaders() });
  }
  // Delete a job by ID
deleteJob(jobId: number): Observable<void> {
  return this.http.delete<void>(`${this.apiUrl}/api/jobs/${jobId}`, { headers: this.getHeaders() });
}


  getApplications(jobId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/api/jobs/${jobId}/applications`, { headers: this.getHeaders() });
  }
}