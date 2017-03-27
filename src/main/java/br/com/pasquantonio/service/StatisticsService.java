package br.com.pasquantonio.service;

import java.util.concurrent.ConcurrentNavigableMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.pasquantonio.model.Statistic;
import br.com.pasquantonio.model.Transaction;

@Service
public class StatisticsService {
	@Autowired
	private TransactionAccountantService transactionAccountantService;


	public void postTransaction(Transaction transaction, ConcurrentNavigableMap<Long, Statistic> statisticsMap) {
		Statistic statistic = new Statistic();
		if (statisticsMap.containsKey(transaction.getTime())) {
			statistic = statisticsMap.get(transaction.getTime());
		}
		statistic = transactionAccountantService.account(statistic, transaction);
		statisticsMap.put(transaction.getTime(), statistic);
	}
}