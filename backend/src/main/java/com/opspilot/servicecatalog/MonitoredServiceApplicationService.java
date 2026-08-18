package com.opspilot.servicecatalog;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class MonitoredServiceApplicationService {

	private final MonitoredServiceRepository repository;

	public MonitoredServiceApplicationService(MonitoredServiceRepository repository) {
		this.repository = repository;
	}

	public MonitoredService register(String name, String description) {
		MonitoredService monitoredService = new MonitoredService(
			UUID.randomUUID(),
			name.trim(),
			normalizeDescription(description),
			Instant.now());

		return repository.save(monitoredService);
	}

	public MonitoredService get(UUID id) {
		return repository.findById(id)
			.orElseThrow(() -> new MonitoredServiceNotFoundException(id));
	}

	private String normalizeDescription(String description) {
		if (description == null) {
			return null;
		}

		return description.trim();
	}

}
