package entities.lobby;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import entities.SafePlayer;
import entities.query.server.One23;
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
	private final List<SafePlayer> playersList = new ArrayList<>();

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

	public One23 addPlayer(SafePlayer safePlayer) {
		if (playersList.size() == maxPlayers.get()) {
			return new One23(2);
		} else if (playersList.size() == maxPlayers.get() - 1) {
			this.playersList.add(safePlayer);
			int newVal = signedUp.get() + 1;
			this.signedUp = new SimpleIntegerProperty(newVal);
			return new One23(4);
		} else {
			this.playersList.add(safePlayer);
			int newVal = signedUp.get() + 1;
			this.signedUp = new SimpleIntegerProperty(newVal);
			return new One23(1);
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
}