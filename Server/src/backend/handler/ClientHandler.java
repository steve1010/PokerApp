package backend.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import entities.query.Query;

public abstract class ClientHandler implements Runnable {

	private byte[] data;
	private final InetSocketAddress clientAdress;
	private Query received;

	public ClientHandler(byte[] data, InetSocketAddress clientAdress) {
		this.data = data;
		this.clientAdress = clientAdress;
	}

	public ClientHandler(Query received, InetSocketAddress clientAdress) {
		this.received = received;
		this.clientAdress = clientAdress;
	}

	@Override
	public abstract void run();

	public abstract void switchQuery(Query received);

	public void answer(Object o) {
		try (DatagramSocket socket = new DatagramSocket();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(bos)) {
			os.writeObject(o);
			byte[] data = bos.toByteArray();
			DatagramPacket toSendPacket = new DatagramPacket(data, data.length, this.getClientAdress());
			socket.send(toSendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void answer(Object o, InetSocketAddress adress) {
		try (DatagramSocket socket = new DatagramSocket();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(bos)) {
			os.writeObject(o);
			byte[] data = bos.toByteArray();
			DatagramPacket toSendPacket = new DatagramPacket(data, data.length, adress);
			socket.send(toSendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected byte[] getData() {
		return data;
	}

	protected InetSocketAddress getClientAdress() {
		return clientAdress;
	}

	public Query getReceived() {
		return received;
	}
}
