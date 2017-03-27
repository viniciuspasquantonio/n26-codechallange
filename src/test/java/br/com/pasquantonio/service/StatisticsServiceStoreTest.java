package br.com.pasquantonio.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import java.time.Instant;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.pasquantonio.component.SingletonStatisticsMap;
import br.com.pasquantonio.model.Statistic;
import br.com.pasquantonio.model.Transaction;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatisticsServiceStoreTest {

	@Autowired
	private StatisticsService statisticsService;
	
	@Autowired
	private SingletonStatisticsMap singletonStatisticsMap;
	
	@Test
	public void shouldAccountTransactionToEmptyStatistics(){
		Transaction transaction = new Transaction(Instant.now().toEpochMilli(),50D);
		statisticsService.postTransaction(transaction,singletonStatisticsMap.getInstance());
		assertTrue(singletonStatisticsMap.getInstance().containsKey(transaction.getTime()));
		Statistic statistic = singletonStatisticsMap.getInstance().get(transaction.getTime());
		assertEquals(statistic.getAvg(), transaction.getAmount(),0);
		assertEquals(statistic.getMax(), transaction.getAmount(),0);
		assertEquals(statistic.getMin(), transaction.getAmount(),0);
		assertEquals(statistic.getSum(), transaction.getAmount(),0);
		assertEquals(statistic.getCount(), 1,0);
	}
	
	
	
	@Test
	public void shouldAccountTransactionToAccountedStatistics(){
		Transaction transaction = new Transaction(Instant.now().toEpochMilli(),50D);
		
		
		Statistic accountedStatistic = new Statistic();
		accountedStatistic.setAvg(70);
		accountedStatistic.setMax(50);
		accountedStatistic.setMin(10);
		accountedStatistic.setSum(350);
		accountedStatistic.setCount(10);
		singletonStatisticsMap.getInstance().put(transaction.getTime(), accountedStatistic);
		
		
		statisticsService.postTransaction(transaction,singletonStatisticsMap.getInstance());
		
		assertTrue(singletonStatisticsMap.getInstance().containsKey(transaction.getTime()));
		Statistic statistic = singletonStatisticsMap.getInstance().get(transaction.getTime());
		assertEquals(statistic.getAvg(), 60,0);
		assertEquals(statistic.getMax(), 50,0);
		assertEquals(statistic.getMin(), 10,0);
		assertEquals(statistic.getSum(), 400,0);
		assertEquals(statistic.getCount(), 11,0);
	}
	
}
