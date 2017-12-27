package backend.handler;

import java.net.InetSocketAddress;

import backend.Server;
import entities.query.Query;
import entities.query.server.ServerMsg;

public abstract class TcpClientHandler extends Server implements Runnable {

	private final InetSocketAddress clientAdress;
	private Query received;
	private final int port;

	public TcpClientHandler(Query query) {
		this.received = query;
		this.clientAdress = query.getSrcAddress();
		this.port = newRandomPort();
	}

	@Override
	public abstract void run();

	public abstract void switchQuery(Query received);

	public void answer(ServerMsg serverMsg) {
		sendMsg(serverMsg);
	}

	protected InetSocketAddress getClientAdress() {
		return clientAdress;
	}

	public Query getReceived() {
		return received;
	}

	private int newRandomPort() {
		return (int) (10000 + Math.random() * 54000);
	}

	public abstract void triggerServerMsg(ServerMsg serverMsg);

	public int getPort() {
		return port;
	}
}
