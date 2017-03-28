package br.com.pasquantonio.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

@Service
public class TimeIntervalService {


	private static final long TIME_INTERVAL_SECONDS = 60L;

	public boolean isInTimeInterval(long epochMilli) {
		return (getGreaterTimeWithinTimeInterval() <= epochMilli );
	}

	public long getGreaterTimeWithinTimeInterval(){
		return Instant.now().minusSeconds(TIME_INTERVAL_SECONDS).toEpochMilli();
	}
	
}
