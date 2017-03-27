package br.com.pasquantonio.controller;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
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
import com.jayway.restassured.response.Response;

import br.com.pasquantonio.component.SingletonStatisticsMap;
import br.com.pasquantonio.model.Statistic;
import br.com.pasquantonio.model.Transaction;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatisticControllerTest {

	@LocalServerPort
	private int port;

	private String transactionAsString;
	
	@Autowired
	private SingletonStatisticsMap singletonStatisticsMap;

	@Before
	public void setup() {
		Transaction mostRecentTransaction = new Transaction(Instant.now().getEpochSecond(), 100D);
		try {
			transactionAsString = new ObjectMapper().writeValueAsString(mostRecentTransaction);
		} catch (JsonProcessingException e) {
			Assertions.fail(e.getMessage());
		}
	}

	@Test
	public void shouldReturnStatisticFromOneTransaction() throws IOException {
		given()
			.port(port)
			.contentType("application/json")
			.body(transactionAsString)
		.when()
			.post("/transactions")
		.then()
			.statusCode(HttpStatus.SC_CREATED);

		Response response = getStatisticsEndPoint();
		Statistic statistic = new ObjectMapper().readValue(response.getBody().asString(), Statistic.class);
		
		assertEquals(statistic.getAvg(), 100D,0);
		assertEquals(statistic.getMax(), 100D,0);
		assertEquals(statistic.getMin(), 100D,0);
		assertEquals(statistic.getSum(), 100D,0);
		assertEquals(statistic.getCount(), 1,0);
		
		singletonStatisticsMap.getInstance().clear();
		
	}
	
	@Test
	public void shouldReturnStatisticFromTransactionWithSameTime() throws IOException {
		long now = Instant.now().getEpochSecond();
		Transaction firstTransaction = new Transaction(now, 100D);
		Transaction secondTransaction = new Transaction(now, 200D);
		Transaction thirdTransaction = new Transaction(now, 300.50D);
		
		callPostTransaction(new ObjectMapper().writeValueAsString(firstTransaction),HttpStatus.SC_CREATED);
		callPostTransaction(new ObjectMapper().writeValueAsString(secondTransaction),HttpStatus.SC_NO_CONTENT);
		callPostTransaction(new ObjectMapper().writeValueAsString(thirdTransaction),HttpStatus.SC_NO_CONTENT);

		Response response = getStatisticsEndPoint();
		Statistic statistic = new ObjectMapper().readValue(response.getBody().asString(), Statistic.class);
		
		assertEquals(statistic.getAvg(), 200.166D,0.001);
		assertEquals(statistic.getMax(), 300.50,0);
		assertEquals(statistic.getMin(), 100D,0);
		assertEquals(statistic.getSum(), 600.50,0);
		assertEquals(statistic.getCount(), 3,0);
		
		singletonStatisticsMap.getInstance().clear();
	}
	
	@Test
	public void shouldReturnStatisticFromTransactionWithDifferentTimes() throws IOException {
		long now = Instant.now().getEpochSecond();
		long tenSecondsAgo = Instant.now().getEpochSecond() - 10000;
		Transaction firstTransaction = new Transaction(now, 100D);
		Transaction secondTransaction = new Transaction(now, 200D);
		Transaction thirdTransaction = new Transaction(now, 300D);
		Transaction fourthTransaction = new Transaction(tenSecondsAgo, 100D);
		Transaction fifthTransaction = new Transaction(tenSecondsAgo, 100D);
		
		callPostTransaction(new ObjectMapper().writeValueAsString(firstTransaction),HttpStatus.SC_CREATED);
		callPostTransaction(new ObjectMapper().writeValueAsString(secondTransaction),HttpStatus.SC_NO_CONTENT);
		callPostTransaction(new ObjectMapper().writeValueAsString(thirdTransaction),HttpStatus.SC_NO_CONTENT);
		callPostTransaction(new ObjectMapper().writeValueAsString(fourthTransaction),HttpStatus.SC_CREATED);
		callPostTransaction(new ObjectMapper().writeValueAsString(fifthTransaction),HttpStatus.SC_NO_CONTENT);

		Response response = getStatisticsEndPoint();
		Statistic statistic = new ObjectMapper().readValue(response.getBody().asString(), Statistic.class);
		
		assertEquals(statistic.getAvg(), 160D,0);
		assertEquals(statistic.getMax(), 300D,0);
		assertEquals(statistic.getMin(), 100D,0);
		assertEquals(statistic.getSum(), 800,0);
		assertEquals(statistic.getCount(), 5,0);
		
		singletonStatisticsMap.getInstance().clear();
	}
	
	@Test
	public void shouldNotAccountStatisticWithTimeOlderThanSixtySeconds() throws IOException {
		long now = Instant.now().getEpochSecond();
		long tenSecondsAgo = Instant.now().getEpochSecond() - 10000;
		long twoMinutesAgo = Instant.now().getEpochSecond() - 120000;
		Transaction firstTransaction = new Transaction(now, 100D);
		Transaction secondTransaction = new Transaction(now, 200D);
		Transaction thirdTransaction = new Transaction(now, 300D);
		Transaction fourthTransaction = new Transaction(tenSecondsAgo, 100D);
		Transaction fifthTransaction = new Transaction(tenSecondsAgo, 100D);
		Transaction twoMinutesOlderTransaction = new Transaction(twoMinutesAgo, 100D);
		
		callPostTransaction(new ObjectMapper().writeValueAsString(firstTransaction),HttpStatus.SC_CREATED);
		callPostTransaction(new ObjectMapper().writeValueAsString(secondTransaction),HttpStatus.SC_NO_CONTENT);
		callPostTransaction(new ObjectMapper().writeValueAsString(thirdTransaction),HttpStatus.SC_NO_CONTENT);
		callPostTransaction(new ObjectMapper().writeValueAsString(fourthTransaction),HttpStatus.SC_CREATED);
		callPostTransaction(new ObjectMapper().writeValueAsString(fifthTransaction),HttpStatus.SC_NO_CONTENT);
		callPostTransaction(new ObjectMapper().writeValueAsString(twoMinutesOlderTransaction),HttpStatus.SC_CREATED);

		Response response = getStatisticsEndPoint();
		Statistic statistic = new ObjectMapper().readValue(response.getBody().asString(), Statistic.class);
		
		assertEquals(statistic.getAvg(), 160D,0);
		assertEquals(statistic.getMax(), 300D,0);
		assertEquals(statistic.getMin(), 100D,0);
		assertEquals(statistic.getSum(), 800,0);
		assertEquals(statistic.getCount(), 5,0);
		
		singletonStatisticsMap.getInstance().clear();
	}

	private void callPostTransaction(String transactionAsString,int httpStatusCode) {
		given()
			.port(port)
			.contentType("application/json")
			.body(transactionAsString)
		.when()
			.post("/transactions")
		.then()
			.statusCode(httpStatusCode);
	}

	private Response getStatisticsEndPoint() {
		return  given()
					.port(port)
					.contentType("application/json")
				.when()
					.get("/statistics")
				.then()
					.statusCode(HttpStatus.SC_OK)
					.extract().response();
	}

}
