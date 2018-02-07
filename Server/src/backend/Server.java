package backend;

import java.net.InetSocketAddress;

import entities.SenderReceiver;
import entities.query.Query;
import entities.query.server.ServerMsg;

public abstract class Server extends SenderReceiver {

	public void answer(Query msg) {
		sendMsg(msg);
	}

	public static ServerMsg serverMsg(Query received) {
		return (ServerMsg) new ServerMsg.SMBuilder().srcAddress(received.getDestAddress())
				.destAddress(received.getSrcAddress()).build();
	}

	public static ServerMsg asyncServerMsg(Query received, int destPort) {
		return (ServerMsg) new ServerMsg.SMBuilder().srcAddress(received.getDestAddress())
				.destAddress(new InetSocketAddress(received.getSrcAddress().getAddress(), destPort)).build();
	}

}
