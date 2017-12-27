package entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;

import entities.query.Query;

/**
 * Object that can send and receive objects by using tcp sockets. Extends
 * Observable due to its subclasses always will act like models/DAOs.
 */
public abstract class SenderReceiver extends Observable {

	public Query receiveMsg(int port) {
		printReceiving(port);
		// A socket bind to a specific port
		try (ServerSocket serverSocket = new ServerSocket(port);
				Socket socket = serverSocket.accept();
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
			return (Query) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Illegal State.");
	}

	public void sendMsg(Query toSend) {
		toSend.getSrcAddress().getPort();
		InetAddress receiverAddress = toSend.getDestAddress().getAddress(),
				senderAddress = toSend.getSrcAddress().getAddress();
		printSending(toSend.getDestAddress(), toSend);
		// Socket that connects from given sender port to given address.
		try (Socket socket = new Socket(receiverAddress, toSend.getDestPort(), senderAddress, toSend.getSrcPort());
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
			oos.writeObject(toSend);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printSending(InetSocketAddress destAdress, Query toSend) {
		System.err.println("Sending " + toSend.toString() + "to " + destAdress.toString());
	}

	private void printReceiving(int port) {
		System.err.println("Waiting on port " + port);
	}

	protected InetSocketAddress createLocalhost(int port) {
		return new InetSocketAddress("localhost", port);
	}
}
