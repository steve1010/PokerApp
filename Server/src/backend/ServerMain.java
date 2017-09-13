package backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerMain {

	/**
	 * args[0]: server port
	 */
	public static void main(String[] args) throws IOException {

		LobbyServer remoteAccess = new LobbyServer(Integer.parseInt(args[0]));
		new Thread(remoteAccess).start();
		BufferedReader shutdownReader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Press enter to shutdown system.");
		shutdownReader.readLine();
		System.out.println("Shutting down...");

		// Shuttung down all remote access implementations
		remoteAccess.shutdown();
		System.out.println("completed. Bye!");
	}

}
