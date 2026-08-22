package com.opspilot.incidents;

import com.opspilot.servicecatalog.MonitoredServiceNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = IncidentController.class)
public class IncidentExceptionHandler {

	@ExceptionHandler(IncidentNotFoundException.class)
	public ResponseEntity<ProblemDetail> handleIncidentNotFound(IncidentNotFoundException exception) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
			HttpStatus.NOT_FOUND,
			"No incident exists with id " + exception.getIncidentId());
		problemDetail.setTitle("Incident not found");
		problemDetail.setProperty("incidentId", exception.getIncidentId());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
	}

	@ExceptionHandler(MonitoredServiceNotFoundException.class)
	public ResponseEntity<ProblemDetail> handleMonitoredServiceNotFound(MonitoredServiceNotFoundException exception) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
			HttpStatus.NOT_FOUND,
			"No monitored service exists with id " + exception.getServiceId());
		problemDetail.setTitle("Monitored service not found");
		problemDetail.setProperty("serviceId", exception.getServiceId());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
	}

}
