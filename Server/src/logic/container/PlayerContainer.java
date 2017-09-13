package logic.container;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import entities.Player;

/**
 * ThreadSafeTicketStore
 */
public final class PlayerContainer {

	private final List<Player> playersList = new ArrayList<>();
	private AtomicInteger currentId = new AtomicInteger(-1);
	private final HashMap<String, Player> playersMap = new HashMap<>();
	private final HashMap<Integer, Player> playersIdMap = new HashMap<>();

	private final Object playersListLock = new Object();
	private final Object playersMapLock = new Object();

	public int getNewId() {
		return currentId.incrementAndGet();
	}

	public List<Player> getAll() {
		synchronized (playersListLock) {
			return this.playersList;
		}
	}

	public Player addPlayer(String playerName, InetSocketAddress adress) {
		synchronized (playersListLock) {
			Player newPlayer = new Player(getNewId(), playerName, adress);
			newPlayer.setLoggedIn(true);
			this.playersList.add(newPlayer);
			this.playersMap.put(playerName, newPlayer);
			this.playersIdMap.put(newPlayer.getId(), newPlayer);
			return newPlayer;
		}
	}

	public void setLoggedIn(String username) {
		synchronized (playersMapLock) {
			playersMap.get(username).setLoggedIn(true);
		}
	}

	public void setLoggedOut(String username) {
		synchronized (playersMapLock) {
			playersMap.get(username).setLoggedIn(false);
		}
	}

	public Player getPlayerByName(String playerName) {

		for (Player player : playersList) {
			if (player.getName().equals(playerName)) {
				return player;
			}
		}
		return new Player(-1, null, null);
	}
}
