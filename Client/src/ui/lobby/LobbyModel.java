package ui.lobby;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import entities.SafePlayer;
import entities.lobby.Game;
import entities.lobby.IDGame;
import entities.lobby.SerializableGame;
import entities.query.GameQuery;
import entities.query.GameQuery.Option;
import entities.query.PlayersQuery;
import entities.query.server.One23;
import entities.query.server.ServerMsg;
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

	private ObservableList<IDGame> gamesTableData;
	private SafePlayer player;
	private int playerPort;
	private final String playerPw;
	private ObservableList<SafePlayer> playersTableData;

	@SuppressWarnings("unchecked")
	public LobbyModel(InetSocketAddress serverAdress, SafePlayer loggedInPlayer, String pw) {
		super(serverAdress);
		this.player = loggedInPlayer;
		this.playerPort = loggedInPlayer.getAdress().getPort();
		this.playerPw = pw;
		runAsyncListener(0);

		// fetch games
		sendObject(new GameQuery(Option.GET_GAMES), playerPort);
		this.gamesTableData = FXCollections
				.observableArrayList(toIDGames((List<SerializableGame>) receiveObject(playerPort)));
		// fetch loggedInPlayers
		sendObject(new PlayersQuery(entities.query.PlayersQuery.Option.GETALL), playerPort);
		this.playersTableData = FXCollections.observableArrayList((ArrayList<SafePlayer>) receiveObject(playerPort));

	}

	private void runAsyncListener(int addition) {
		new Thread(() -> {
			boolean running = true;
			while (running) {
				ServerMsg msg = receiveObjectAsynchronous(playerPort + addition);

				switch (msg.getMsgType()) {
				case NEW_PLAYER_ENROLLED:
					incrementSignedUp(msg.getId());
					player.getGamesIdList().add(msg.getId());
					break;
				case NEW_GAME_OFFERED:
					SerializableGame sg = msg.getGame();
					if (!player.getName().equals("Admin")) {
						addNewGameFromServer(toIDGame(sg));
					}
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
					triggerNotification(new ClientInterna(1, playersTableData));
					break;

				case LAST_PLAYER_ENROLLED:
					incrementSignedUp(msg.getId());
					player.getGamesIdList().add(msg.getId());
					List<Integer> games = player.getGamesIdList().stream()
							.filter(gameId -> gameId.intValue() == msg.getId()).collect(Collectors.toList());
					if (!games.isEmpty()) {
						triggerNotification(new ClientInterna(2, games.get(0).intValue()));
					}
					break;
				default:
					throw new IllegalArgumentException("Unsupported argument: ´" + msg.getMsgType() + '´');
				}
			}
		}).start();

	}

	private void addNewGameFromServer(IDGame game) {
		Platform.runLater(() -> gamesTableData.add(game));
		triggerNotification(gamesTableData);
	}

	private List<IDGame> toIDGames(List<SerializableGame> receivedGames) {
		List<IDGame> idgames = new ArrayList<>();
		for (SerializableGame game : receivedGames) {
			idgames.add(toIDGame(game));
		}
		return idgames;
	}

	public void createGame(String name, double buyIn, int startChips, int maxPlayers, int paid) {
		Game game = new Game(name, buyIn, startChips, maxPlayers, paid, 0);
		sendObject(new GameQuery(game), playerPort);
		gamesTableData.add(toIDGame((SerializableGame) receiveObject(playerPort)));
		triggerNotification(gamesTableData);
	}

	private IDGame toIDGame(SerializableGame sg) {
		return new IDGame(new Game(sg.getName(), sg.getBuyIn(), sg.getStartChips(), sg.getMaxPlayers(), sg.getPaid(),
				sg.getSignedUp()), sg.getId());
	}

	public String enrollPlayerIn(IDGame selectedGame) {
		if (selectedGame != null) {
			if (this.player.getBankRoll().doubleValue() < selectedGame.getBuyIn().doubleValue()) {
				return ERROR_MESSAGE_0;
			} else {
				System.out.println("trying to enroll in :" + selectedGame.getName());
				sendObject(new PlayersQuery(selectedGame.getId(), player), playerPort);
				One23 received = (One23) receiveObject(playerPort);
				switch (received.getI()) {
				case 1:
					player.commitTransaction(selectedGame.getId(), selectedGame.getBuyIn());
					return createSuccessMsg(selectedGame);
				case 2:
					return ERROR_MESSAGE_1;
				case 3:
					return ERROR_MESSAGE_2;
				case 4:
					player.commitTransaction(selectedGame.getId(), selectedGame.getBuyIn());
					return SUCCESS_MSG_1;
				default:
					throw new IllegalStateException("---------------- Illegal State ! ------------------");
				}
			}
		}
		return "No game selected!";
	}

	private void incrementSignedUp(int gameID) {
		IDGame game = gamesTableData.get(gameID);
		game.incrementSignedUp();
		Platform.runLater(() -> {
			gamesTableData.set(gameID, game);
		});
		triggerNotification(gamesTableData);
	}

	private String createSuccessMsg(IDGame selectedGame) {
		return SUCCESS_MSG_0_BEGIN + selectedGame.getMaxPlayers() + SUCCESS_MSG_0_END;

	}

	public void init() {
		triggerNotification(new ClientInterna(0, gamesTableData));
		triggerNotification(new ClientInterna(1, playersTableData));
	}

	public void logoutUser() {
		sendObject(new PlayersQuery(entities.query.PlayersQuery.Option.LOGOUT, player.getName(), playerPw));
	}

	@Override
	public SafePlayer getLoggedInPlayer() {
		return this.player;
	}

	@Override
	public String getPw() {
		return this.playerPw;
	}

	public ObservableList<IDGame> getGamesTableData() {
		return gamesTableData;
	}
}