package com.opspilot.incidents;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class IncidentApiIntegrationTests {

	private final HttpClient httpClient = HttpClient.newHttpClient();

	@LocalServerPort
	private int port;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void createsIncidentForRegisteredService() throws Exception {
		ServiceApiResponse service = registerService("Checkout Service");

		HttpResponse<String> response = postIncident("""
			{
			  "serviceId": "%s",
			  "title": "Checkout latency spike",
			  "description": "P95 latency increased after deploy",
			  "severity": "SEV2"
			}
			""".formatted(service.id()));

		assertThat(response.statusCode()).isEqualTo(201);
		assertThat(response.headers().firstValue("Location")).hasValueSatisfying(location ->
			assertThat(location).matches("/api/v1/incidents/[0-9a-fA-F-]{36}"));

		IncidentApiResponse body = readIncidentResponse(response);
		assertThat(body.id()).isNotNull();
		assertThat(body.serviceId()).isEqualTo(service.id());
		assertThat(body.title()).isEqualTo("Checkout latency spike");
		assertThat(body.description()).isEqualTo("P95 latency increased after deploy");
		assertThat(body.severity()).isEqualTo(IncidentSeverity.SEV2);
		assertThat(body.status()).isEqualTo(IncidentStatus.OPEN);
		assertThat(body.createdAt()).isNotNull();
		assertThat(body.updatedAt()).isNotNull();
	}

	@Test
	void retrievesCreatedIncidentById() throws Exception {
		ServiceApiResponse service = registerService("Inventory Service");
		IncidentApiResponse created = createIncident(service.id(), "Inventory errors", IncidentSeverity.SEV3);

		HttpResponse<String> response = getIncident(created.id());

		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(readIncidentResponse(response)).isEqualTo(created);
	}

	@Test
	void rejectsBlankIncidentTitle() throws Exception {
		ServiceApiResponse service = registerService("Billing Service");

		HttpResponse<String> response = postIncident("""
			{
			  "serviceId": "%s",
			  "title": "   ",
			  "description": "Missing useful title",
			  "severity": "SEV3"
			}
			""".formatted(service.id()));

		assertThat(response.statusCode()).isEqualTo(400);
	}

	@Test
	void rejectsIncidentForUnknownService() throws Exception {
		UUID unknownServiceId = UUID.fromString("00000000-0000-0000-0000-000000000011");

		HttpResponse<String> response = postIncident("""
			{
			  "serviceId": "%s",
			  "title": "Worker failures",
			  "description": "Background jobs are failing",
			  "severity": "SEV2"
			}
			""".formatted(unknownServiceId));

		assertThat(response.statusCode()).isEqualTo(404);
		assertThat(response.body()).contains("\"title\":\"Monitored service not found\"");
		assertThat(response.body()).contains("\"serviceId\":\"" + unknownServiceId + "\"");
	}

	@Test
	void movesIncidentThroughInvestigatingAndResolved() throws Exception {
		ServiceApiResponse service = registerService("Payments Service");
		IncidentApiResponse created = createIncident(service.id(), "Payment failures", IncidentSeverity.SEV1);

		IncidentApiResponse investigating = updateIncidentStatus(created.id(), IncidentStatus.INVESTIGATING);
		assertThat(investigating.status()).isEqualTo(IncidentStatus.INVESTIGATING);

		IncidentApiResponse resolved = updateIncidentStatus(created.id(), IncidentStatus.RESOLVED);
		assertThat(resolved.status()).isEqualTo(IncidentStatus.RESOLVED);

		HttpResponse<String> response = getIncident(created.id());
		assertThat(readIncidentResponse(response).status()).isEqualTo(IncidentStatus.RESOLVED);
	}

	@Test
	void rejectsSkippingInvestigationStatus() throws Exception {
		ServiceApiResponse service = registerService("Notification Service");
		IncidentApiResponse created = createIncident(service.id(), "Notification backlog", IncidentSeverity.SEV4);

		HttpResponse<String> response = patchIncidentStatus(created.id(), IncidentStatus.RESOLVED);

		assertThat(response.statusCode()).isEqualTo(409);
		assertThat(response.body()).contains("\"title\":\"Invalid incident status transition\"");
		assertThat(response.body()).contains("\"currentStatus\":\"OPEN\"");
		assertThat(response.body()).contains("\"requestedStatus\":\"RESOLVED\"");
	}

	private ServiceApiResponse registerService(String name) throws Exception {
		HttpRequest request = HttpRequest.newBuilder(serviceUri())
			.header("Accept", "application/json")
			.header("Content-Type", "application/json")
			.POST(HttpRequest.BodyPublishers.ofString("""
				{
				  "name": "%s",
				  "description": "Owned by the incident API integration test"
				}
				""".formatted(name)))
			.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		assertThat(response.statusCode()).isEqualTo(201);
		return objectMapper.readValue(response.body(), ServiceApiResponse.class);
	}

	private IncidentApiResponse createIncident(UUID serviceId, String title, IncidentSeverity severity) throws Exception {
		HttpResponse<String> response = postIncident("""
			{
			  "serviceId": "%s",
			  "title": "%s",
			  "description": "Created by an integration test",
			  "severity": "%s"
			}
			""".formatted(serviceId, title, severity));

		assertThat(response.statusCode()).isEqualTo(201);
		return readIncidentResponse(response);
	}

	private IncidentApiResponse updateIncidentStatus(UUID incidentId, IncidentStatus status) throws Exception {
		HttpResponse<String> response = patchIncidentStatus(incidentId, status);
		assertThat(response.statusCode()).isEqualTo(200);
		return readIncidentResponse(response);
	}

	private HttpResponse<String> postIncident(String body) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder(incidentUri())
			.header("Accept", "application/json")
			.header("Content-Type", "application/json")
			.POST(HttpRequest.BodyPublishers.ofString(body))
			.build();

		return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private HttpResponse<String> getIncident(UUID id) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder(incidentUri().resolve("/api/v1/incidents/" + id))
			.header("Accept", "application/json")
			.GET()
			.build();

		return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private HttpResponse<String> patchIncidentStatus(UUID id, IncidentStatus status) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder(incidentUri().resolve("/api/v1/incidents/" + id + "/status"))
			.header("Accept", "application/json")
			.header("Content-Type", "application/json")
			.method("PATCH", HttpRequest.BodyPublishers.ofString("""
				{
				  "status": "%s"
				}
				""".formatted(status)))
			.build();

		return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private IncidentApiResponse readIncidentResponse(HttpResponse<String> response) throws IOException {
		return objectMapper.readValue(response.body(), IncidentApiResponse.class);
	}

	private URI incidentUri() {
		return URI.create("http://localhost:" + port + "/api/v1/incidents");
	}

	private URI serviceUri() {
		return URI.create("http://localhost:" + port + "/api/v1/services");
	}

	private record ServiceApiResponse(UUID id, String name, String description, Instant createdAt) {
	}

	private record IncidentApiResponse(
			UUID id,
			UUID serviceId,
			String title,
			String description,
			IncidentSeverity severity,
			IncidentStatus status,
			Instant createdAt,
			Instant updatedAt) {
	}

}
