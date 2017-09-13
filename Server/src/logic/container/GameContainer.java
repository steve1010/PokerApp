package logic.container;

import java.util.ArrayList;
import java.util.List;

import entities.Player;
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

	public GameContainer(Dealer dealer, PlayerContainer store) {
		this.dealer = dealer;
		this.playerStore = store;
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
			return toSerializableGame(idGame);
		}
	}

	private SerializableGame toSerializableGame(IDGame idGame) {
		return new SerializableGame(idGame.getId(), idGame.getStartChips(), idGame.getMaxPlayers(), idGame.getPaid(),
				idGame.getSignedUp(), idGame.getName(), idGame.getBuyIn());
	}

	public List<SerializableGame> getGamesListSerializable() {
		synchronized (gamesListLock) {
			List<SerializableGame> games = new ArrayList<>(gamesList.size());
			for (IDGame game : gamesList) {
				games.add(toSerializableGame(game));
			}
			return games;
		}
	}

	public List<IDGame> getGamesList() {
		synchronized (gamesListLock) {
			return gamesList;
		}
	}

	public One23 addPlayerToGame(Player player, int gameId) {
		synchronized (gamesListLock) {
			List<Player> playersInGame = gamesList.get(gameId).getPlayersList();
			boolean inGame = false;
			for (Player plr : playersInGame) {
				if (plr.getName().equals(player.getName())) {
					inGame = true;
				}
			}
			if (inGame) {
				return new One23(3);
			}

			playerStore.getPlayerByName(player.getName()).commitTransaction(gamesList.get(gameId).getBuyIn());
			player.commitTransaction(gamesList.get(gameId).getBuyIn());
			if (gamesList.get(gameId).addPlayer(player)) {
				return new One23(1);
			} else {
				return new One23(2);
			}
		}
	}
}