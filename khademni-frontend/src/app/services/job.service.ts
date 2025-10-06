import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class JobService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    if (!token) {
      console.warn('⚠️ No token found in localStorage');
    }
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  private handleError(error: HttpErrorResponse) {
    console.error('=== HTTP Error Details ===');
    console.error('Status:', error.status);
    console.error('Status Text:', error.statusText);
    console.error('URL:', error.url);
    console.error('Error Object:', error);
    
    let errorMessage = 'An error occurred';
    
    if (error.status === 0) {
      console.error('❌ Network error - Cannot connect to backend');
      console.error('Check if backend is running on:', this.apiUrl);
      errorMessage = 'Cannot connect to server. Please check if the backend is running.';
    } else if (error.status === 401) {
      console.error('❌ Unauthorized - Invalid or expired token');
      errorMessage = 'Unauthorized. Please login again.';
    } else if (error.status === 403) {
      console.error('❌ Forbidden - Insufficient permissions');
      errorMessage = 'You do not have permission to perform this action.';
    } else if (error.status === 404) {
      console.error('❌ Not Found');
      errorMessage = 'Resource not found.';
    } else if (error.error) {
      if (typeof error.error === 'string') {
        errorMessage = error.error;
      } else if (error.error.message) {
        errorMessage = error.error.message;
      }
    }
    
    // Create a user-friendly error object
    const userError = new Error(errorMessage);
    (userError as any).status = error.status;
    (userError as any).originalError = error;
    
    return throwError(() => userError);
  }

  getAllJobs(): Observable<any> {
    const url = `${this.apiUrl}/api/jobs`;
    console.log('🔍 GET:', url);
    
    return this.http.get(url).pipe(
      tap(response => console.log('✅ getAllJobs success:', response)),
      catchError(this.handleError.bind(this))
    );
  }

  getMyJobs(): Observable<any> {
    const url = `${this.apiUrl}/api/jobs/my-jobs`;
    console.log('🔍 GET:', url);
    console.log('Token:', localStorage.getItem('token') ? 'Present' : 'Missing');
    
    return this.http.get(url, { headers: this.getHeaders() }).pipe(
      tap(response => console.log('✅ getMyJobs success:', response)),
      catchError(this.handleError.bind(this))
    );
  }

  createJob(job: any): Observable<any> {
    const url = `${this.apiUrl}/api/jobs`;
    console.log('🔍 POST:', url);
    console.log('Job data:', job);
    console.log('Token:', localStorage.getItem('token') ? 'Present' : 'Missing');
    
    return this.http.post(url, job, { headers: this.getHeaders() }).pipe(
      tap(response => console.log('✅ createJob success:', response)),
      catchError(this.handleError.bind(this))
    );
  }

  applyForJob(jobId: number): Observable<any> {
    const url = `${this.apiUrl}/api/jobs/${jobId}/apply`;
    console.log('🔍 POST:', url);
    console.log('Token:', localStorage.getItem('token') ? 'Present' : 'Missing');
    
    return this.http.post(url, {}, { headers: this.getHeaders() }).pipe(
      tap(response => console.log('✅ applyForJob success:', response)),
      catchError(this.handleError.bind(this))
    );
  }

  deleteJob(jobId: number): Observable<void> {
    const url = `${this.apiUrl}/api/jobs/${jobId}`;
    console.log('🔍 DELETE:', url);
    console.log('Token:', localStorage.getItem('token') ? 'Present' : 'Missing');
    
    return this.http.delete<void>(url, { headers: this.getHeaders() }).pipe(
      tap(() => console.log('✅ deleteJob success')),
      catchError(this.handleError.bind(this))
    );
  }

  getApplications(jobId: number): Observable<any> {
    const url = `${this.apiUrl}/api/jobs/${jobId}/applications`;
    console.log('🔍 GET:', url);
    console.log('Token:', localStorage.getItem('token') ? 'Present' : 'Missing');
    
    return this.http.get(url, { headers: this.getHeaders() }).pipe(
      tap(response => console.log('✅ getApplications success:', response)),
      catchError(this.handleError.bind(this))
    );
  }
}