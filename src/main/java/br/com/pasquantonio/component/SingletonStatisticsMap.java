package br.com.pasquantonio.component;

import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.springframework.stereotype.Component;

import br.com.pasquantonio.model.Statistic;

@Component
public class SingletonStatisticsMap {

	static ConcurrentNavigableMap<Long, Statistic> statisticsMap = null;
	
	public static ConcurrentNavigableMap<Long, Statistic> getInstance(){
		if(statisticsMap == null){
			statisticsMap = new ConcurrentSkipListMap();
		}
		return statisticsMap;
	}

}
