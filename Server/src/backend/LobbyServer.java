package backend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import backend.handler.UdpClientHandler;
import entities.query.server.PoisonPill;
import logic.container.GameContainer;
import logic.container.PlayerContainer;
import logic.gameplay.Dealer;

public class LobbyServer implements Runnable {

	private boolean running = true;

	private final int port;
	private final GameContainer game;

	public LobbyServer(int port) {
		this.port = port;
		this.game = new GameContainer(new Dealer(), new PlayerContainer());
	}

	@Override
	public void run() {
		try (DatagramSocket socket = new DatagramSocket(port)) {
			byte[] incomingData = new byte[2048];
			while (this.running) {
				DatagramPacket incPacket = new DatagramPacket(incomingData, incomingData.length);
				socket.receive(incPacket);
				new Thread(new UdpClientHandler(incPacket.getData(),
						new InetSocketAddress(incPacket.getAddress(), incPacket.getPort()), game)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		this.running = false;
		poisonPill();
	}

	private void poisonPill() {
		try (DatagramSocket socket = new DatagramSocket(port + 1);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(bos)) {
			os.writeObject(new PoisonPill());
			byte[] toSendData = bos.toByteArray();
			DatagramPacket toSendPacket = new DatagramPacket(toSendData, toSendData.length,
					new InetSocketAddress("localhost", port));
			socket.send(toSendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
