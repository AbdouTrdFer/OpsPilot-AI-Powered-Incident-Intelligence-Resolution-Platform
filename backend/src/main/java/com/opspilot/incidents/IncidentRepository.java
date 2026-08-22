package com.opspilot.incidents;

import java.util.Optional;
import java.util.UUID;

public interface IncidentRepository {

	Incident save(Incident incident);

	Optional<Incident> findById(UUID id);

}
