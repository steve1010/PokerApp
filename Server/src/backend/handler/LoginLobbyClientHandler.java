package backend.handler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import entities.SafePlayer;
import entities.lobby.IDGame;
import entities.lobby.SerializableGame;
import entities.query.PlayersQuery;
import entities.query.Query;
import entities.query.server.One23;
import entities.query.server.ServerMsg;
import entities.query.server.ServerMsg.MsgType;
import logic.container.GameContainer;
import logic.container.PlayerContainer;

public class LoginLobbyClientHandler extends ClientHandler {

	private final GameContainer gameContainer;

	public LoginLobbyClientHandler(Query received, InetSocketAddress clientAdress, GameContainer game) {
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
				answer(new SafePlayer(-1, null, null, null));
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
						SafePlayer sp = playerStore.addPlayer(username, pw, getClientAdress());
						answer(sp);
						triggerServerMsg(new ServerMsg(MsgType.PLAYER_LOGIN, sp));
						break;
					}
				}
				answer(new SafePlayer(-1, null, null, null));
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case REGISTER:
			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("users/users.txt"),
					StandardOpenOption.APPEND)) {
				writer.newLine();
				writer.write(received.getPlayerName() + " -> " + received.getPw());
				SafePlayer sp = playerStore.addPlayer(received.getPlayerName(), received.getPw(), getClientAdress());
				answer(sp);
				triggerServerMsg(new ServerMsg(MsgType.PLAYER_LOGIN, sp));
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
						triggerServerMsg(new ServerMsg(MsgType.PLAYER_LOGOUT,
								gameContainer.getPlayerStore().getPlayerByName(username)));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case ENROLL:
			One23 result = gameContainer.addPlayerToGame(received.getPlayer(),
					IDGame.fromSerializableGame(received.getGame()));
			answer(result);//TODO: may crush on multiple threads (lost update)
			SerializableGame game = received.getGame();
			game.addPlayer(received.getPlayer());
			if (result.getI() == 1) {
				triggerServerMsg(new ServerMsg(MsgType.NEW_PLAYER_ENROLLED, received.getPlayer(), game.getId()));
				break;
			}
			if (result.getI() == 4) {
				triggerServerMsg(new ServerMsg(MsgType.LAST_PLAYER_ENROLLED, received.getPlayer(), game.getId()));
				break;
			}
			break;
		default:
			throw new IllegalStateException("-----------Illegal state!!-------------");
		}
	}

	@Override
	public void triggerServerMsg(ServerMsg serverMsg) {
		// inform all players
		List<SafePlayer> players = gameContainer.getPlayerStore().getAll();
		players.forEach(pl -> {
			// Convention: clients are listening on sending port+1 on a
			// extra thread for server infos.
			int asyncClientPort = 1 + pl.getAdress().getPort();
			answer(serverMsg, new InetSocketAddress(pl.getAdress().getAddress(), asyncClientPort));
		});

	}
}