package com.khademni.controller;

import com.khademni.dto.JobDTO;
import com.khademni.entity.Application;
import com.khademni.entity.Job;
import com.khademni.service.JobService;
import com.khademni.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private static final Logger logger = LoggerFactory.getLogger(JobController.class);

    private final JobService jobService;
    private final JwtUtil jwtUtil;

    public JobController(JobService jobService, JwtUtil jwtUtil) {
        this.jobService = jobService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<?> getAllOpenJobs() {
        try {
            logger.info("GET /api/jobs - Fetching all open jobs");
            List<Job> jobs = jobService.getAllOpenJobs();
            logger.info("Successfully retrieved {} jobs", jobs.size());
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            logger.error("Error fetching all jobs: ", e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/my-jobs")
    public ResponseEntity<?> getMyJobs(@RequestHeader("Authorization") String authHeader) {
        try {
            logger.info("GET /api/jobs/my-jobs - Authorization header received");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.error("Invalid authorization header format");
                return ResponseEntity.status(401).body("Invalid authorization header");
            }

            String token = authHeader.substring(7);
            logger.info("Token extracted, length: {}", token.length());

            String role = jwtUtil.getRoleFromToken(token);
            logger.info("Role from token: {}", role);

            if (!role.equals("employer")) {
                logger.warn("Access denied - User role is {} but endpoint requires employer", role);
                return ResponseEntity.status(403).body("Only employers can access this endpoint");
            }

            Long employerId = jwtUtil.getClaimsFromToken(token).get("id", Long.class);
            logger.info("Employer ID: {}", employerId);

            List<Job> jobs = jobService.getJobsByEmployer(employerId);
            logger.info("Successfully retrieved {} jobs for employer {}", jobs.size(), employerId);

            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            logger.error("Error in getMyJobs: ", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody JobDTO jobDTO, @RequestHeader("Authorization") String authHeader) {
        try {
            logger.info("POST /api/jobs - Creating new job");
            logger.info("Job data: {}", jobDTO);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.error("Invalid authorization header format");
                return ResponseEntity.status(401).body("Invalid authorization header");
            }

            String token = authHeader.substring(7);
            String role = jwtUtil.getRoleFromToken(token);
            logger.info("User role: {}", role);

            if (!role.equals("employer")) {
                logger.warn("Access denied - Only employers can create jobs");
                return ResponseEntity.status(403).body("Only employers can create jobs");
            }

            Long employerId = jwtUtil.getClaimsFromToken(token).get("id", Long.class);
            logger.info("Creating job for employer ID: {}", employerId);

            Job job = jobService.createJob(jobDTO, employerId);
            logger.info("Job created successfully with ID: {}", job.getId());

            return ResponseEntity.ok(job);
        } catch (Exception e) {
            logger.error("Error creating job: ", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(@PathVariable Long id) {
        try {
            logger.info("GET /api/jobs/{} - Fetching job details", id);
            Job job = jobService.getJobById(id);
            return ResponseEntity.ok(job);
        } catch (Exception e) {
            logger.error("Error fetching job {}: ", id, e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/apply")
    public ResponseEntity<?> applyForJob(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        try {
            logger.info("POST /api/jobs/{}/apply - Worker applying for job", id);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.error("Invalid authorization header format");
                return ResponseEntity.status(401).body("Invalid authorization header");
            }

            String token = authHeader.substring(7);
            String role = jwtUtil.getRoleFromToken(token);
            logger.info("User role: {}", role);

            if (!role.equals("worker")) {
                logger.warn("Access denied - Only workers can apply for jobs");
                return ResponseEntity.status(403).body("Only workers can apply for jobs");
            }

            Long workerId = jwtUtil.getClaimsFromToken(token).get("id", Long.class);
            logger.info("Worker ID {} applying for job {}", workerId, id);

            Application application = jobService.applyForJob(id, workerId);
            logger.info("Application created successfully with ID: {}", application.getId());

            return ResponseEntity.ok(application);
        } catch (Exception e) {
            logger.error("Error applying for job {}: ", id, e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/applications")
    public ResponseEntity<?> getApplicationsForJob(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        try {
            logger.info("GET /api/jobs/{}/applications - Fetching applications", id);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.error("Invalid authorization header format");
                return ResponseEntity.status(401).body("Invalid authorization header");
            }

            String token = authHeader.substring(7);
            String role = jwtUtil.getRoleFromToken(token);

            if (!role.equals("employer")) {
                logger.warn("Access denied - Only employers can view applications");
                return ResponseEntity.status(403).body("Only employers can view applications");
            }

            Long employerId = jwtUtil.getClaimsFromToken(token).get("id", Long.class);
            logger.info("Fetching applications for employer ID: {}", employerId);

            List<Application> applications = jobService.getApplicationsForJob(id, employerId);
            logger.info("Found {} applications for job {}", applications.size(), id);

            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            logger.error("Error fetching applications for job {}: ", id, e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        try {
            logger.info("DELETE /api/jobs/{} - Deleting job", id);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.error("Invalid authorization header format");
                return ResponseEntity.status(401).body("Invalid authorization header");
            }

            String token = authHeader.substring(7);
            String role = jwtUtil.getRoleFromToken(token);

            if (!role.equals("employer")) {
                logger.warn("Access denied - Only employers can delete jobs");
                return ResponseEntity.status(403).body("Only employers can delete jobs");
            }

            Long employerId = jwtUtil.getClaimsFromToken(token).get("id", Long.class);
            logger.info("Employer ID {} deleting job {}", employerId, id);

            jobService.deleteJob(id, employerId);
            logger.info("Job {} deleted successfully", id);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting job {}: ", id, e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}