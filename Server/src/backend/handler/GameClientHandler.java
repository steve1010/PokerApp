package backend.handler;

import java.net.InetSocketAddress;

import backend.GameServer;
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
				answer(((GameServer) gameContainer.getRemoteAccesses().get(1)).getDealer().newCard());
				break;
			case NEW_ROUND:
				((GameServer) gameContainer.getRemoteAccesses().get(1)).getDealer().shuffle();
				break;
			case NEW_GAME:
				SerializableGame game = gameContainer.addGame(received.getGame());
				triggerServerMsg(new ServerMsg(MsgType.NEW_GAME_OFFERED, game.getId(), game));
				break;
			case EVALUATION:
				Evaluation evaluation = (Evaluation) query;
				answer(((GameServer) gameContainer.getRemoteAccesses().get(1)).getDealer()
						.evaluate(evaluation.getBoard(), evaluation.getHands()));
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void triggerServerMsg(ServerMsg serverMsg) {
		for (SafePlayer player : gameContainer.getPlayerStore().getAll()) {
			int asyncClientPort = 1 + player.getAdress().getPort();
			answer(serverMsg, new InetSocketAddress(player.getAdress().getAddress(), asyncClientPort));
		}
	}
}