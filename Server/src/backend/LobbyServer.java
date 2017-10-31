package backend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import backend.gameplay.container.GameContainer;
import backend.gameplay.container.PlayerContainer;
import backend.handler.UdpClientHandler;
import entities.query.server.PoisonPill;

public class LobbyServer implements RemoteAccess {

	private boolean running = true;

	private final int port;
	private final GameContainer game;

	public LobbyServer(int port, ArrayList<RemoteAccess> remoteAccesses) {
		this.port = port;
		this.game = new GameContainer(new PlayerContainer(), remoteAccesses,port);
	}

	@Override
	public void run() {
		try (DatagramSocket socket = new DatagramSocket(port)) {
			byte[] incomingData = new byte[3048];
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