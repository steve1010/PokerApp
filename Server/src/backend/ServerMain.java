package backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import backend.dao.PokerDB_DAO;

/**
 * Main-Class of Server.<br>
 * <br>
 * Just starts a Lobby Server.
 * <ul>
 * <li>args[0]: server port <br>
 * <i>
 * <li>admin: GameServers ports: <b>serverPort</b>+<b>100 <br>
 * 
 * Running LobbyServer as a Thread is more overhead here, but it's the better
 * choice running everything independently here ease extensions. <br>
 * <br>
 */
public class ServerMain {

	private static ArrayList<RemoteAccess> remoteAccesses;

	public static void main(String[] args) throws IOException {

		PokerDB_DAO dao = new PokerDB_DAO();
		remoteAccesses = new ArrayList<>();
		LobbyServer lobbyServer = new LobbyServer(Integer.parseInt(args[0]), remoteAccesses);
		new Thread(lobbyServer).start();
		remoteAccesses.add(lobbyServer);
		BufferedReader shutdownReader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("\nPress enter to shutdown system.\n");
		shutdownReader.readLine();
		System.out.println("Shutting down...");

		// Shuttung down all remote access implementations
		remoteAccesses.forEach(remoteAccess -> remoteAccess.shutdown());
		System.out.println("completed. Bye!");
	}
}