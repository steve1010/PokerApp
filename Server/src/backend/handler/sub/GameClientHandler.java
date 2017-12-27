package backend.handler.sub;

import java.net.InetSocketAddress;

import backend.GameServer;
import backend.gameplay.container.GameContainer;
import backend.handler.TcpClientHandler;
import entities.game.Game;
import entities.game.SafePlayer;
import entities.query.Query;
import entities.query.game.EvaluationQuery;
import entities.query.game.GameQuery;
import entities.query.server.ServerMsg;
import entities.query.server.ServerMsg.MsgType;

public final class GameClientHandler extends TcpClientHandler {

	private final GameContainer gameContainer;

	public GameClientHandler(Query received, InetSocketAddress clientAdress, GameContainer game) {
		super(received);
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
				answer(new ServerMsg.SMBuilder(MsgType.GAME).srcAddress(query.getSrcAddress()).build());
				// answer(new ServerMsg(query.getSrcAddress(), MsgType.GAMES_LIST,
				// gameContainer.getGamesList()));
				break;
			case NEW_CARD:
				// answer(new ServerMsg(query.getSrcAddress(),
				// ((GameServer)
				// gameContainer.getRemoteAccesses().get(1)).getDealer().newCard()));
				break;
			case NEW_ROUND:
				((GameServer) gameContainer.getRemoteAccesses().get(1)).getDealer().shuffle();
				break;
			case NEW_GAME:
				Game game = gameContainer.addGame(received.getGame());
				// triggerServerMsg(new ServerMsg(query.getSrcAddress(),
				// MsgType.NEW_GAME_OFFERED, game.getId(), game));
				break;
			case EVALUATION:
				EvaluationQuery evaluation = (EvaluationQuery) query;
				// answer(new ServerMsg(query.getSrcAddress(), ((GameServer)
				// gameContainer.getRemoteAccesses().get(1))
				// .getDealer().evaluate(evaluation.getBoard(), evaluation.getHands())));
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
			// answer(serverMsg, new InetSocketAddress(player.getAdress().getAddress(),
			// asyncClientPort));
		}
	}
}