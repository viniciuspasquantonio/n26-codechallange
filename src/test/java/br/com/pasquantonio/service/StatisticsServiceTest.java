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
public class StatisticsServiceTest {

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
		
		singletonStatisticsMap.getInstance().clear();
	}
	
	
	
	@Test
	public void shouldAccountTransactionToAccountedStatistics(){
		Transaction transaction = new Transaction(Instant.now().toEpochMilli(),50D);
		
		Statistic accountedStatistic = new Statistic();
		accountedStatistic.setAvg(70);
		accountedStatistic.setMax(50);
		accountedStatistic.setMin(10);
		accountedStatistic.setSum(250);
		accountedStatistic.setCount(5);
		singletonStatisticsMap.getInstance().put(transaction.getTime(), accountedStatistic);
		
		
		statisticsService.postTransaction(transaction,singletonStatisticsMap.getInstance());
		
		assertTrue(singletonStatisticsMap.getInstance().containsKey(transaction.getTime()));
		Statistic statistic = singletonStatisticsMap.getInstance().get(transaction.getTime());
		assertEquals(statistic.getAvg(), 50,0);
		assertEquals(statistic.getMax(), 50,0);
		assertEquals(statistic.getMin(), 10,0);
		assertEquals(statistic.getSum(), 300,0);
		assertEquals(statistic.getCount(), 6,0);
		
		singletonStatisticsMap.getInstance().clear();
	}
	
	@Test
	public void shouldAccountSeveralTransactionsWithTheSameTime(){
		long now = Instant.now().toEpochMilli();
		Transaction transaction = new Transaction(now,50D);
		Transaction secondTransaction = new Transaction(now,100D);
		Transaction thirdTransaction = new Transaction(now,10D);
		Transaction fourthTransaction = new Transaction(now,30D);
		Transaction fifthTransaction = new Transaction(now,90D);
		
		statisticsService.postTransaction(transaction,singletonStatisticsMap.getInstance());
		statisticsService.postTransaction(secondTransaction,singletonStatisticsMap.getInstance());
		statisticsService.postTransaction(thirdTransaction,singletonStatisticsMap.getInstance());
		statisticsService.postTransaction(fourthTransaction,singletonStatisticsMap.getInstance());
		statisticsService.postTransaction(fifthTransaction,singletonStatisticsMap.getInstance());
		
		Statistic statistic = singletonStatisticsMap.getInstance().get(now);
		assertEquals(statistic.getAvg(), 56,0);
		assertEquals(statistic.getMax(), 100,0);
		assertEquals(statistic.getMin(), 10,0);
		assertEquals(statistic.getSum(), 280,0);
		assertEquals(statistic.getCount(), 5,0);
		
		singletonStatisticsMap.getInstance().clear();
	}
	
	@Test
	public void shouldNotAccountTransactionsWithDifferentTime(){
		long now = Instant.now().toEpochMilli();
		long tenSecondsAgo = now - 100000;
		Transaction transaction = new Transaction(now,50D);
		Transaction secondTransaction = new Transaction(now,100D);
		Transaction thirdTransaction = new Transaction(now,10D);
		Transaction fourthTransaction = new Transaction(tenSecondsAgo,30D);
		Transaction fifthTransaction = new Transaction(tenSecondsAgo,90D);
		
		statisticsService.postTransaction(transaction,singletonStatisticsMap.getInstance());
		statisticsService.postTransaction(secondTransaction,singletonStatisticsMap.getInstance());
		statisticsService.postTransaction(thirdTransaction,singletonStatisticsMap.getInstance());
		statisticsService.postTransaction(fourthTransaction,singletonStatisticsMap.getInstance());
		statisticsService.postTransaction(fifthTransaction,singletonStatisticsMap.getInstance());
		
		Statistic statistic = singletonStatisticsMap.getInstance().get(now);
		assertEquals(statistic.getAvg(), 53.3333,0.0001);
		assertEquals(statistic.getMax(), 100,0);
		assertEquals(statistic.getMin(), 10,0);
		assertEquals(statistic.getSum(), 160,0);
		assertEquals(statistic.getCount(), 3,0);
		
		singletonStatisticsMap.getInstance().clear();
	}
	
	@Test
	public void shoulRetriveStatisticsFromPast60Seconds(){
		long now = Instant.now().toEpochMilli();
		long tenSecondsAgo = now - 10000;
		long sixtySecondsAgo = now - (60 * 1000);
		long twoMinutesAgo = now - (120 *1000);
		Transaction transaction = new Transaction(now,50D);
		Transaction secondTransaction = new Transaction(now,100D);
		Transaction thirdTransaction = new Transaction(now,10D);
		Transaction fourthTransaction = new Transaction(tenSecondsAgo,30D);
		Transaction fifthTransaction = new Transaction(tenSecondsAgo,90D);
		Transaction twoMinutesAgoTransaction = new Transaction(twoMinutesAgo,90D);
		
		statisticsService.postTransaction(transaction,singletonStatisticsMap.getInstance());
		statisticsService.postTransaction(secondTransaction,singletonStatisticsMap.getInstance());
		statisticsService.postTransaction(thirdTransaction,singletonStatisticsMap.getInstance());
		statisticsService.postTransaction(fourthTransaction,singletonStatisticsMap.getInstance());
		statisticsService.postTransaction(fifthTransaction,singletonStatisticsMap.getInstance());
		statisticsService.postTransaction(twoMinutesAgoTransaction,singletonStatisticsMap.getInstance());
		
		Statistic pastSixtySecondsStatistic = statisticsService.retriveAllStatisticsWithTimeGreaterThan(sixtySecondsAgo, singletonStatisticsMap.getInstance());
		
		assertEquals(pastSixtySecondsStatistic.getAvg(), 56,0);
		assertEquals(pastSixtySecondsStatistic.getMax(), 100,0);
		assertEquals(pastSixtySecondsStatistic.getMin(), 10,0);
		assertEquals(pastSixtySecondsStatistic.getSum(), 280,0);
		assertEquals(pastSixtySecondsStatistic.getCount(), 5,0);
		
		singletonStatisticsMap.getInstance().clear();
	
	}
	
}
