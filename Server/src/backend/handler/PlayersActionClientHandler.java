package backend.handler;

import java.net.InetSocketAddress;

import entities.query.Query;
import entities.query.server.ServerMsg;

public class PlayersActionClientHandler extends ClientHandler {

	public PlayersActionClientHandler(Query received, InetSocketAddress clientAdress) {
		super(received, clientAdress);
	}

	@Override
	public void run() {

	}

	@Override
	public void switchQuery(Query received) {

	}

	@Override
	public void triggerServerMsg(ServerMsg serverMsg) {
	}
}