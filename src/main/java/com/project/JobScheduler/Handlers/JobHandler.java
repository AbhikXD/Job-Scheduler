package com.project.JobScheduler.Handlers;

import com.project.JobScheduler.Jobs;

public interface JobHandler {
    String type();
    void handle(Jobs job);
}
