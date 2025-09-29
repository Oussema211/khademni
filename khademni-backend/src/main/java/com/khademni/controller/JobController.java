package com.khademni.controller;

import com.khademni.dto.JobDTO;
import com.khademni.entity.Application;
import com.khademni.entity.Job;
import com.khademni.service.JobService;
import com.khademni.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;
    private final JwtUtil jwtUtil;

    public JobController(JobService jobService, JwtUtil jwtUtil) {
        this.jobService = jobService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<Job> getAllOpenJobs() {
        return jobService.getAllOpenJobs();
    }

    // NEW ENDPOINT: Get jobs for the logged-in employer
    @GetMapping("/my-jobs")
    public ResponseEntity<?> getMyJobs(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String role = jwtUtil.getRoleFromToken(token);

            if (!role.equals("employer")) {
                return ResponseEntity.status(403).body("Only employers can access this endpoint");
            }

            Long employerId = jwtUtil.getClaimsFromToken(token).get("id", Long.class);
            List<Job> jobs = jobService.getJobsByEmployer(employerId);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody JobDTO jobDTO, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String role = jwtUtil.getRoleFromToken(token);
            if (!role.equals("employer")) {
                return ResponseEntity.status(403).body("Only employers can create jobs");
            }
            Long employerId = jwtUtil.getClaimsFromToken(token).get("id", Long.class);
            Job job = jobService.createJob(jobDTO, employerId);
            return ResponseEntity.ok(job);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(@PathVariable Long id) {
        try {
            Job job = jobService.getJobById(id);
            return ResponseEntity.ok(job);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/apply")
    public ResponseEntity<?> applyForJob(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String role = jwtUtil.getRoleFromToken(token);
            if (!role.equals("worker")) {
                return ResponseEntity.status(403).body("Only workers can apply for jobs");
            }
            Long workerId = jwtUtil.getClaimsFromToken(token).get("id", Long.class);
            Application application = jobService.applyForJob(id, workerId);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/applications")
    public ResponseEntity<?> getApplicationsForJob(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String role = jwtUtil.getRoleFromToken(token);
            if (!role.equals("employer")) {
                return ResponseEntity.status(403).body("Only employers can view applications");
            }
            Long employerId = jwtUtil.getClaimsFromToken(token).get("id", Long.class);
            List<Application> applications = jobService.getApplicationsForJob(id, employerId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String role = jwtUtil.getRoleFromToken(token);

            if (!role.equals("employer")) {
                return ResponseEntity.status(403).body("Only employers can delete jobs");
            }

            Long employerId = jwtUtil.getClaimsFromToken(token).get("id", Long.class);
            jobService.deleteJob(id, employerId);

            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}