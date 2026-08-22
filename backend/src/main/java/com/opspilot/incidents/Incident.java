package com.opspilot.incidents;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Incident(
		UUID id,
		UUID serviceId,
		String title,
		String description,
		IncidentSeverity severity,
		IncidentStatus status,
		Instant createdAt,
		Instant updatedAt) {

	public Incident {
		Objects.requireNonNull(id, "id must not be null");
		Objects.requireNonNull(serviceId, "serviceId must not be null");
		Objects.requireNonNull(title, "title must not be null");
		Objects.requireNonNull(severity, "severity must not be null");
		Objects.requireNonNull(status, "status must not be null");
		Objects.requireNonNull(createdAt, "createdAt must not be null");
		Objects.requireNonNull(updatedAt, "updatedAt must not be null");
	}

	public Incident withStatus(IncidentStatus nextStatus, Instant updatedAt) {
		return new Incident(id, serviceId, title, description, severity, nextStatus, createdAt, updatedAt);
	}

}
