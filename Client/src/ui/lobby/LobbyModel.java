package ui.lobby;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import entities.Game;
import entities.Player;
import entities.SafePlayer;
import entities.query.GameQuery;
import entities.query.GameQuery.Option;
import entities.query.PlayersQuery;
import entities.query.server.One23;
import entities.query.server.PoisonPill;
import entities.query.server.ServerMsg;
import entities.query.server.ServerMsgObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ui.Model;

public class LobbyModel extends Model {

	private static final String SUCCESS_MSG_0_BEGIN = "Successfully registered.\nTurnament starts on ";
	private static final String SUCCESS_MSG_0_END = "th player enrolling.\nGood Luck!";
	private static final String SUCCESS_MSG_1 = "Last player registered.\nStarting turnament..";
	private static final String ERROR_MESSAGE_0 = "Your bankroll is insufficient! Please choose a cheeper one or load $ on your account!";
	private static final String ERROR_MESSAGE_1 = "Registration closed. Please choose another one!";
	private static final String ERROR_MESSAGE_2 = "You already signed up to this game! Please wait for other players..";

	private ObservableList<Game> gamesTableData;
	private Player player;
	private int playerPort;
	private final String playerPw;
	private ObservableList<SafePlayer> playersTableData;

	@SuppressWarnings("unchecked")
	public LobbyModel(InetSocketAddress serverAdress, Player loggedInPlayer, String pw) {
		super(serverAdress);
		this.player = loggedInPlayer;
		this.playerPort = loggedInPlayer.getAdress().getPort();
		this.playerPw = pw;
		runAsyncListener();

		// fetch games
		sendObject(new GameQuery(Option.GET_GAMES), playerPort);
		this.gamesTableData = FXCollections.observableArrayList((List<Game>) receiveObject(playerPort));
		// fetch loggedInPlayers
		sendObject(new PlayersQuery(entities.query.PlayersQuery.Option.GETALL), playerPort);
		this.playersTableData = FXCollections.observableArrayList((ArrayList<SafePlayer>) receiveObject(playerPort));

	}

	private void runAsyncListener() {
		new Thread(() -> {
			boolean running = true;
			while (running) {
				ServerMsgObject msgObj = receiveObjectAsynchronous(playerPort);
				ServerMsg msg;
				if (msgObj instanceof PoisonPill) {
					msg = null;
				} else {
					msg = (ServerMsg) msgObj;
				}
				if (msg == null) {
					running = false;
					break;
				}
				switch (msg.getMsgType()) {
				case NEW_PLAYER_ENROLLED:
					newPlayerEnrolled(msg.getId(), msg.getPlayer());
					break;
				case NEW_GAME_OFFERED:
					Game sg = msg.getGame();
					addNewGameFromServer(sg);
					break;

				case PLAYER_LOGIN:
					Platform.runLater(() -> {
						playersTableData.add((msg.getPlayer()));
					});
					triggerNotification(playersTableData);
					break;

				case PLAYER_LOGOUT:
					System.out.println("loggin out : " + msg.getId());
					Platform.runLater(() -> {
						playersTableData.remove(playersTableData.stream().filter(e -> e.getId() == msg.getId())
								.collect(Collectors.toList()).get(0));
					});
					triggerNotification(new LobbyClientInterna(1, playersTableData));
					break;

				case LAST_PLAYER_ENROLLED:
					newPlayerEnrolled(msg.getId(), msg.getPlayer());
					List<Game> games = player.getGamesList().stream().filter(game -> game.getId() == msg.getId())
							.collect(Collectors.toList());
					if (!games.isEmpty()) {
						triggerNotification(new LobbyClientInterna(2, games.get(0).getId()));
					}
					break;
				default:
					throw new IllegalArgumentException("Unsupported argument: ´" + msg.getMsgType() + '´');
				}
			}
		}).start();
	}

	private void addNewGameFromServer(Game game) {
		player.getGamesList().add(game);
		Platform.runLater(() -> gamesTableData.add(game));
		triggerNotification(gamesTableData);
	}

	public void createGame(String name, double buyIn, int startChips, int maxPlayers, int paid) {
		Game game = new Game(name, gamesTableData.size(), buyIn, startChips, maxPlayers, paid, 0);
		sendObject(new GameQuery(game), playerPort);
	}

	public String enrollPlayerIn(Game selectedGame) {
		if (selectedGame != null) {
			if (this.player.getBankRoll() < selectedGame.getBuyIn().doubleValue()) {
				return ERROR_MESSAGE_0;
			}
			Game cur = this.gamesTableData.stream().filter(g -> g.getId() == selectedGame.getId())
					.collect(Collectors.toList()).get(0);
			if (cur.getMaxPlayers() == cur.getSignedUp()) {
				return ERROR_MESSAGE_1;
			} else {
				sendObject(new PlayersQuery(selectedGame, Player.toSafePlayer(player)), playerPort);
				One23 received = (One23) receiveObject(playerPort);
				switch (received.getI()) {
				case 1:
					player.commitTransaction(selectedGame, selectedGame.getBuyIn());
					return createSuccessMsg(selectedGame);
				case 2:
					return ERROR_MESSAGE_1;
				case 3:
					return ERROR_MESSAGE_2;
				case 4:
					player.commitTransaction(selectedGame, selectedGame.getBuyIn());
					return SUCCESS_MSG_1;
				default:
					throw new IllegalStateException("---------------- Illegal State ! ------------------");
				}
			}
		}
		return "No game selected!";
	}

	private void newPlayerEnrolled(int gameID, SafePlayer player) {
		List<Game> games = this.player.getGamesList();
		games.get(gameID).addPlayer(player);
		this.player = new Player(player.getId(), player.getName(), playerPw, player.getAdress(), games);
		Game game = gamesTableData.get(gameID);
		game.addPlayer(player);
		Platform.runLater(() -> {
			gamesTableData.set(gameID, game);
		});
		triggerNotification(gamesTableData);
	}

	private String createSuccessMsg(Game selectedGame) {
		return SUCCESS_MSG_0_BEGIN + selectedGame.getMaxPlayers() + SUCCESS_MSG_0_END;

	}

	public void init() {
		triggerNotification(new LobbyClientInterna(0, gamesTableData));
		triggerNotification(new LobbyClientInterna(1, playersTableData));
	}

	public void logoutUser() {
		sendObject(new PlayersQuery(entities.query.PlayersQuery.Option.LOGOUT, player.getName(), playerPw));
		poisonPill();
	}

	private void poisonPill() {
		try (DatagramSocket clientSocket = new DatagramSocket();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(bos)) {
			os.writeObject(new PoisonPill());
			byte[] toSendData = bos.toByteArray();
			DatagramPacket toSendPacket = new DatagramPacket(toSendData, toSendData.length,
					new InetSocketAddress("localhost", playerPort + 1));
			clientSocket.send(toSendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public SafePlayer getLoggedInPlayer() {
		return this.player;
	}

	@Override
	public String getPw() {
		return this.playerPw;
	}

	public ObservableList<Game> getGamesTableData() {
		return gamesTableData;
	}
}