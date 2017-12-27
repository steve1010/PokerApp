package backend.handler;

import java.util.Objects;

import backend.gameplay.container.GameContainer;
import backend.handler.sub.GameClientHandler;
import backend.handler.sub.LoginLobbyClientHandler;
import backend.handler.sub.PlayerActionClientHandler;
import entities.PoisonPill;
import entities.query.Query;
import entities.query.game.GameQuery;
import entities.query.game.PlayerQuery;
import entities.query.game.PlayerActionQuery;
import entities.query.server.ServerMsg;

public final class ServerDispatcher extends TcpClientHandler {

	private final GameContainer gameContainer;

	public ServerDispatcher(Query query, GameContainer gameContainer) {
		super(query);
		this.gameContainer = gameContainer;
	}

	@Override
	public void run() {
		// process received data
		Query received = getReceived();
		if (received instanceof PoisonPill) {
			return;
		}
		if (received instanceof Query) {
			switchQuery((Query) received);
		} else {

			if (Objects.nonNull(received)) {
				System.err.println("Unknown message. - " + received.getClass());
			} else {
				System.err.println("Defekt message. - Msg null.");
			}
		}
	}

	@Override
	public void switchQuery(Query received) {
		if (received instanceof PlayerQuery) {
			new Thread(new LoginLobbyClientHandler(received, getClientAdress(), gameContainer)).start();
		}
		if (received instanceof GameQuery) {
			new Thread(new GameClientHandler(received, getClientAdress(), gameContainer)).start();
		}
		if (received instanceof PlayerActionQuery) {
			new Thread(new PlayerActionClientHandler((PlayerActionQuery) received)).start();
		}
	}

	@Override
	public void triggerServerMsg(ServerMsg serverMsg) {
	}
}