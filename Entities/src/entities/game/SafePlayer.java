package entities.game;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.List;

import entities.game.play.PlayerHand;

/**
 * A {@link Player} with no pw.<br>
 * Used to serialize players in a secure way.
 */
public class SafePlayer implements Serializable {

	private static final long serialVersionUID = 6942923641868073010L;

	private final String name;
	private final int id;
	private final InetSocketAddress adress, asyncAdress;
	private boolean loggedIn = false;
	private int stack;
	private final List<Game> gamesList;
	private PlayerHand hand;

	private double bankRoll;

	public SafePlayer(int id, String name, InetSocketAddress adress, List<Game> gamesList) {
		this.id = id;
		this.name = name;
		this.adress = adress;
		if (adress != null) {
			int asyncPort = adress.getPort() + 1;
			this.asyncAdress = new InetSocketAddress(adress.getAddress(), asyncPort);
		} else {
			asyncAdress = null;
		}

		if (gamesList != null) {
			this.gamesList = gamesList;
		} else {
			this.gamesList = null;
		}
		// set initial bankroll:
		this.bankRoll = 10000;
	}

	public SafePlayer(int id, String name, InetSocketAddress adress, List<Game> gamesList, boolean ignore) {
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

		// TODO: @MinorConstraint set initial bankroll dynamically
		this.bankRoll = 10000;
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

	public double getBankRoll() {
		return this.bankRoll;
	}

	private void setBankRoll(double bankRoll) {
		this.bankRoll = bankRoll;
	}

	public InetSocketAddress getAsyncAdress() {
		return asyncAdress;
	}

	public void commitTransaction(Game idGame, Double buyIn) {
		setBankRoll(bankRoll - buyIn);
		gamesList.add(idGame);
	}

	public List<Game> getGamesList() {
		return gamesList;
	}
}