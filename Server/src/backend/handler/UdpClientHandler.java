package backend.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;

import entities.query.GameQuery;
import entities.query.PlayersActionQuery;
import entities.query.PlayersQuery;
import entities.query.Query;
import entities.query.server.PoisonPill;
import entities.query.server.ServerMsg;
import logic.container.GameContainer;

public final class UdpClientHandler extends ClientHandler {

	private final GameContainer gameContainer;

	public UdpClientHandler(byte[] data, InetSocketAddress clientAdress, GameContainer game) {
		super(data, clientAdress);
		this.gameContainer = game;
	}

	@Override
	public void run() {
		// process received data
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(getData()))) {
			Object received = ois.readObject();
			if (received instanceof Query) {
				switchQuery((Query) received);
			} else {
				if (received instanceof PoisonPill) {
					return;
				}
				System.err.println("Unknown message.");
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void switchQuery(Query received) {
		if (received instanceof PlayersQuery) {
			new Thread(new PlayersClientHandler(received, getClientAdress(), gameContainer)).start();
		}
		if (received instanceof GameQuery) {
			new Thread(new GameClientHandler(received, getClientAdress(), gameContainer)).start();
		}
		if (received instanceof PlayersActionQuery) {
//			new Thread(new PlayersActionClientHandler(received, getClientAdress())).start();
		}
	}

	@Override
	public void triggerServerMsg(ServerMsg serverMsg) {
	}
}