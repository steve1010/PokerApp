package entities.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import entities.query.server.ServerResult;
import entities.query.server.ServerResult.Type;

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
	 * @return a {@link ServerResult} representation of the result.
	 * @see ServerResult
	 */
	public ServerResult addPlayer(SafePlayer safePlayer) {
		if (playersList.size() == maxPlayers) {
			return new ServerResult.ServerResultBuilder(null, Type.ERROR).derivation(2).build();
		} else if (playersList.size() == maxPlayers - 1) {
			this.playersList.add(safePlayer);
			incrementSignedUp();
			return new ServerResult.ServerResultBuilder(null, Type.SUCCESS).derivation(4).build();
		} else {
			this.playersList.add(safePlayer);
			incrementSignedUp();
			return new ServerResult.ServerResultBuilder(null, Type.SUCCESS).derivation(1).build();
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
