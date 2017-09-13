package entities.lobby;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import entities.Player;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class IDGame implements Serializable {

	private static final long serialVersionUID = 5533851077335308290L;
	private final int id;
	private final StringProperty name;
	private final DoubleProperty buyIn;
	private final IntegerProperty startChips, maxPlayers, paid;
	private final List<Player> playersList = new ArrayList<>();

	private IntegerProperty signedUp;

	public IDGame(Game game, int id) {
		this.id = id;
		this.name = new SimpleStringProperty(game.getName());
		this.buyIn = new SimpleDoubleProperty(game.getBuyIn());
		this.startChips = new SimpleIntegerProperty(game.getStartChips());
		this.maxPlayers = new SimpleIntegerProperty(game.getMaxPlayers());
		this.paid = new SimpleIntegerProperty(game.getPaid());
		this.signedUp = new SimpleIntegerProperty(game.getSignedUp());
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

	public boolean addPlayer(Player player) {
		if (playersList.size() == maxPlayers.get()) {
			return false;
		} else {
			this.playersList.add(player);
			int newVal = signedUp.get() + 1;
			this.signedUp = new SimpleIntegerProperty(newVal);
			return true;
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

	public List<Player> getPlayersList() {
		return playersList;
	}
}