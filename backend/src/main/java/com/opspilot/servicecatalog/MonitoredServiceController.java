package com.opspilot.servicecatalog;

import java.net.URI;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/services")
public class MonitoredServiceController {

	private final MonitoredServiceApplicationService applicationService;

	public MonitoredServiceController(MonitoredServiceApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	@PostMapping
	public ResponseEntity<MonitoredServiceResponse> register(
			@Valid @RequestBody RegisterMonitoredServiceRequest request) {
		MonitoredService monitoredService = applicationService.register(request.name(), request.description());
		MonitoredServiceResponse response = MonitoredServiceResponse.from(monitoredService);

		return ResponseEntity
			.created(URI.create("/api/v1/services/" + response.id()))
			.body(response);
	}

	@GetMapping("/{id}")
	public MonitoredServiceResponse get(@PathVariable UUID id) {
		return MonitoredServiceResponse.from(applicationService.get(id));
	}

}
