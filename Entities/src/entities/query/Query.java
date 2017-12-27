package entities.query;

import java.io.Serializable;
import java.net.InetSocketAddress;

public abstract class Query implements Serializable {

	public enum Type {
		GAME, PACTION, PLAYER;
	}

	private final InetSocketAddress destAddress, srcAddress;

	protected Query(InetSocketAddress srcAddress, InetSocketAddress destAddress) {
		this.destAddress = destAddress;
		this.srcAddress = srcAddress;
	}

	private static final long serialVersionUID = 8458981665609609705L;

	public InetSocketAddress getDestAddress() {
		return destAddress;
	}

	public InetSocketAddress getSrcAddress() {
		return srcAddress;
	}

	public int getDestPort() {
		return destAddress.getPort();
	}

	public int getSrcPort() {
		return srcAddress.getPort();
	}
}