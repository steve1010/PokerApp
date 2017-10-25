package logic.container;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import backend.GameServer;
import backend.RemoteAccess;
import entities.SafePlayer;
import entities.lobby.Game;
import entities.lobby.IDGame;
import entities.lobby.SerializableGame;
import entities.query.server.One23;
import entities.query.server.One23.Bool;

public final class GameContainer {

	private final PlayerContainer playerStore;
	private final List<IDGame> gamesList;
	private final Object gamesListLock = new Object();
	private final ArrayList<RemoteAccess> remoteAccesses;

	public GameContainer(PlayerContainer players, ArrayList<RemoteAccess> remoteAccesses) {
		this.playerStore = players;
		this.gamesList = new ArrayList<>();
		this.remoteAccesses = remoteAccesses;
	}

	public PlayerContainer getPlayerStore() {
		return playerStore;
	}

	public SerializableGame addGame(Game game) {
		synchronized (gamesListLock) {
			IDGame idGame = new IDGame(game, gamesList.size());
			gamesList.add(idGame);
			return IDGame.toSerializableGame(idGame);
		}
	}

	public List<SerializableGame> getGamesListSerializable() {
		synchronized (gamesListLock) {
			List<SerializableGame> games = new ArrayList<>(gamesList.size());
			for (IDGame game : gamesList) {
				games.add(IDGame.toSerializableGame(game));
			}
			return games;
		}
	}

	/**
	 * Evaluates whether a new player can be added to a given game.
	 * 
	 * @param player
	 * @param game
	 * @return A {@link One23} representing the result.
	 */
	public One23 addPlayerToGame(SafePlayer player, IDGame game) {
		synchronized (gamesListLock) {
			/**
			 * If there is a player in the game's playersList named exactly
			 * equal to the given player, return a 3 (already ingame)
			 */
			if (!(gamesList.get(game.getId()).getPlayersList().stream()
					.filter(plr -> plr.getName().equals(player.getName())).collect(Collectors.toList()).isEmpty())) {
				return new One23(3, Bool.ERROR);
			}
			One23 result = gamesList.get(game.getId()).addPlayer(player);
			if (result.getI() == 4) {
				game.addPlayer(player);
				GameServer gameServer = new GameServer(game);
				new Thread(gameServer).start();
				remoteAccesses.add(gameServer);
			}
			if (result.getB().equals(Bool.SUCCESS)) {
				playerStore.commitTransaction(player, game, gamesList.get(game.getId()).getBuyIn());
			}
			return result;
		}
	}

	public ArrayList<RemoteAccess> getRemoteAccesses() {
		return remoteAccesses;
	}
}