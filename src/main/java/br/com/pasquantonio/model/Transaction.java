package br.com.pasquantonio.model;

import javax.validation.constraints.Min;

public class Transaction {
	@Min(1)
	private long time;
	
	@Min(0)
	private double amount;
	
	public Transaction() {}
	
	public Transaction(long time, double amount) {
		this.time = time;
		this.amount = amount;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
	
}
