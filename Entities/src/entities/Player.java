package entities;

import java.net.InetSocketAddress;
import java.util.List;

public class Player extends SafePlayer {

	private static final long serialVersionUID = 2770927297609110070L;
	private final String pw;

	public Player(int id, String name, String pw, InetSocketAddress adress, List<Game> gamesList) {
		super(id, name, adress, gamesList);
		this.pw = pw;
	}

	public Player(SafePlayer player, String pw) {
		super(player.getId(), player.getName(), player.getAdress(), player.getGamesList());
		this.pw = pw;
	}

	public String getPw() {
		return this.pw;
	}

	public static SafePlayer toSafePlayer(Player player) {
		if (player == null) {
			throw new IllegalArgumentException();
		}
		SafePlayer sp = new SafePlayer(player.getId(), player.getName(), player.getAdress(), player.getGamesList(),
				true);
		if (player.isLoggedIn()) {
			sp.setLoggedIn(true);
		}
		return sp;
	}

	public static Player fromSafePlayer(SafePlayer player) {
		if (player == null) {
			throw new IllegalArgumentException();
		}
		Player player2 = new Player(player.getId(), player.getName(), null, player.getAdress(), player.getGamesList());
		player2.setLoggedIn(true);
		return player2;

	}

}