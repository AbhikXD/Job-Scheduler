package com.project.JobScheduler;

public enum JobStatus {
    PENDING,     // waiting to run (new, or scheduled for a retry)
    RUNNING,     // claimed by a worker
    SUCCEEDED,   // finished, terminal
    DEAD         // out of attempts, terminal (dead-letter)
}