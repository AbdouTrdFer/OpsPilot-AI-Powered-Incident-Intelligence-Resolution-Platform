package com.opspilot.servicecatalog;

import java.time.Instant;
import java.util.UUID;

public record MonitoredServiceResponse(UUID id, String name, String description, Instant createdAt) {

	public static MonitoredServiceResponse from(MonitoredService monitoredService) {
		return new MonitoredServiceResponse(
			monitoredService.id(),
			monitoredService.name(),
			monitoredService.description(),
			monitoredService.createdAt());
	}

}
