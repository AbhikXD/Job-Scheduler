package com.project.JobScheduler;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

public class JobRowMapper implements RowMapper<Jobs> {

    @Override
    public Jobs mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Jobs(
                rs.getObject("id", UUID.class),
                rs.getString("type"),
                rs.getString("payload"),
                JobStatus.valueOf(rs.getString("status")),
                instant(rs,"run_at"),
                rs.getInt("attempts"),
                rs.getInt("max_attempts"),
                rs.getString("locked_by"),
                instant(rs,"locked_util"),
                rs.getString("idempotency_key"),
                rs.getString("last_error"),
                instant(rs,"created_at"),
                instant(rs,"updated_at")
        );
    }
    private static Instant instant(ResultSet rs, String column) throws SQLException {
        OffsetDateTime  value= rs.getObject(column,OffsetDateTime.class);
        return value == null?null:value.toInstant();
    }

}
