package backend;

import java.util.ArrayList;

import backend.gameplay.container.GameContainer;
import backend.gameplay.container.PlayerContainer;
import backend.handler.ServerDispatcher;
import entities.PoisonPill;
import entities.query.Query;

/**
 * Contains and triggers on opening threads GameContainer.
 * Listens on port 50000.
 */
public class LobbyServer extends Server implements RemoteAccess {

	private final int port;
	private final GameContainer gameContainer;
	private boolean running = true;

	public LobbyServer(int port, ArrayList<RemoteAccess> remoteAccesses) {
		this.port = port;
		this.gameContainer = new GameContainer(new PlayerContainer(), remoteAccesses, port);
	}

	@Override
	public void run() {
		while (running) {
			Query query = (Query) receiveMsg(port);
			if (!(query instanceof PoisonPill)) {
				new Thread(new ServerDispatcher(query, gameContainer)).start();
			} else {
				running = false;
			}
		}
		shutdown();
	}

	public void shutdown() {
		sendMsg(new PoisonPill(createLocalhost(port), createLocalhost(port + 5)));
	}
}