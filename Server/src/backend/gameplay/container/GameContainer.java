package backend.gameplay.container;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import backend.GameServer;
import backend.RemoteAccess;
import entities.game.Game;
import entities.game.SafePlayer;
import entities.query.server.ServerResult;
import entities.query.server.ServerResult.Type;

public final class GameContainer {

	private final PlayerContainer playerStore;
	private final List<Game> gamesList;
	private final Object gamesListLock = new Object();
	private final ArrayList<RemoteAccess> remoteAccesses;
	private final int lobbyServerPort;
	private int newGameServerPort = 100;

	public GameContainer(PlayerContainer players, ArrayList<RemoteAccess> remoteAccesses, int port) {
		this.playerStore = players;
		this.gamesList = new ArrayList<>();
		this.remoteAccesses = remoteAccesses;
		this.lobbyServerPort = port;
		this.newGameServerPort += lobbyServerPort;
	}

	public PlayerContainer getPlayerStore() {
		return playerStore;
	}

	public Game addGame(Game game) {
		synchronized (gamesListLock) {
			gamesList.add(game);
			return game;
		}
	}

	public List<Game> getGamesList() {
		synchronized (gamesListLock) {
			return gamesList;
		}
	}

	/**
	 * Evaluates whether a new player can be added to a given game. <br>
	 * GameServer(Threads) are started here. GameServer-Port-Pattern:
	 * 
	 * @param player
	 * @param game
	 * @return A {@link ServerResult} representing the result.
	 */
	public ServerResult addPlayerToGame(SafePlayer player, Game game) {
		synchronized (gamesListLock) {
			/**
			 * If there is a player in the game's playersList named exactly equal to the
			 * given player, return a 3 (already ingame)
			 */
			if (!(gamesList.get(game.getId()).getPlayersList().stream()
					.filter(plr -> plr.getName().equals(player.getName())).collect(Collectors.toList()).isEmpty())) {
				return new ServerResult.ServerResultBuilder(null, Type.ERROR).derivation(3).build();
			}
			ServerResult result = gamesList.get(game.getId()).addPlayer(player);
			if (result.getType().equals(Type.SUCCESS)) {
				if (result.getDerivation() == 4) {
					game.addPlayer(player);
					GameServer gameServer = new GameServer(game, (newGameServerPort()));
					new Thread(gameServer).start();
					remoteAccesses.add(gameServer);
				}
				playerStore.commitTransaction(player, game, gamesList.get(game.getId()).getBuyIn());
			}
			return result;
		}
	}

	private int newGameServerPort() {
		newGameServerPort++;
		return newGameServerPort;
	}

	public ArrayList<RemoteAccess> getRemoteAccesses() {
		return remoteAccesses;
	}

	public int getLobbyServerPort() {
		return lobbyServerPort;
	}
}