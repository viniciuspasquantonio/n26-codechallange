package br.com.pasquantonio.model;

public class Statistic {

	private double sum;

	private double avg;

	private double max = Double.MIN_VALUE;

	private double min = Double.MAX_VALUE;

	private double count;

	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public double getAvg() {
		return avg;
	}

	public void setAvg(double avg) {
		this.avg = avg;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getCount() {
		return count;
	}

	public void setCount(double count) {
		this.count = count;
	}


}
