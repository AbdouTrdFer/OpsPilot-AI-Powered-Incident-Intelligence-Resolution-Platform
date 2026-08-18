package com.opspilot.servicecatalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterMonitoredServiceRequest(
		@NotBlank(message = "name is required")
		@Size(max = 100, message = "name must be at most 100 characters")
		String name,

		@Size(max = 500, message = "description must be at most 500 characters")
		String description) {
}
