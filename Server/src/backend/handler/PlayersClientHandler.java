package backend.handler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import entities.Player;
import entities.query.PlayersQuery;
import entities.query.Query;
import entities.query.server.One23;
import entities.query.server.ServerMsg;
import entities.query.server.ServerMsg.MsgType;
import logic.container.GameContainer;
import logic.container.PlayerContainer;

public class PlayersClientHandler extends ClientHandler {

	private final GameContainer gameContainer;

	public PlayersClientHandler(Query received, InetSocketAddress clientAdress, GameContainer game) {
		super(received, clientAdress);
		this.gameContainer = game;
	}

	@Override
	public void run() {
		switchQuery(getReceived());
	}

	@Override
	public void switchQuery(Query receivedQuery) {
		PlayerContainer playerStore = gameContainer.getPlayerStore();
		PlayersQuery received = (PlayersQuery) receivedQuery;
		switch (((PlayersQuery) received).getOption()) {
		case GET:
			try {
				List<String> lines = Files.readAllLines(Paths.get("users/users.txt"));
				for (String line : lines) {

					String registedUser = line.split(" -> ")[0];
					if (registedUser.equals(received.getPlayerName())) {
						answer(playerStore.getPlayerByName(received.getPlayerName()));
						return;
					}
				}
				answer(new Player(-1, null, null));
			} catch (IOException e) {
				e.printStackTrace();

			}
			break;

		case GETALL:
			answer(playerStore.getAll());
			break;

		case LOGIN:
			try {
				List<String> lines = Files.readAllLines(Paths.get("users/users.txt"));
				for (String line : lines) {

					String[] usernamePw = line.split(" -> ");
					String username = usernamePw[0];
					String pw = usernamePw[1];

					if (username.equals(received.getPlayerName()) && pw.equals(received.getPw())) {
						answer(playerStore.addPlayer(username, getClientAdress()));
					}
				}
				answer(new Player(-1, null, null));
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case REGISTER:
			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("users/users.txt"),
					StandardOpenOption.APPEND)) {
				writer.newLine();
				writer.write(received.getPlayerName() + " -> " + received.getPw());
				answer(playerStore.addPlayer(received.getPlayerName(), getClientAdress()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case LOGOUT:
			try {
				List<String> lines = Files.readAllLines(Paths.get("users/users.txt"));
				for (String line : lines) {

					String[] usernamePw = line.split(" -> ");
					String username = usernamePw[0];
					String pw = usernamePw[1];

					if (username.equals(received.getPlayerName()) && pw.equals(received.getPw())) {
						playerStore.setLoggedOut(username);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case ENROLL:
			One23 result = gameContainer.addPlayerToGame(received.getPlayer(), received.getGameId());
			if (result.getI() == 1) {
				answer(result);

				// inform all:
				List<Player> players = gameContainer.getPlayerStore().getAll();

				for (Player player : players) {
					// Convention: clients are listening on sending port+1 on a
					// extra thread for server infos.
					int val = 1 + player.getAdress().getPort();
					answer(new ServerMsg(MsgType.NEW_PLAYER_ENROLLED, received.getGameId()),
							new InetSocketAddress(player.getAdress().getAddress(), val));
				}
			} else {
				answer(result);
			}
			break;
		default:
			throw new IllegalStateException("-----------Illegal state!!-------------");
		}
	}
}