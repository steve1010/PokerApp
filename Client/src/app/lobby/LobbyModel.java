package app.lobby;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

import app.lobby.LobbyClientInterna.Type;
import app.ui.ClientModel;
import entities.PoisonPill;
import entities.game.Game;
import entities.game.Player;
import entities.game.SafePlayer;
import entities.query.Query;
import entities.query.game.GameQuery;
import entities.query.game.GameQuery.Option;
import entities.query.game.PlayerQuery;
import entities.query.server.LobbyServerMsg;
import entities.query.server.ServerResult;
import entities.query.server.ServerMsg;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LobbyModel extends ClientModel {

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
		addressAndSend(new GameQuery.GQBuilder(GameQuery.Option.GET_GAMES).build());
		this.gamesTableData = FXCollections.observableArrayList((List<Game>) receiveMsg(playerPort));
		// fetch loggedInPlayers
		addressAndSend(new PlayerQuery.PQBuilder(entities.query.game.PlayerQuery.Option.GETALL).build());
		this.playersTableData = FXCollections.observableArrayList(((ServerMsg) receiveMsg(playerPort)).getPlayers());
	}

	private void runAsyncListener() {
		new Thread(() -> {
			boolean running = true;
			while (running) {
				System.err.print("LobbyModel ");
				//TODO: SEND MSG: GIVE ME A NEW PORT
				Query query = receiveMsgAsynchronous(playerPort);
				LobbyServerMsg msg;
				if (query instanceof PoisonPill) {
					running = false;
					break;
				}
				msg = (LobbyServerMsg) query;
				System.err.println("\nLobbyModel: " + msg.getMsgType().toString());

				switch (msg.getLobbyMsgType()) {
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
					triggerNotification(new LobbyClientInterna(Type.UPDATE_PLAYERS, playersTableData));
					break;
				case PLAYER_LOGOUT:
					System.out.println("loggin out : " + msg.getId());
					Platform.runLater(() -> {
						playersTableData.remove(playersTableData.stream().filter(e -> e.getId() == msg.getId())
								.collect(Collectors.toList()).get(0));
					});
					triggerNotification(new LobbyClientInterna(Type.UPDATE_PLAYERS, playersTableData));
					break;
				case LAST_PLAYER_ENROLLED:
					newPlayerEnrolled(msg.getId(),
							msg.getPlayer());/** TODO: extract method into Game: game.filter(cond) */
					List<Game> games = player.getGamesList().stream().filter(game -> game.getId() == msg.getId())
							.collect(Collectors.toList());
					if (!games.isEmpty()) {
						triggerNotification(new LobbyClientInterna(Type.GAME_START, games.get(0).getId()));
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
		triggerNotification(new LobbyClientInterna(Type.UPDATE_GAMES, gamesTableData));// TODO: required?
	}

	public void createGame(String name, double buyIn, int startChips, int maxPlayers, int paid) {
		Game game = new Game(name, gamesTableData.size(), buyIn, startChips, maxPlayers, paid, 0);
		addressAndSend(new GameQuery.GQBuilder(Option.NEW_GAME).game(game).build());
	}

	public String enrollPlayerIn(Game game) {
		if (game != null) {
			if (this.player.getBankRoll() < game.getBuyIn().doubleValue()) {
				return ERROR_MESSAGE_0;
			}
			if (game.getMaxPlayers() == game.getSignedUp()) {
				return ERROR_MESSAGE_1;
			} else {
				addressAndSend(new PlayerQuery.PQBuilder(entities.query.game.PlayerQuery.Option.ENROLL).game(game)
						.player(Player.toSafePlayer(player)).build());
				ServerResult received = ((ServerMsg) receiveMsg(playerPort)).getOneResult();
				switch (received.getDerivation()) {
				case 1:
					player.commitTransaction(game, game.getBuyIn());
					return createSuccessMsg(game);
				case 2:
					return ERROR_MESSAGE_1;
				case 3:
					return ERROR_MESSAGE_2;
				case 4:
					player.commitTransaction(game, game.getBuyIn());
					return SUCCESS_MSG_1;
				default:
					throw new IllegalStateException("------- Illegal State ! --------");
				}
			}
		}
		return "No game selected!";
	}

	private void newPlayerEnrolled(int gameID, SafePlayer player) {
		this.gamesTableData.get(gameID).addPlayer(player);
		triggerNotification(new LobbyClientInterna(Type.UPDATE_GAMES, gamesTableData));
	}

	private String createSuccessMsg(Game selectedGame) {
		return SUCCESS_MSG_0_BEGIN + selectedGame.getMaxPlayers() + SUCCESS_MSG_0_END;

	}

	public void init() {
		triggerNotification(new LobbyClientInterna(Type.UPDATE_GAMES, gamesTableData));
		triggerNotification(new LobbyClientInterna(Type.UPDATE_PLAYERS, playersTableData));
	}

	public void logoutUser() {
		addressAndSend(new PlayerQuery.PQBuilder(entities.query.game.PlayerQuery.Option.LOGOUT).pName(player.getName())
				.pW(playerPw).build());
		poisonPill();
	}

	private void poisonPill() {
		sendPoisonPill(new InetSocketAddress("localhost", (playerPort + 1)));
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