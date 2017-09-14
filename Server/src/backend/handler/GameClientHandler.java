package backend.handler;

import java.net.InetSocketAddress;

import entities.SafePlayer;
import entities.lobby.SerializableGame;
import entities.query.Evaluation;
import entities.query.GameQuery;
import entities.query.Query;
import entities.query.server.ServerMsg;
import entities.query.server.ServerMsg.MsgType;
import logic.container.GameContainer;

public final class GameClientHandler extends ClientHandler {

	private final GameContainer gameContainer;

	public GameClientHandler(Query received, InetSocketAddress clientAdress, GameContainer game) {
		super(received, clientAdress);
		this.gameContainer = game;
	}

	@Override
	public void run() {
		switchQuery(getReceived());
	}

	@Override
	public void switchQuery(Query query) {
		if (query instanceof GameQuery) {
			GameQuery received = (GameQuery) query;
			switch (received.getOption()) {
			case GET_GAMES:
				answer(gameContainer.getGamesListSerializable());
				break;
			case NEW_CARD:
				answer(gameContainer.getDealer().newCard());
				break;
			case NEW_ROUND:
				gameContainer.getDealer().shuffle();
				break;
			case NEW_GAME:
				SerializableGame game = gameContainer.addGame(received.getGame());
				for (SafePlayer player : gameContainer.getPlayerStore().getAll()) {
					answer(new ServerMsg(MsgType.NEW_GAME_OFFERED, game.getId(), game), player.getAsyncAdress());
				}
				answer(game);
				break;
			case EVALUATION:
				Evaluation evaluation = (Evaluation) query;
				answer(gameContainer.getDealer().evaluate(evaluation.getBoard(), evaluation.getHands()));
				break;
			default:
				break;
			}
		}
	}
}