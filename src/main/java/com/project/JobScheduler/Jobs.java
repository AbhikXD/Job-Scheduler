package com.project.JobScheduler;

import java.time.Instant;
import java.util.UUID;

public record Jobs(
        UUID id,
        String type,
        String payload,          // raw JSON text; handlers parse it
        JobStatus status,
        Instant runAt,
        int attempts,
        int maxAttempts,
        String lockedBy,         // null when not claimed
        Instant lockedUntil,     // null when not claimed
        String idempotencyKey,   // may be null
        String lastError,        // null until a failure
        Instant createdAt,
        Instant updatedAt
) {}