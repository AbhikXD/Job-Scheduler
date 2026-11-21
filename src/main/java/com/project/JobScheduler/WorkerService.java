package com.project.JobScheduler;

import com.project.JobScheduler.Handlers.HandlerRegistry;
import com.project.JobScheduler.Handlers.JobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class WorkerService {

    private static final Logger logger = LoggerFactory.getLogger(WorkerService.class);
    private final HandlerRegistry handlerRegistry;
    private final JobRepository jobRepository;
    private final String workerId;

    public WorkerService(JobRepository jobRepository,HandlerRegistry handlerRegistry){
        this.handlerRegistry = handlerRegistry;
        this.jobRepository = jobRepository;
        this.workerId = "worker-" + UUID.randomUUID().toString().substring(0,0);
    }

    @Scheduled(fixedDelay =  2000)
    public void pollAndExecute(){
        int batchSize = 5;
        List<Jobs> claimedJobs = jobRepository.claim(workerId,batchSize);
        if(claimedJobs.isEmpty()){
            return;
        }

        logger.info("Worker [{}] claimed {} jobs ",workerId,claimedJobs.size());

        for(Jobs job : claimedJobs){
            executeJob(job);
        }
    }

    private void executeJob(Jobs job){
        try{
            JobHandler jobHandler = handlerRegistry.get(job.type());
            jobHandler.handle(job);
            jobRepository.markSucceeded(job.id());
            logger.info("Job [{}] of type [{}] completed successfully", job.id(), job.type());
        }catch(Exception e){
            logger.error("Job [{}] execution failed: {}", job.id(), e.getMessage());
            handleFailure(job, e);
        }
    }

    private void handleFailure(Jobs job, Exception e){
        String errorMessage = e.getMessage()!= null?e.getMessage(): e.getClass().getSimpleName();
        //attempts count was already incremented by 1 during claim
        if(job.attempts() >= job.maxAttempts()){
            logger.warn("Job [{}] reached max attempts ({}/{}). Moving to DEAD.",
                    job.id(), job.attempts(), job.maxAttempts());
            jobRepository.markDead(job.id(), errorMessage);
        }else{

            Instant nextRunAt = Instant.now().plusSeconds(30);
            logger.info("Requeueing Job [{}] (attempt {}/{}). Next run at : {} ",job.id(),job.attempts(),job.maxAttempts(),nextRunAt);
            jobRepository.markFailed(job.id(),errorMessage,nextRunAt);
        }
    }

}
