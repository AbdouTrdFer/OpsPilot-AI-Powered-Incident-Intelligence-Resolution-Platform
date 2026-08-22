package com.opspilot.incidents;

import java.util.UUID;

public class InvalidIncidentStatusTransitionException extends RuntimeException {

	private final UUID incidentId;
	private final IncidentStatus currentStatus;
	private final IncidentStatus requestedStatus;

	public InvalidIncidentStatusTransitionException(
			UUID incidentId,
			IncidentStatus currentStatus,
			IncidentStatus requestedStatus) {
		super("Cannot move incident %s from %s to %s".formatted(incidentId, currentStatus, requestedStatus));
		this.incidentId = incidentId;
		this.currentStatus = currentStatus;
		this.requestedStatus = requestedStatus;
	}

	public UUID getIncidentId() {
		return incidentId;
	}

	public IncidentStatus getCurrentStatus() {
		return currentStatus;
	}

	public IncidentStatus getRequestedStatus() {
		return requestedStatus;
	}

}
