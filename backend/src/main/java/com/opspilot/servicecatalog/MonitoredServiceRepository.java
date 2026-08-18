package com.opspilot.servicecatalog;

import java.util.Optional;
import java.util.UUID;

public interface MonitoredServiceRepository {

	MonitoredService save(MonitoredService monitoredService);

	Optional<MonitoredService> findById(UUID id);

}
