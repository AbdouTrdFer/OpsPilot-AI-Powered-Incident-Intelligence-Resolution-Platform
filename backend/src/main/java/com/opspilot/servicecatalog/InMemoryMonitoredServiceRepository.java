package com.opspilot.servicecatalog;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Repository;

@Repository
public class InMemoryMonitoredServiceRepository implements MonitoredServiceRepository {

	private final ConcurrentMap<UUID, MonitoredService> services = new ConcurrentHashMap<>();

	@Override
	public MonitoredService save(MonitoredService monitoredService) {
		services.put(monitoredService.id(), monitoredService);
		return monitoredService;
	}

	@Override
	public Optional<MonitoredService> findById(UUID id) {
		return Optional.ofNullable(services.get(id));
	}

}
