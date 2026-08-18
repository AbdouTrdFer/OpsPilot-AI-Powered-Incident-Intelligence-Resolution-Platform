package com.opspilot.servicecatalog;

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
class MonitoredServiceApiIntegrationTests {

	private final HttpClient httpClient = HttpClient.newHttpClient();

	@LocalServerPort
	private int port;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void registersValidService() throws Exception {
		HttpResponse<String> response = postService("""
			{
			  "name": "Payment Service",
			  "description": "Processes customer payments"
			}
			""");

		assertThat(response.statusCode()).isEqualTo(201);
		assertThat(response.headers().firstValue("Location")).hasValueSatisfying(location ->
			assertThat(location).matches("/api/v1/services/[0-9a-fA-F-]{36}"));

		MonitoredServiceApiResponse body = readServiceResponse(response);
		assertThat(body.id()).isNotNull();
		assertThat(body.name()).isEqualTo("Payment Service");
		assertThat(body.description()).isEqualTo("Processes customer payments");
		assertThat(body.createdAt()).isNotNull();
	}

	@Test
	void retrievesCreatedServiceById() throws Exception {
		MonitoredServiceApiResponse created = registerService("Inventory Service", "Tracks available stock");

		HttpResponse<String> response = getService(created.id());

		assertThat(response.statusCode()).isEqualTo(200);

		MonitoredServiceApiResponse retrieved = readServiceResponse(response);
		assertThat(retrieved).isEqualTo(created);
	}

	@Test
	void rejectsBlankName() throws Exception {
		HttpResponse<String> response = postService("""
			{
			  "name": "   ",
			  "description": "Missing a useful name"
			}
			""");

		assertThat(response.statusCode()).isEqualTo(400);
	}

	@Test
	void returnsNotFoundForUnknownServiceId() throws Exception {
		UUID unknownId = UUID.fromString("00000000-0000-0000-0000-000000000001");

		HttpResponse<String> response = getService(unknownId);

		assertThat(response.statusCode()).isEqualTo(404);
		assertThat(response.body()).contains("\"title\":\"Monitored service not found\"");
		assertThat(response.body()).contains("\"serviceId\":\"" + unknownId + "\"");
	}

	private MonitoredServiceApiResponse registerService(String name, String description) throws Exception {
		String body = """
			{
			  "name": "%s",
			  "description": "%s"
			}
			""".formatted(name, description);

		HttpResponse<String> response = postService(body);
		assertThat(response.statusCode()).isEqualTo(201);
		return readServiceResponse(response);
	}

	private HttpResponse<String> postService(String body) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder(serviceUri())
			.header("Accept", "application/json")
			.header("Content-Type", "application/json")
			.POST(HttpRequest.BodyPublishers.ofString(body))
			.build();

		return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private HttpResponse<String> getService(UUID id) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder(serviceUri().resolve("/api/v1/services/" + id))
			.header("Accept", "application/json")
			.GET()
			.build();

		return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private MonitoredServiceApiResponse readServiceResponse(HttpResponse<String> response) throws IOException {
		return objectMapper.readValue(response.body(), MonitoredServiceApiResponse.class);
	}

	private URI serviceUri() {
		return URI.create("http://localhost:" + port + "/api/v1/services");
	}

	private record MonitoredServiceApiResponse(UUID id, String name, String description, Instant createdAt) {
	}

}
