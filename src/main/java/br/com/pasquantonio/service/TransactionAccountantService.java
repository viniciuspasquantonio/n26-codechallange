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
		if(statistic.getCount() == 0){
			statistic.setAvg(transaction.getAmount());
		}else{
			statistic.setAvg((statistic.getAvg()+transaction.getAmount())/2);
		}
		statistic.setCount(statistic.getCount()+1);
		return statistic;
		
			
	}

}
