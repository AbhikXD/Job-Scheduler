package com.project.JobScheduler;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JobRepository {
    public static final JobRowMapper MAPPER = new JobRowMapper();
    private final JdbcTemplate jdbc;

    public JobRepository() {
        this.jdbc = jdbc;
    }

    public void insert(Jobs job){
        jdbc.update("""
                INSERT INTO jobs (id, type, payload, status, run_at, max_attempts, idempotency_key)
                VALUES (?, ?, ?::jsonb, ?, ?, ?, ?)
                """,
                job.id(),
                job.type(),
                job.payload(),
                job.status().name(),
                OffsetDateTime.ofInstant(job.runAt(), ZoneOffset.UTC),
                job.maxAttempts(),
                job.idempotencyKey());
    }
    public Optional<Jobs> findById(UUID id){
        List<Jobs> rows = jdbc.query("SELECT * FROM jobs WHERE id =?",MAPPER,id);
        return rows.stream().findFirst();
    }

    //To Fetch the list of PENDING jobs and Marking it RUNNING
    public List<Jobs> claim(String workerId, int batchsize){
        String sqlQuery = """
        UPDATE jobs
        SET    status       = 'RUNNING',
               locked_by    = ?,
               locked_until = now() + interval '5 minutes',
               attempts     = attempts + 1,
               updated_at   = now()
        WHERE  id IN (
            SELECT id FROM jobs
            WHERE  status = 'PENDING'
              AND  run_at <= now()
            ORDER  BY run_at
            LIMIT  ?
            FOR UPDATE SKIP LOCKED
        )
        RETURNING *""";
        return jdbc.query(sqlQuery,MAPPER,workerId,batchsize); // MAPPER is required in only JDBC Query which returns ROW
    }

    //To mark a job as SUCCEEDED
    public void markSucceeded(UUID id){
        String sqlQuery = """
                UPDATE  jobs SET status = 'SUCCEEDED', locked_by = NULL,locked_until = NULL, updated_at = now()
                WHERE id =?
                """;
        jdbc.update(sqlQuery,id);
    }

    //to mark a job as PENDING again, so that it can be requeued
    public void markFailed(UUID id, String error, Instant nextRunAt){
        String sqlQuery = """
                UPDATE jobs SET last_error = ?,
                run_at = ?,
                status = 'PENDING',
                locked_by = NULL,
                locked_until = NULL,
                updated_at = now()
                WHERE id = ?
                """;
        jdbc.update(sqlQuery,error,OffsetDateTime.ofInstant(nextRunAt,ZoneOffset.UTC),id);
    }

    //To mark the job as DEAD
    public void markDead(UUID id, String error){
        String sqlQuery = """
                UPDATE jobs SET status = 'DEAD',
                locked_by = NULL,
                locked_until = NULL,
                last_error = ?,
                updated_at = now()
                WHERE id = ?
                """;

        jdbc.update(sqlQuery,error,id);
    }


}
