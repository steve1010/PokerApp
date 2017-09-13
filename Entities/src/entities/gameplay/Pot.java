package entities.gameplay;

import java.io.Serializable;

public final class Pot implements Serializable {

	private static final long serialVersionUID = -6824969880147530047L;
	private double amount;

	public Pot(double amount) {
		this.setAmount(amount);
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

}
