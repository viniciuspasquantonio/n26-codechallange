package br.com.pasquantonio.service;

import org.springframework.stereotype.Service;

import br.com.pasquantonio.model.Statistic;
import br.com.pasquantonio.model.Transaction;

@Service
public class TransactionAccountantService {

	public Statistic account(Statistic statistic, Transaction transaction) {
		if(statistic.getMax() < transaction.getAmount()){
			statistic.setMax(transaction.getAmount());
		}
		if(statistic.getMin() > transaction.getAmount()){
			statistic.setMin(transaction.getAmount());
		}
		statistic.setSum(statistic.getSum() + transaction.getAmount());		
		statistic.setCount(statistic.getCount()+1);
		statistic.setAvg(statistic.getSum()/statistic.getCount());
		return statistic;
		
			
	}

	public Statistic account(Statistic recentStatistic, Statistic statistic) {
		if(recentStatistic.getMax() < statistic.getMax()){
			recentStatistic.setMax(statistic.getMax());
		}
		if(recentStatistic.getMin() > statistic.getMin()){
			recentStatistic.setMin(statistic.getMin());
		}
		recentStatistic.setSum(recentStatistic.getSum() + statistic.getSum());		
		recentStatistic.setCount(recentStatistic.getCount()+statistic.getCount());
		recentStatistic.setAvg(recentStatistic.getSum()/recentStatistic.getCount());
		return recentStatistic;
	}

}
