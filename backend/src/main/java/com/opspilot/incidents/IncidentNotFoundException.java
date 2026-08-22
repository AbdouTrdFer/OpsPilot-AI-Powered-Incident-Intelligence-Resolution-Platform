package com.opspilot.incidents;

import java.util.UUID;

public class IncidentNotFoundException extends RuntimeException {

	private final UUID incidentId;

	public IncidentNotFoundException(UUID incidentId) {
		super("Incident not found: " + incidentId);
		this.incidentId = incidentId;
	}

	public UUID getIncidentId() {
		return incidentId;
	}

}
