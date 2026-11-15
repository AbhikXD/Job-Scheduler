package com.project.JobScheduler;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JobRepository {
    public static final JobRowMapper MAPPER = new JobRowMapper();
    private final JdbcTemplate jdbc;

    public JobRepository(JdbcTemplate jdbc) {
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
}
