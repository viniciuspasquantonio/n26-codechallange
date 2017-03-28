package br.com.pasquantonio.component;

import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.pasquantonio.model.Statistic;
import br.com.pasquantonio.model.Transaction;
import br.com.pasquantonio.service.TimeIntervalService;
import br.com.pasquantonio.service.TransactionAccountantService;

@Component
public class StatisticsComponent {
	@Autowired
	private TransactionAccountantService transactionAccountantService;
	
	@Autowired
	private TimeIntervalService timeIntervalService;

	private ConcurrentNavigableMap<Long, Statistic> statisticsMap;

	public StatisticsComponent() {
		this.statisticsMap = new ConcurrentSkipListMap<>();
	}

	public synchronized  void postTransaction(Transaction transaction) {
		Statistic statistic = new Statistic();
		if (statisticsMap.containsKey(transaction.getTime())) {
			statistic = statisticsMap.get(transaction.getTime());
		}
		statistic = transactionAccountantService.account(statistic, transaction);
		statisticsMap.put(transaction.getTime(), statistic);
		
	}


	public synchronized  Statistic retriveAllStatisticsWithTimeGreaterThan() {
		clearStatisticsNotInTimeInterval();
		Statistic recentStatistic = new Statistic();
		for (Statistic statistic : statisticsMap.values()) {
			recentStatistic = transactionAccountantService.account(recentStatistic, statistic);
		};
		return recentStatistic;
	}
	
	public synchronized  void clearStatisticsNotInTimeInterval(){
		statisticsMap = statisticsMap.tailMap(timeIntervalService.getGreaterTimeWithinTimeInterval());
	}

	public synchronized boolean containsKey(long time) {
		return statisticsMap.containsKey(time);
	}

	public synchronized Statistic get(long time) {
		return statisticsMap.get(time);
	}

	public synchronized void clear() {
		statisticsMap.clear();
		
	}

	public synchronized void put(long time, Statistic accountedStatistic) {
		statisticsMap.put(time, accountedStatistic);
		
	}
	
	
}
