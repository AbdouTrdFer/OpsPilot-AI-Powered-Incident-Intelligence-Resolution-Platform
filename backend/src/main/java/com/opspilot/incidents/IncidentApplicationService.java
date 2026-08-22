package com.opspilot.incidents;

import java.time.Instant;
import java.util.UUID;

import com.opspilot.servicecatalog.MonitoredServiceApplicationService;

import org.springframework.stereotype.Service;

@Service
public class IncidentApplicationService {

	private final IncidentRepository incidentRepository;
	private final MonitoredServiceApplicationService serviceCatalog;

	public IncidentApplicationService(
			IncidentRepository incidentRepository,
			MonitoredServiceApplicationService serviceCatalog) {
		this.incidentRepository = incidentRepository;
		this.serviceCatalog = serviceCatalog;
	}

	public Incident create(UUID serviceId, String title, String description, IncidentSeverity severity) {
		serviceCatalog.get(serviceId);

		Instant now = Instant.now();
		Incident incident = new Incident(
			UUID.randomUUID(),
			serviceId,
			title.trim(),
			normalizeDescription(description),
			severity,
			IncidentStatus.OPEN,
			now,
			now);

		return incidentRepository.save(incident);
	}

	public Incident get(UUID id) {
		return incidentRepository.findById(id)
			.orElseThrow(() -> new IncidentNotFoundException(id));
	}

	public Incident changeStatus(UUID id, IncidentStatus nextStatus) {
		Incident incident = get(id);
		if (incident.status() == nextStatus) {
			return incident;
		}

		if (!canMoveTo(incident.status(), nextStatus)) {
			throw new InvalidIncidentStatusTransitionException(incident.id(), incident.status(), nextStatus);
		}

		return incidentRepository.save(incident.withStatus(nextStatus, Instant.now()));
	}

	private boolean canMoveTo(IncidentStatus currentStatus, IncidentStatus nextStatus) {
		return (currentStatus == IncidentStatus.OPEN && nextStatus == IncidentStatus.INVESTIGATING)
			|| (currentStatus == IncidentStatus.INVESTIGATING && nextStatus == IncidentStatus.RESOLVED);
	}

	private String normalizeDescription(String description) {
		if (description == null) {
			return null;
		}

		return description.trim();
	}

}
