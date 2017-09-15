package logic.container;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import entities.SafePlayer;
import entities.lobby.Game;
import entities.lobby.IDGame;
import entities.lobby.SerializableGame;
import entities.query.server.One23;
import logic.gameplay.Dealer;

public class GameContainer {

	private final Dealer dealer;
	private final PlayerContainer playerStore;
	private final List<IDGame> gamesList;
	private Object gamesListLock = new Object();

	public GameContainer(Dealer dealer, PlayerContainer players) {
		this.dealer = dealer;
		this.playerStore = players;
		this.gamesList = new ArrayList<>();
	}

	public Dealer getDealer() {
		return dealer;
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
			return result;
		}
	}
}