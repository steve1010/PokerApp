package app.ui;

import java.net.InetSocketAddress;

import entities.DBConnector;
import entities.PoisonPill;
import entities.SenderReceiver;
import entities.game.SafePlayer;
import entities.query.Query;
import entities.query.game.GameQuery;
import entities.query.game.PlayerActionQuery;
import entities.query.game.PlayerQuery;
import entities.query.server.ServerMsg;

/**
 * Used for sending objects by using Client superclass.<br>
 * Client ports are distributed as following: <br>
 * <ul>
 * <li>Login-Port = randomPort()
 * <li>AsyncWaitingLobbyPort=Login-Port+1
 * <li>LobbyPort=Login-Port
 * <li>GameplayPort=Lobby-Port+1
 * <li>evtl. asyncGameplayPort=GameplayPort+1
 * <li>Poison-Pills are sent from: LoginPort+3 to asyncWaitingLobbyPort, evtl.
 * aysncGameplayPort to close corresponding views.
 */
public abstract class ClientModel extends SenderReceiver {

	protected final InetSocketAddress serverAdress, clientAddress, clientPoisenPillSenderAddress;

	private DBConnector database;

	public ClientModel(InetSocketAddress serverAdress) {
		this.serverAdress = serverAdress;
		int port = newRandomPort();
		clientAddress = createLocalhost(port); // TODO:  - -port problem: -  shall be free!?
		this.clientPoisenPillSenderAddress = createLocalhost(port + 3);
	}

	/**
	 * Add dest/src address & send via {@linkplain SenderReceiver}.
	 * 
	 * @param type
	 * @param query
	 */
	public void addressAndSend(Query query) {
		if (query instanceof GameQuery) {
			sendMsg(new GameQuery.GQBuilder((GameQuery) query).srcAddress(clientAddress).destAddress(serverAdress)
					.build());
		}
		if (query instanceof PlayerActionQuery) {
			sendMsg(new PlayerActionQuery.PAQBuilder((PlayerActionQuery) query).srcAddress(clientAddress)
					.destAddress(serverAdress).build());
		}
		if (query instanceof PlayerQuery) {
			sendMsg(new PlayerQuery.PQBuilder((PlayerQuery) query).destAddress(serverAdress).srcAddress(clientAddress)
					.build());
		}
	}

	public void sendPoisonPill(InetSocketAddress destAddress) {
		sendMsg(new PoisonPill(destAddress, clientPoisenPillSenderAddress));
	}

	public ServerMsg receiveMsgAsynchronous(int port) {
		return (ServerMsg) receiveMsg(port + 1);
	}

	private int newRandomPort() {
		return (int) (10000 + Math.random() * 54000); // TODO: expand this to a server-request to ensure port-safety.
	}

	public InetSocketAddress getServerAdress() {
		return serverAdress;
	}

	public void triggerNotification(ClientInterna o) {
		this.setChanged();
		this.notifyObservers(o);
	}

	public abstract SafePlayer getLoggedInPlayer();

	public abstract String getPw();

	public DBConnector getDatabase() {
		return database;
	}

}