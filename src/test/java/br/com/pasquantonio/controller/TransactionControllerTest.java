package br.com.pasquantonio.controller;

import static com.jayway.restassured.RestAssured.given;

import java.time.Instant;

import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pasquantonio.component.SingletonStatisticsMap;
import br.com.pasquantonio.model.Transaction;
import br.com.pasquantonio.service.TimeIntervalService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionControllerTest {

	@LocalServerPort
	private int port;
	
	@Autowired
	private SingletonStatisticsMap singletonStatisticsMap;
	
	@Autowired
	private TimeIntervalService timeIntervalService;
	
	
	private String transactionAsString;
	
	@Before
	public void setup() {
		Transaction transaction = new Transaction(Instant.now().toEpochMilli(), 100D);
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
		singletonStatisticsMap.getInstance().clear();
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
		singletonStatisticsMap.getInstance().clear();
	}
	
	@Test
	public void shouldReturnNoContentWhenTransactionTimeIsNotInTimeInterval() throws JsonProcessingException {
		Transaction transaction = new Transaction(Instant.now().minusSeconds(100).toEpochMilli(), 100D);
		
		given()
			.port(port)
			.contentType("application/json")
			.body(new ObjectMapper().writeValueAsString(transaction))
		.when()
			.post("/transactions")
		.then()
			.statusCode(HttpStatus.SC_NO_CONTENT);
		
		singletonStatisticsMap.getInstance().clear();
	}
}
