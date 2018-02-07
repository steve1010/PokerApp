package entities.query;

import java.net.InetSocketAddress;

public abstract class NetworkAddressBuilder{
	private InetSocketAddress srcAddress, destAddress;

	public NetworkAddressBuilder srcAddress(InetSocketAddress newSrcAddress) {
		this.srcAddress = newSrcAddress;
		return this;
	}

	public NetworkAddressBuilder destAddress(InetSocketAddress newDestAddress) {
		this.destAddress = newDestAddress;
		return this;
	}

	public InetSocketAddress getSrcAddress() {
		return srcAddress;
	}

	public InetSocketAddress getDestAddress() {
		return destAddress;
	}

	public abstract Query build();
}
