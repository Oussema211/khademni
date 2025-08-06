package com.khademni.dto;

public class ApplicationDTO {
    private Long jobId;
    private Long workerId;
    private String status;

    // Getters and Setters
    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
    public Long getWorkerId() { return workerId; }
    public void setWorkerId(Long workerId) { this.workerId = workerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}