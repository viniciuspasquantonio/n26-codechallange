package br.com.pasquantonio.service;

import java.time.Instant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.pasquantonio.model.Statistic;
import br.com.pasquantonio.model.Transaction;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionAccountantServiceTest {

	@Autowired
	private TransactionAccountantService transactionAccountantService;
	
	@Test
	public void statisticShouldBeEqualToAccountantedTransaction(){
		Transaction transaction = new Transaction(Instant.now().toEpochMilli(),100D);
		Statistic statistic = transactionAccountantService.account(new Statistic(),transaction);
		
		assertEquals(statistic.getAvg(), transaction.getAmount(),0);
		assertEquals(statistic.getMax(), transaction.getAmount(),0);
		assertEquals(statistic.getMin(), transaction.getAmount(),0);
		assertEquals(statistic.getSum(), transaction.getAmount(),0);
		assertEquals(statistic.getCount(), 1,0);
	}
	
	
	@Test
	public void transactonShouldBeAccountantedToExistingStatistic(){
		Transaction transaction = new Transaction(Instant.now().toEpochMilli(),100D);
		Statistic statistic = new Statistic();
		statistic.setAvg(500);
		statistic.setMax(500);
		statistic.setMin(500);
		statistic.setSum(500);
		statistic.setCount(1);
		
		Statistic accountantedStatistic = transactionAccountantService.account(statistic,transaction);
		
		assertEquals(accountantedStatistic.getAvg(), 300,0);
		assertEquals(accountantedStatistic.getMax(), 500,0);
		assertEquals(accountantedStatistic.getMin(), 100,0);
		assertEquals(accountantedStatistic.getSum(), 600,0);
		assertEquals(accountantedStatistic.getCount(), 2,0);
	}
}
