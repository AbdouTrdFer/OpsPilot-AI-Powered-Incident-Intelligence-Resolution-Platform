package com.opspilot.incidents;

import jakarta.validation.constraints.NotNull;

public record UpdateIncidentStatusRequest(
		@NotNull(message = "status is required")
		IncidentStatus status) {
}
