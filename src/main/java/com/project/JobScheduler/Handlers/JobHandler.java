package com.project.JobScheduler.Handlers;

import com.project.JobScheduler.Jobs;

public interface JobHandler {
    void handle(Jobs job);
}
