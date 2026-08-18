package com.opspilot.servicecatalog;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = MonitoredServiceController.class)
public class ServiceCatalogExceptionHandler {

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
