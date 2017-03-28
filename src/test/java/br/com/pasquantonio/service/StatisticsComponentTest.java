package br.com.pasquantonio.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.time.Instant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.pasquantonio.component.StatisticsComponent;
import br.com.pasquantonio.model.Statistic;
import br.com.pasquantonio.model.Transaction;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatisticsComponentTest {

	@Autowired
	private StatisticsComponent statisticsComponent;
	
	@Autowired
	private TimeIntervalService timeIntervalService;
	
	@Test
	public void shouldAccountTransactionToEmptyStatistics(){
		Transaction transaction = new Transaction(Instant.now().toEpochMilli(),50D);
		statisticsComponent.postTransaction(transaction);
		assertTrue(statisticsComponent.containsKey(transaction.getTime()));
		Statistic statistic = statisticsComponent.get(transaction.getTime());
		assertEquals(statistic.getAvg(), transaction.getAmount(),0);
		assertEquals(statistic.getMax(), transaction.getAmount(),0);
		assertEquals(statistic.getMin(), transaction.getAmount(),0);
		assertEquals(statistic.getSum(), transaction.getAmount(),0);
		assertEquals(statistic.getCount(), 1,0);
		
		statisticsComponent.clear();
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
		statisticsComponent.put(transaction.getTime(), accountedStatistic);
		StatisticsComponent statisticsComponentFake = mock(StatisticsComponent.class);
		
		statisticsComponent.postTransaction(transaction);
		
		assertTrue(statisticsComponent.containsKey(transaction.getTime()));
		Statistic statistic = statisticsComponent.get(transaction.getTime());
		assertEquals(statistic.getAvg(), 50,0);
		assertEquals(statistic.getMax(), 50,0);
		assertEquals(statistic.getMin(), 10,0);
		assertEquals(statistic.getSum(), 300,0);
		assertEquals(statistic.getCount(), 6,0);
		
		statisticsComponent.clear();
	}
	
	@Test
	public void shouldAccountSeveralTransactionsWithTheSameTime(){
		long now = Instant.now().toEpochMilli();
		Transaction transaction = new Transaction(now,50D);
		Transaction secondTransaction = new Transaction(now,100D);
		Transaction thirdTransaction = new Transaction(now,10D);
		Transaction fourthTransaction = new Transaction(now,30D);
		Transaction fifthTransaction = new Transaction(now,90D);
		
		statisticsComponent.postTransaction(transaction);
		statisticsComponent.postTransaction(secondTransaction);
		statisticsComponent.postTransaction(thirdTransaction);
		statisticsComponent.postTransaction(fourthTransaction);
		statisticsComponent.postTransaction(fifthTransaction);
		
		Statistic statistic = statisticsComponent.get(now);
		assertEquals(statistic.getAvg(), 56,0);
		assertEquals(statistic.getMax(), 100,0);
		assertEquals(statistic.getMin(), 10,0);
		assertEquals(statistic.getSum(), 280,0);
		assertEquals(statistic.getCount(), 5,0);
		
		statisticsComponent.clear();
	}
	
	@Test
	public void shouldNotAccountTransactionsWithDifferentTime(){
		long now = Instant.now().toEpochMilli();
		long tenSecondsAgo = Instant.now().minusSeconds(10L).toEpochMilli();
		Transaction transaction = new Transaction(now,50D);
		Transaction secondTransaction = new Transaction(now,100D);
		Transaction thirdTransaction = new Transaction(now,10D);
		Transaction fourthTransaction = new Transaction(tenSecondsAgo,30D);
		Transaction fifthTransaction = new Transaction(tenSecondsAgo,90D);
		
		statisticsComponent.postTransaction(transaction);
		statisticsComponent.postTransaction(secondTransaction);
		statisticsComponent.postTransaction(thirdTransaction);
		statisticsComponent.postTransaction(fourthTransaction);
		statisticsComponent.postTransaction(fifthTransaction);
		
		Statistic statistic = statisticsComponent.get(now);
		assertEquals(statistic.getAvg(), 53.3333,0.0001);
		assertEquals(statistic.getMax(), 100,0);
		assertEquals(statistic.getMin(), 10,0);
		assertEquals(statistic.getSum(), 160,0);
		assertEquals(statistic.getCount(), 3,0);
		
		statisticsComponent.clear();
	}
	
	@Test
	public void shoulRetriveStatisticsFromPast60Seconds(){
		long now = Instant.now().toEpochMilli();
		long tenSecondsAgo = Instant.now().minusSeconds(10L).toEpochMilli();
		long twoMinutesAgo = Instant.now().minusSeconds(120L).toEpochMilli();
		Transaction transaction = new Transaction(now,50D);
		Transaction secondTransaction = new Transaction(now,100D);
		Transaction thirdTransaction = new Transaction(now,10D);
		Transaction fourthTransaction = new Transaction(tenSecondsAgo,30D);
		Transaction fifthTransaction = new Transaction(tenSecondsAgo,90D);
		Transaction twoMinutesAgoTransaction = new Transaction(twoMinutesAgo,90D);
		
		statisticsComponent.postTransaction(transaction);
		statisticsComponent.postTransaction(secondTransaction);
		statisticsComponent.postTransaction(thirdTransaction);
		statisticsComponent.postTransaction(fourthTransaction);
		statisticsComponent.postTransaction(fifthTransaction);
		statisticsComponent.postTransaction(twoMinutesAgoTransaction);
		Statistic pastSixtySecondsStatistic = statisticsComponent.retriveAllStatisticsWithTimeGreaterThan();
		
		assertEquals(pastSixtySecondsStatistic.getAvg(), 56,0);
		assertEquals(pastSixtySecondsStatistic.getMax(), 100,0);
		assertEquals(pastSixtySecondsStatistic.getMin(), 10,0);
		assertEquals(pastSixtySecondsStatistic.getSum(), 280,0);
		assertEquals(pastSixtySecondsStatistic.getCount(), 5,0);
		
		statisticsComponent.clear();
	
	}
	
	@Test
	public void shouldRemoveObsoleteStatisticsAfterPost(){
		long twoMinutesAgo = Instant.now().minusSeconds(120L).toEpochMilli();
		Transaction twoMinutesAgoTransaction = new Transaction(twoMinutesAgo,90D);
		
		statisticsComponent.postTransaction(twoMinutesAgoTransaction);
		
		assertFalse(statisticsComponent.containsKey(twoMinutesAgo));
		
		statisticsComponent.clear();
	}


}
