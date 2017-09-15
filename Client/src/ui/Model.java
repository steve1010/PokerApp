package ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Observable;

import entities.SafePlayer;
import entities.query.server.ServerMsg;

public abstract class Model extends Observable {

	protected final InetSocketAddress serverAdress;

	private final int port;

	public Model(InetSocketAddress serverAdress) {
		this.serverAdress = serverAdress;
		this.port = newRandomPort();
	}

	public void sendObject(Object object) {
		try (DatagramSocket clientSocket = new DatagramSocket(port);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(bos)) {
			os.writeObject(object);
			byte[] toSendData = bos.toByteArray();
			DatagramPacket toSendPacket = new DatagramPacket(toSendData, toSendData.length, this.serverAdress);
			clientSocket.send(toSendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendObject(Object object, int portt) {
		try (DatagramSocket clientSocket = new DatagramSocket(portt);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(bos)) {
			os.writeObject(object);
			byte[] toSendData = bos.toByteArray();
			DatagramPacket toSendPacket = new DatagramPacket(toSendData, toSendData.length, this.serverAdress);
			clientSocket.send(toSendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Object receiveObject() {
		try (DatagramSocket clientSocket = new DatagramSocket(port)) {
			byte[] incomingData = new byte[2024];
			DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
			// wait for server response
			clientSocket.receive(incomingPacket);
			byte[] data = incomingPacket.getData();
			try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
				return (Object) ois.readObject();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object receiveObject(int portt) {
		try (DatagramSocket clientSocket = new DatagramSocket(portt)) {
			byte[] incomingData = new byte[2024];
			DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
			// wait for server response
			clientSocket.receive(incomingPacket);
			byte[] data = incomingPacket.getData();
			try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
				return (Object) ois.readObject();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ServerMsg receiveObjectAsynchronous(int portt) {
		try (DatagramSocket clientSocket = new DatagramSocket(portt + 1)) {
			byte[] incomingData = new byte[2024];
			DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
			clientSocket.receive(incomingPacket);
			byte[] data = incomingPacket.getData();
			try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
				return (ServerMsg) ois.readObject();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassCastException castEx) {
				// ignore
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int newRandomPort() {
		return (int) (20000 + Math.random() * 40000);
	}

	public InetSocketAddress getServerAdress() {
		return serverAdress;
	}

	public void triggerNotification(Object o) {
		this.setChanged();
		this.notifyObservers(o);
	}

	public abstract SafePlayer getLoggedInPlayer();

	public abstract String getPw();

}