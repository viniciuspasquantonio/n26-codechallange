package br.com.pasquantonio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.pasquantonio.component.StatisticsComponent;
import br.com.pasquantonio.model.Statistic;

@RestController
@RequestMapping("/")
public class StatisticsController {
	
	@Autowired	
	private StatisticsComponent statisticsComponent;
	
	
	@RequestMapping(value = "/statistics", method = RequestMethod.GET, consumes="application/json",produces="application/json")
	public ResponseEntity<Statistic> retrivePastSixtySecondsStatistics() {
		Statistic pastSixtySecondsSatistic = statisticsComponent.retriveAllStatisticsWithTimeGreaterThan();
		return new ResponseEntity<>(pastSixtySecondsSatistic,HttpStatus.OK);
	}

}
