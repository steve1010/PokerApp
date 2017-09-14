package entities.lobby;

import java.io.Serializable;

/**
 * Basic but not super class of IDGame. IDGame can use this class in its
 * constructor.
 */
public class Game implements Serializable {

	private static final long serialVersionUID = 4745346719589341823L;

	private final String name;
	private final Double buyIn;
	private final Integer startChips, maxPlayers, paid, signedUp;

	public Game(String name, Double buyIn, Integer startChips, Integer maxPlayers, Integer paid, Integer signedUp) {
		this.name = name;
		this.buyIn = buyIn;
		this.startChips = startChips;
		this.maxPlayers = maxPlayers;
		this.paid = paid;
		this.signedUp = signedUp;
	}

	public String getName() {
		return name;
	}

	public Double getBuyIn() {
		return buyIn;
	}

	public Integer getStartChips() {
		return startChips;
	}

	public Integer getMaxPlayers() {
		return maxPlayers;
	}

	public Integer getPaid() {
		return paid;
	}

	public Integer getSignedUp() {
		return signedUp;
	}
}
