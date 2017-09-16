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

	public List<IDGame> getGamesList() {
		synchronized (gamesListLock) {
			return gamesList;
		}
	}

	public One23 addPlayerToGame(SafePlayer safePlayer, IDGame idGame) {
		synchronized (gamesListLock) {
			if (!(gamesList.get(idGame.getId()).getPlayersList().stream()
					.filter(plr -> plr.getName().equals(safePlayer.getName())).collect(Collectors.toList())
					.isEmpty())) {
				return new One23(3);
			}
			playerStore.commitTransaction(safePlayer, idGame, gamesList.get(idGame.getId()).getBuyIn());
			One23 result = gamesList.get(idGame.getId()).addPlayer(safePlayer);
			if (result.getI() == 4) {
				idGame.addPlayer(safePlayer);
				new Thread(() -> {
					GameServer gameServer = new GameServer(idGame);
					new Thread(gameServer).start();
					remoteAccesses.add(gameServer);
				}).start();
			}
			return result;
		}
	}

	public ArrayList<RemoteAccess> getRemoteAccesses() {
		return remoteAccesses;
	}
}