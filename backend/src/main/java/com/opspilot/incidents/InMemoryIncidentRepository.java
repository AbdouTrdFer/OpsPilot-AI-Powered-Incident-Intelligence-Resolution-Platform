package com.opspilot.incidents;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Repository;

@Repository
public class InMemoryIncidentRepository implements IncidentRepository {

	private final ConcurrentMap<UUID, Incident> incidents = new ConcurrentHashMap<>();

	@Override
	public Incident save(Incident incident) {
		incidents.put(incident.id(), incident);
		return incident;
	}

	@Override
	public Optional<Incident> findById(UUID id) {
		return Optional.ofNullable(incidents.get(id));
	}

}
