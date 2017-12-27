package entities;

import java.net.InetSocketAddress;

import entities.query.Query;

public class PoisonPill extends Query {

	public PoisonPill(InetSocketAddress destinationAddress, InetSocketAddress senderAddress) {
		super(destinationAddress, senderAddress);
	}

	private static final long serialVersionUID = 3316383026226154230L;
}