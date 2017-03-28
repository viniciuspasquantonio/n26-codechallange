package br.com.pasquantonio.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.pasquantonio.component.StatisticsComponent;
import br.com.pasquantonio.model.Transaction;
import br.com.pasquantonio.service.TimeIntervalService;

@RestController
@RequestMapping("/")
public class TransactionController {
	
	@Autowired	
	private StatisticsComponent statisticsComponent;
	
	
	@Autowired
	private TimeIntervalService timeIntervalService;
	
	@RequestMapping(value = "/transactions", method = RequestMethod.POST, consumes="application/json")
	public ResponseEntity<Object> update(@RequestBody @Valid Transaction transaction) {
		if(!timeIntervalService.isInTimeInterval(transaction.getTime())){
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		ResponseEntity<Object> responseEntity;
		if(statisticsComponent.containsKey(transaction.getTime())){
			responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}else{
			responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
		}
		statisticsComponent.postTransaction(transaction);
		return responseEntity;
	}

}
