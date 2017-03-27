package br.com.pasquantonio.controller;

import static com.jayway.restassured.RestAssured.given;

import java.time.Instant;

import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pasquantonio.model.Transaction;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionControllerTest {

	@LocalServerPort
	private int port;
	
	private String transactionAsString;
	@Before
	public void setup() {
		Transaction transaction = new Transaction(Instant.now().getEpochSecond(), 100D);
		try {
			transactionAsString = new ObjectMapper().writeValueAsString(transaction);
		} catch (JsonProcessingException e) {
			Assertions.fail(e.getMessage());
		}
	}
	@Test
	public void shouldReturnCreatedWithNewTransactionTime() throws JsonProcessingException {
		given()
			.port(port)
			.contentType("application/json")
			.body(transactionAsString)
		.when()
			.post("/transactions")
		.then()
			.statusCode(HttpStatus.SC_CREATED);
	}
	
	@Test
	public void shouldReturnNoContentWhenTransactionTimeAlreadyCreated() throws JsonProcessingException {
		given()
			.port(port)
			.contentType("application/json")
			.body(transactionAsString)
		.when()
			.post("/transactions")
		.then()
			.statusCode(HttpStatus.SC_CREATED);
		
		given()
			.port(port)
			.contentType("application/json")
			.body(transactionAsString)
		.when()
			.post("/transactions")
		.then()
			.statusCode(HttpStatus.SC_NO_CONTENT);
	}
}
