package entities;

import java.io.Serializable;
import java.net.InetSocketAddress;

import entities.gameplay.PlayerHand;

public class Player implements Serializable {

	private static final long serialVersionUID = 2770927297609110070L;

	private final String name;
	private final int id;
	private final InetSocketAddress adress, asyncAdress;
	private boolean loggedIn = false;
	private int stack;
	private PlayerHand hand;

	private Double bankRoll;

	public Player(int id, String name, InetSocketAddress adress) {
		this.id = id;
		this.name = name;
		this.adress = adress;
		if (adress != null) {
			int asyncPort = adress.getPort() + 1;
			this.asyncAdress = new InetSocketAddress(adress.getAddress(), asyncPort);
		} else {
			asyncAdress = null;
		}

		// set initial bankroll:
		this.bankRoll = new Double(10000);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public InetSocketAddress getAdress() {
		return adress;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public int getStack() {
		return stack;
	}

	public void setStack(int stack) {
		this.stack = stack;
	}

	public PlayerHand getHand() {
		return hand;
	}

	public void setHand(PlayerHand hand) {
		this.hand = hand;
	}

	public Double getBankRoll() {
		return this.bankRoll;
	}

	private void setBankRoll(Double bankRoll) {
		this.bankRoll = bankRoll;
	}

	public InetSocketAddress getAsyncAdress() {
		return asyncAdress;
	}

	public void commitTransaction(Double buyIn) {
		setBankRoll(bankRoll - buyIn);
	}

}