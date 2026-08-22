package com.opspilot.incidents;

import java.net.URI;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

	private final IncidentApplicationService applicationService;

	public IncidentController(IncidentApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	@PostMapping
	public ResponseEntity<IncidentResponse> create(@Valid @RequestBody CreateIncidentRequest request) {
		Incident incident = applicationService.create(
			request.serviceId(),
			request.title(),
			request.description(),
			request.severity());
		IncidentResponse response = IncidentResponse.from(incident);

		return ResponseEntity
			.created(URI.create("/api/v1/incidents/" + response.id()))
			.body(response);
	}

	@GetMapping("/{id}")
	public IncidentResponse get(@PathVariable UUID id) {
		return IncidentResponse.from(applicationService.get(id));
	}

	@PatchMapping("/{id}/status")
	public IncidentResponse changeStatus(
			@PathVariable UUID id,
			@Valid @RequestBody UpdateIncidentStatusRequest request) {
		return IncidentResponse.from(applicationService.changeStatus(id, request.status()));
	}

}
