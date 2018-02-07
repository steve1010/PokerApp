package backend.gameplay.container;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import entities.game.Game;
import entities.game.Player;
import entities.game.SafePlayer;

/**
 * <ul>
 * <li>Contains Players known to the system
 * <li>Regardless whether they are logged-in or not
 * <li>Initializes with all players in DB
 */
public final class PlayerContainer {

	private final List<SafePlayer> playersList = new ArrayList<>();
	private AtomicInteger currentId = new AtomicInteger(-1);
	private final HashMap<String, Player> playersMap = new HashMap<>();
	private final HashMap<Integer, Player> playersIdMap = new HashMap<>();

	private final Object playersLock = new Object();
	private Object idLock = new Object();

	public List<SafePlayer> getLoggedInPlayers() {
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

	/**
	 * Updates the player's current bankroll according to the buyIn he did transact.
	 * 
	 * @param safePlayer
	 * @param idGame
	 * @param buyIn
	 */
	public void commitTransaction(SafePlayer safePlayer, Game idGame, Double buyIn) {
		synchronized (playersLock) {
			playersMap.get(safePlayer.getName()).commitTransaction(idGame, buyIn);
			playersIdMap.get(safePlayer.getId()).commitTransaction(idGame, buyIn);
			playersList.get(safePlayer.getId()).commitTransaction(idGame, buyIn);
		}
	}
}