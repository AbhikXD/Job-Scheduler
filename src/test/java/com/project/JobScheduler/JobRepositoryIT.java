package com.project.JobScheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class JobRepositoryIT {

    @Autowired
    private JobRepository repository;

    private Jobs newJob(String idempotencyKey) {
        return new Jobs(
                UUID.randomUUID(),
                "SEND_EMAIL",
                "{\"to\": \"a@b.com\"}",
                JobStatus.PENDING,
                Instant.now().truncatedTo(ChronoUnit.MILLIS),
                0,
                5,
                null, null,            // lockedBy, lockedUntil
                idempotencyKey,
                null,                  // lastError
                null, null             // createdAt, updatedAt (database fills them)
        );
    }

    @Test
    void roundTripWithOptionalFieldsNull() {
        Jobs job = newJob(null);

        repository.insert(job);
        Jobs found = repository.findById(job.id()).orElseThrow();

        assertEquals(job.id(), found.id());
        assertEquals(job.type(), found.type());
        assertEquals(job.payload(), found.payload());
        assertEquals(JobStatus.PENDING, found.status());
        assertEquals(job.runAt(), found.runAt());
        assertEquals(0, found.attempts());
        assertEquals(5, found.maxAttempts());
        assertNull(found.lockedBy());
        assertNull(found.lockedUntil());
        assertNull(found.idempotencyKey());
        assertNull(found.lastError());
        assertNotNull(found.createdAt());
        assertNotNull(found.updatedAt());
    }

    @Test
    void roundTripWithIdempotencyKey() {
        String key = "key-" + UUID.randomUUID();
        Jobs job = newJob(key);

        repository.insert(job);

        assertEquals(key, repository.findById(job.id()).orElseThrow().idempotencyKey());
    }

    @Test
    void duplicateIdempotencyKeyIsRejected() {
        String key = "key-" + UUID.randomUUID();
        repository.insert(newJob(key));

        assertThrows(DuplicateKeyException.class, () -> repository.insert(newJob(key)));
    }

    @Test
    void findByIdReturnsEmptyWhenMissing() {
        assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
    }
}