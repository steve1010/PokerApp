package logic.container;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import entities.Player;
import entities.SafePlayer;

/**
 * ThreadSafeTicketStore
 */
public final class PlayerContainer {

	private final List<SafePlayer> playersList = new ArrayList<>();
	private AtomicInteger currentId = new AtomicInteger(-1);
	private final HashMap<String, Player> playersMap = new HashMap<>();
	private final HashMap<Integer, Player> playersIdMap = new HashMap<>();

	private final Object playersLock = new Object();
	private Object idLock = new Object();

	public List<SafePlayer> getAll() {
		synchronized (playersLock) {
			return this.playersList.stream().filter(e -> e.isLoggedIn()).collect(Collectors.toList());
		}
	}

	public SafePlayer addPlayer(String playerName, String pw, InetSocketAddress adress) {
		synchronized (playersLock) {
			Player newPlayer = new Player(getNewId(), playerName, pw, adress, new ArrayList<>());
			newPlayer.setLoggedIn(true);
			this.playersList.add(newPlayer);
			this.playersMap.put(playerName, newPlayer);
			this.playersIdMap.put(newPlayer.getId(), newPlayer);
			return Player.toSafePlayer(newPlayer);
		}
	}

	public void setLoggedIn(String username) {
		synchronized (playersLock) {
			int id = playersMap.get(username).getId();
			playersMap.get(username).setLoggedIn(true);
			playersIdMap.get(new Integer(id)).setLoggedIn(true);
			playersList.get(id).setLoggedIn(true);
		}
	}

	public void setLoggedOut(String username) {
		synchronized (playersLock) {
			int id = playersMap.get(username).getId();
			playersMap.get(username).setLoggedIn(false);
			playersIdMap.get(new Integer(id)).setLoggedIn(false);
			playersList.get(id).setLoggedIn(false);
		}
	}

	public SafePlayer getPlayerByName(String playerName) {
		synchronized (playersLock) {
			return Player.toSafePlayer(playersMap.get(playerName));
		}
	}

	private int getNewId() {
		synchronized (idLock) {
			return currentId.incrementAndGet();
		}
	}

	public void commitTransaction(String name, Double buyIn) {
		synchronized (playersLock) {
			int id = getPlayerByName(name).getId();
			playersMap.get(name).commitTransaction(buyIn);
			playersIdMap.get(id).commitTransaction(buyIn);
			playersList.get(id).commitTransaction(buyIn);
		}
	}
}