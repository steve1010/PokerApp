package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import entities.query.server.One23;
import entities.query.server.One23.Bool;

/**
 * Basic but not super class of IDGame. IDGame can use this class in its
 * constructor.
 */
public class Game implements Serializable {

	private static final long serialVersionUID = 4745346719589341823L;

	private final String name;

	private final Double buyIn;
	private final Integer id, startChips, maxPlayers, paid;
	private Integer signedUp;
	private final List<SafePlayer> playersList;

	public Game(String name, Integer id, Double buyIn, Integer startChips, Integer maxPlayers, Integer paid,
			Integer signedUp) {
		this.name = name;
		this.id = id;
		this.buyIn = buyIn;
		this.startChips = startChips;
		this.maxPlayers = maxPlayers;
		this.paid = paid;
		this.signedUp = signedUp;
		this.playersList = new ArrayList<>();
	}

	public Game(String name, Integer id, Double buyIn, Integer startChips, Integer maxPlayers, Integer paid,
			Integer signedUp, List<SafePlayer> orderedPlayers) {
		this.name = name;
		this.id = id;
		this.buyIn = buyIn;
		this.startChips = startChips;
		this.maxPlayers = maxPlayers;
		this.paid = paid;
		this.signedUp = signedUp;
		this.playersList = orderedPlayers;
	}

	/**
	 * Evaluates whether a new player can join the game or not.
	 * 
	 * @param safePlayer
	 * @return a {@link One23} representation of the result.
	 * @see One23
	 */
	public One23 addPlayer(SafePlayer safePlayer) {
		if (playersList.size() == maxPlayers) {
			return new One23(2, Bool.ERROR);
		} else if (playersList.size() == maxPlayers - 1) {
			this.playersList.add(safePlayer);
			incrementSignedUp();
			return new One23(4, Bool.SUCCESS);
		} else {
			this.playersList.add(safePlayer);
			incrementSignedUp();
			return new One23(1, Bool.SUCCESS);
		}
	}

	private void incrementSignedUp() {
		this.signedUp++;
	}

	public Integer getId() {
		return id;
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

	public List<SafePlayer> getPlayersList() {
		return playersList;
	}
}
