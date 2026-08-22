package com.opspilot.incidents;

import java.time.Instant;
import java.util.UUID;

public record IncidentResponse(
		UUID id,
		UUID serviceId,
		String title,
		String description,
		IncidentSeverity severity,
		IncidentStatus status,
		Instant createdAt,
		Instant updatedAt) {

	public static IncidentResponse from(Incident incident) {
		return new IncidentResponse(
			incident.id(),
			incident.serviceId(),
			incident.title(),
			incident.description(),
			incident.severity(),
			incident.status(),
			incident.createdAt(),
			incident.updatedAt());
	}

}
