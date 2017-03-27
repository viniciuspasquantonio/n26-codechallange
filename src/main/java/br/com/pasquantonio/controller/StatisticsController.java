package br.com.pasquantonio.controller;

import java.time.Instant;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.pasquantonio.component.SingletonStatisticsMap;
import br.com.pasquantonio.model.Statistic;
import br.com.pasquantonio.model.Transaction;
import br.com.pasquantonio.service.StatisticsService;

@RestController
@RequestMapping("/")
public class StatisticsController {
	
	private static final int SIXTY_SECONDS = 60000;
	@Autowired	
	private StatisticsService statisticsService;
	
	@Autowired
	private SingletonStatisticsMap singletonStatisticsMap;
	
	@RequestMapping(value = "/statistics", method = RequestMethod.GET, consumes="application/json",produces="application/json")
	public ResponseEntity<Statistic> retrivePastSixtySecondsStatistics() {
		long sixtySecondsAgo = Instant.now().getEpochSecond() - SIXTY_SECONDS ;
		Statistic pastSixtySecondsSatistic = statisticsService.retriveAllStatisticsWithTimeGreaterThan(sixtySecondsAgo, singletonStatisticsMap.getInstance());
		return new ResponseEntity<>(pastSixtySecondsSatistic,HttpStatus.OK);
	}

}
