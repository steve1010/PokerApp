package entities;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.List;

import entities.gameplay.PlayerHand;
import entities.lobby.IDGame;

public class SafePlayer implements Serializable {

	private static final long serialVersionUID = 6942923641868073010L;

	private final String name;
	private final int id;
	private final InetSocketAddress adress, asyncAdress;
	private boolean loggedIn = false;
	private int stack;
	private final List<IDGame> gamesList;
	private PlayerHand hand;

	private Double bankRoll;

	public SafePlayer(int id, String name, InetSocketAddress adress, List<IDGame> gamesList) {
		this.id = id;
		this.name = name;
		this.adress = adress;
		if (adress != null) {
			int asyncPort = adress.getPort() + 1;
			this.asyncAdress = new InetSocketAddress(adress.getAddress(), asyncPort);
		} else {
			asyncAdress = null;
		}
		this.gamesList = gamesList;

		// set initial bankroll:
		this.bankRoll = new Double(10000);
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

	public List<IDGame> getGamesList() {
		return gamesList;
	}
}