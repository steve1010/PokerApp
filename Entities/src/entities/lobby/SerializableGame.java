package entities.lobby;

import java.io.Serializable;

public class SerializableGame implements Serializable {

	private static final long serialVersionUID = -2838713606186843691L;

	private final int id, startChips, maxPlayers, paid, signedUp;
	private final String name;
	private final double buyIn;

	public SerializableGame(int id, int startChips, int maxPlayers, int paid, int signedUp, String name, double buyIn) {
		super();
		this.id = id;
		this.startChips = startChips;
		this.maxPlayers = maxPlayers;
		this.paid = paid;
		this.signedUp = signedUp;
		this.name = name;
		this.buyIn = buyIn;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getId() {
		return id;
	}

	public int getStartChips() {
		return startChips;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public int getPaid() {
		return paid;
	}

	public int getSignedUp() {
		return signedUp;
	}

	public String getName() {
		return name;
	}

	public double getBuyIn() {
		return buyIn;
	}

}
