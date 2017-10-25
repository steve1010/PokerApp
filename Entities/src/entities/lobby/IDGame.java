package entities.lobby;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import entities.SafePlayer;
import entities.query.server.One23;
import entities.query.server.One23.Bool;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Game representing the games int gui table (and in
 * observableLists(=tableData))
 */
public final class IDGame implements Serializable {

	private static final long serialVersionUID = 5533851077335308290L;
	private final int id;
	private final StringProperty name;
	private final DoubleProperty buyIn;
	private final IntegerProperty startChips, maxPlayers, paid;
	private final List<SafePlayer> playersList;

	private IntegerProperty signedUp;

	public IDGame(Game game, int id) {
		this.id = id;
		if (game != null) {
			this.name = new SimpleStringProperty(game.getName());
			this.buyIn = new SimpleDoubleProperty(game.getBuyIn());
			this.startChips = new SimpleIntegerProperty(game.getStartChips());
			this.maxPlayers = new SimpleIntegerProperty(game.getMaxPlayers());
			this.paid = new SimpleIntegerProperty(game.getPaid());
			this.signedUp = new SimpleIntegerProperty(game.getSignedUp());
		} else {
			this.name = null;
			this.buyIn = null;
			this.startChips = null;
			this.maxPlayers = null;
			this.paid = null;
			this.signedUp = null;
		}
		this.playersList = new ArrayList<>();
	}

	public IDGame(Game game, int id, List<SafePlayer> playersList) {
		this(game, id);
		playersList.forEach(e -> this.playersList.add(e));
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name.get();
	}

	public Double getBuyIn() {
		return buyIn.get();
	}

	public Integer getStartChips() {
		return startChips.get();
	}

	public Integer getMaxPlayers() {
		return maxPlayers.get();
	}

	public Integer getPaid() {
		return paid.get();
	}

	/**
	 * Evaluates whether a new player can join the game or not.
	 * 
	 * @param safePlayer
	 * @return a {@link One23} representation of the result.
	 * @see One23
	 */
	public One23 addPlayer(SafePlayer safePlayer) {
		if (playersList.size() == maxPlayers.get()) {
			return new One23(2, Bool.ERROR);
		} else if (playersList.size() == maxPlayers.get() - 1) {
			this.playersList.add(safePlayer);
			incrementSignedUp();
			return new One23(4, Bool.SUCCESS);
		} else {
			this.playersList.add(safePlayer);
			incrementSignedUp();
			return new One23(1, Bool.SUCCESS);
		}
	}

	public Integer getSignedUp() {
		return signedUp.get();
	}

	public void incrementSignedUp() {
		signedUp = new SimpleIntegerProperty((signedUp.get() + 1));
	}

	public StringProperty nameProperty() {
		return name;
	}

	public DoubleProperty buyInProperty() {
		return buyIn;
	}

	public IntegerProperty startChipsProperty() {
		return startChips;
	}

	public IntegerProperty maxPlayersProperty() {
		return maxPlayers;
	}

	public IntegerProperty paidProperty() {
		return paid;
	}

	public IntegerProperty signedUpProperty() {
		return signedUp;
	}

	public List<SafePlayer> getPlayersList() {
		return playersList;
	}

	public static SerializableGame toSerializableGame(IDGame idGame) {
		return new SerializableGame(idGame.getId(), idGame.getStartChips(), idGame.getMaxPlayers(), idGame.getPaid(),
				idGame.getSignedUp(), idGame.getName(), idGame.getBuyIn(), idGame.getPlayersList());
	}

	public static IDGame fromSerializableGame(SerializableGame serializableGame) {
		return new IDGame(
				new Game(serializableGame.getName(), serializableGame.getBuyIn(), serializableGame.getStartChips(),
						serializableGame.getMaxPlayers(), serializableGame.getPaid(), serializableGame.getSignedUp()),
				serializableGame.getId(), serializableGame.getPlayersList());
	}

	public static List<IDGame> toIDGames(List<SerializableGame> receivedGames) {
		List<IDGame> idgames = new ArrayList<>();
		for (SerializableGame game : receivedGames) {
			idgames.add(fromSerializableGame(game));
		}
		return idgames;
	}

	public static List<SerializableGame> fromIDGames(List<IDGame> receivedGames) {
		List<SerializableGame> idgames = new ArrayList<>();
		for (IDGame game : receivedGames) {
			idgames.add(toSerializableGame(game));
		}
		return idgames;
	}
}