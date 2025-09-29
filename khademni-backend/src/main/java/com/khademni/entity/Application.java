package com.khademni.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long jobId;

    @Column(nullable = false)
    private Long workerId;

    @Column(nullable = false)
    private String status; // "pending", "accepted", "rejected"

    @ManyToOne
    @JoinColumn(name = "jobId", insertable = false, updatable = false)
    @JsonIgnoreProperties({"applications", "employer"})  // Prevent circular reference
    private Job job;

    @ManyToOne
    @JoinColumn(name = "workerId", insertable = false, updatable = false)
    @JsonIgnoreProperties({"applications"})  // Prevent circular reference
    private User worker;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public Long getWorkerId() { return workerId; }
    public void setWorkerId(Long workerId) { this.workerId = workerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public User getWorker() { return worker; }
    public void setWorker(User worker) { this.worker = worker; }
}