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

	@ExceptionHandler(InvalidIncidentStatusTransitionException.class)
	public ResponseEntity<ProblemDetail> handleInvalidStatusTransition(
			InvalidIncidentStatusTransitionException exception) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
			HttpStatus.CONFLICT,
			"Cannot move incident from " + exception.getCurrentStatus() + " to " + exception.getRequestedStatus());
		problemDetail.setTitle("Invalid incident status transition");
		problemDetail.setProperty("incidentId", exception.getIncidentId());
		problemDetail.setProperty("currentStatus", exception.getCurrentStatus());
		problemDetail.setProperty("requestedStatus", exception.getRequestedStatus());

		return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
	}

}
