package com.opspilot.servicecatalog;

import java.util.UUID;

public class MonitoredServiceNotFoundException extends RuntimeException {

	private final UUID serviceId;

	public MonitoredServiceNotFoundException(UUID serviceId) {
		super("Monitored service not found: " + serviceId);
		this.serviceId = serviceId;
	}

	public UUID getServiceId() {
		return serviceId;
	}

}
