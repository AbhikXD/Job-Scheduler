CREATE TABLE jobs (
                      id              UUID PRIMARY KEY,
                      type            VARCHAR(64)  NOT NULL,
                      payload         JSONB        NOT NULL,
                      status          VARCHAR(16)  NOT NULL,
                      run_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
                      attempts        INT          NOT NULL DEFAULT 0,
                      max_attempts    INT          NOT NULL DEFAULT 5,
                      locked_by       VARCHAR(64),
                      locked_until    TIMESTAMPTZ,
                      idempotency_key VARCHAR(128) UNIQUE,
                      last_error      TEXT,
                      created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                      updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_jobs_due ON jobs (status, run_at);