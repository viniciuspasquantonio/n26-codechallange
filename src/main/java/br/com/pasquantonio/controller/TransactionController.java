package br.com.pasquantonio.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.pasquantonio.component.SingletonStatisticsMap;
import br.com.pasquantonio.model.Transaction;
import br.com.pasquantonio.service.StatisticsService;

@RestController
@RequestMapping("/")
public class TransactionController {
	
	@Autowired	
	private StatisticsService statisticsService;
	
	@Autowired
	private SingletonStatisticsMap singletonStatisticsMap;
	
	@RequestMapping(value = "/transactions", method = RequestMethod.POST, consumes="application/json")
	public ResponseEntity<Object> update(@RequestBody @Valid Transaction transaction) {
		ResponseEntity<Object> responseEntity;
		if(singletonStatisticsMap.getInstance().containsKey(transaction.getTime())){
			responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}else{
			responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
		}
		statisticsService.postTransaction(transaction, singletonStatisticsMap.getInstance());
		return responseEntity;
	}

}
