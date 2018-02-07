package backend.handler.sub;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import backend.Server;
import backend.gameplay.container.GameContainer;
import backend.gameplay.container.PlayerContainer;
import backend.handler.TcpClientHandler;
import entities.game.Game;
import entities.game.SafePlayer;
import entities.query.Query;
import entities.query.game.PlayerQuery;
import entities.query.server.LobbyServerMsg;
import entities.query.server.LobbyServerMsg.LobbyMsgType;
import entities.query.server.ServerMsg;
import entities.query.server.ServerResult;

public class LoginLobbyClientHandler extends TcpClientHandler {

	private final GameContainer gameContainer;

	public LoginLobbyClientHandler(Query received, InetSocketAddress clientAdress, GameContainer game) {
		super(received);
		this.gameContainer = game;
	}

	@Override
	public void run() {
		switchQuery(getReceived());
	}

	@Override
	public void switchQuery(Query receivedQuery) {
		PlayerContainer playerStore = gameContainer.getPlayerStore();
		PlayerQuery received = (PlayerQuery) receivedQuery;
		final ServerMsg rcvMsg = Server.serverMsg(received);
		switch (((PlayerQuery) received).getOption()) {
		case EXISTS:
			try {
				List<String> lines = Files.readAllLines(Paths.get("users/users.txt"));
				// TODO: change here to DB request makes it much shorter
				for (String line : lines) {
					String registedUser = line.split(" -> ")[0];
					if (registedUser.equals(received.getPlayerName())) {
						answer(new LobbyServerMsg.LSMBuilder(rcvMsg, LobbyMsgType.PLAYER_EXISTS).build());
						return;
					}
				}
				answer(new LobbyServerMsg.LSMBuilder(Server.serverMsg(received), LobbyMsgType.PLAYER_EXISTS_NOT)
						.build());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case GETALL:
			answer(new LobbyServerMsg.LSMBuilder(rcvMsg, LobbyMsgType.PLAYERS).players(playerStore.getLoggedInPlayers()).build());
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
						answer(new LobbyServerMsg.LSMBuilder(rcvMsg, LobbyMsgType.PLAYER_LOGIN).player(sp).build());
						// TODO: sill required? all clients are informed below ?
						triggerServerMsg(
								new LobbyServerMsg.LSMBuilder(rcvMsg, LobbyMsgType.PLAYER_LOGIN).player(sp).build());
						break;
					}
				}
				answer(new LobbyServerMsg.LSMBuilder(rcvMsg, LobbyMsgType.PLAYER_EXISTS_NOT).build());
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
				answer(new LobbyServerMsg.LSMBuilder(rcvMsg, LobbyMsgType.PLAYER_LOGIN).player(sp).build());
				triggerServerMsg(new LobbyServerMsg.LSMBuilder(rcvMsg, LobbyMsgType.PLAYER_LOGIN).player(sp).build());
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
						triggerServerMsg(new LobbyServerMsg.LSMBuilder(rcvMsg, LobbyMsgType.PLAYER_LOGOUT)
								.player(gameContainer.getPlayerStore().getPlayerByName(username)).build());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case ENROLL:// TODO: dosnt work yet.
			ServerResult result = gameContainer.addPlayerToGame(received.getPlayer(), received.getGame());
			// answer(new ServerMsg(receivedQuery.getSrcAddress(), result));
			Game game = received.getGame();
			game.addPlayer(received.getPlayer());
			if (result.getDerivation() == 1) {
				// triggerServerMsg(new ServerMsg(receivedQuery.getSrcAddress(),
				// MsgType.NEW_PLAYER_ENROLLED,
				// received.getPlayer(), game.getId()));
				break;
			}
			if (result.getDerivation() == 4) {
				// triggerServerMsg(new ServerMsg(receivedQuery.getSrcAddress(),
				// MsgType.LAST_PLAYER_ENROLLED,
				// received.getPlayer(), game.getId()));
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
		List<SafePlayer> players = gameContainer.getPlayerStore().getLoggedInPlayers();
		players.forEach(pl -> {
			// Convention: clients are listening on sending port+1 on a
			// extra thread for server infos.
			int asyncClientPort = 1 + pl.getAdress().getPort();
			answer(new LobbyServerMsg.LSMBuilder(Server.asyncServerMsg(serverMsg, asyncClientPort),
					((LobbyServerMsg) serverMsg).getLobbyMsgType()).build());
		});

	}
}