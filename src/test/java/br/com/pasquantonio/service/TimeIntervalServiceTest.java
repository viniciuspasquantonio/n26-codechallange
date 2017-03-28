package br.com.pasquantonio.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.time.Instant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TimeIntervalServiceTest {

	private static final long TIME_INTERVAL_SECONDS = 60L;
	@Autowired
	private TimeIntervalService timeIntervalService;
	
	@Test
	public void shouldReturnTrueWhenTimeIsInTheMidleOfTimeInterval(){
		assertTrue(timeIntervalService.isInTimeInterval(Instant.now().toEpochMilli()));
	}
	
	@Test
	public void shouldReturnTrueWhenTimeIsInTheLimitOfTimeInterval(){
		assertTrue(timeIntervalService.isInTimeInterval(Instant.now().minusSeconds(TIME_INTERVAL_SECONDS).toEpochMilli()));
	}
	
	@Test
	public void shouldFalseTrueWhenTimeIsNotInTimeInterval(){
		assertFalse(timeIntervalService.isInTimeInterval(Instant.now().minusSeconds(120L).toEpochMilli()));
	}

}
