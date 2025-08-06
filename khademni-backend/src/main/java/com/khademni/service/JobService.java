package com.khademni.service;

import com.khademni.dto.ApplicationDTO;
import com.khademni.dto.JobDTO;
import com.khademni.entity.Application;
import com.khademni.entity.Job;
import com.khademni.entity.User;
import com.khademni.repository.ApplicationRepository;
import com.khademni.repository.JobRepository;
import com.khademni.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository, ApplicationRepository applicationRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    public Job createJob(JobDTO jobDTO, Long employerId) {
        Job job = new Job();
        job.setTitle(jobDTO.getTitle());
        job.setDescription(jobDTO.getDescription());
        job.setEmployerId(employerId);
        job.setStatus("open");
        return jobRepository.save(job);
    }

    public List<Job> getAllOpenJobs() {
        return jobRepository.findByStatus("open");
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    public Application applyForJob(Long jobId, Long workerId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));
        if (!job.getStatus().equals("open")) {
            throw new RuntimeException("Job is closed");
        }
        Application application = new Application();
        application.setJobId(jobId);
        application.setWorkerId(workerId);
        application.setStatus("pending");
        return applicationRepository.save(application);
    }

    public List<Application> getApplicationsForJob(Long jobId, Long employerId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        if (!job.getEmployerId().equals(employerId)) {
            throw new RuntimeException("Unauthorized");
        }
        return applicationRepository.findByJobId(jobId);
    }
}