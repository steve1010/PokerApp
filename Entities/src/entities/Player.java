package entities;

import java.net.InetSocketAddress;
import java.util.List;

import entities.lobby.IDGame;

public class Player extends SafePlayer {

	private static final long serialVersionUID = 2770927297609110070L;
	private final String pw;

	public Player(int id, String name, String pw, InetSocketAddress adress, List<IDGame> gamesList) {
		super(id, name, adress, gamesList);
		this.pw = pw;
	}

	public String getPw() {
		return this.pw;
	}

	public static SafePlayer toSafePlayer(Player player) {

		if (player == null) {
			throw new IllegalArgumentException();
		}

		SafePlayer sp = new SafePlayer(player.getId(), player.getName(), player.getAdress(), player.getGamesList());
		if (player.isLoggedIn()) {
			sp.setLoggedIn(true);
		}
		return sp;
	}
}