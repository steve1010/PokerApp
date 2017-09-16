package backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ServerMain {

	private static ArrayList<RemoteAccess> remoteAccesses;

	/**
	 * args[0]: server port
	 */
	public static void main(String[] args) throws IOException {

		remoteAccesses = new ArrayList<>();
		LobbyServer lobbyServer = new LobbyServer(Integer.parseInt(args[0]), remoteAccesses);
		new Thread(lobbyServer).start();
		remoteAccesses.add(lobbyServer);
		BufferedReader shutdownReader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Press enter to shutdown system.");
		shutdownReader.readLine();
		System.out.println("Shutting down...");

		// Shuttung down all remote access implementations
		remoteAccesses.forEach(remoteAccess -> remoteAccess.shutdown());

		System.out.println("completed. Bye!");
	}
}