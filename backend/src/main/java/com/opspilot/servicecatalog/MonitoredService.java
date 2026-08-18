package com.opspilot.servicecatalog;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record MonitoredService(UUID id, String name, String description, Instant createdAt) {

	public MonitoredService {
		Objects.requireNonNull(id, "id must not be null");
		Objects.requireNonNull(name, "name must not be null");
		Objects.requireNonNull(createdAt, "createdAt must not be null");
	}

}
