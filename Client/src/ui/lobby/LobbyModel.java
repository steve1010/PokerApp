package ui.lobby;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import entities.Player;
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

	private static final String SUCCESS_MSG_BEGIN = "Successfully registered.\nTurnament starts on ";
	private static final String SUCCESS_MSG_END = "th player enrolling.\nGood Luck!";
	private static final String ERROR_MESSAGE_0 = "Your bankroll is insufficient! Please choose a cheeper one or load $ on your account!";
	private static final String ERROR_MESSAGE_1 = "Registration closed. Please choose another one!";
	private static final String ERROR_MESSAGE_2 = "You already signed up to this game! Please wait for other players..";

	private ObservableList<IDGame> tableData;
	private Player player;
	private int playerPort;

	@SuppressWarnings("unchecked")
	public LobbyModel(InetSocketAddress serverAdress, Player player) {
		super(serverAdress);
		playerPort = player.getAdress().getPort();
		new Thread(() -> {
			boolean running = true;
			while (running) {
				ServerMsg msg = receiveObjectAsynchronous(playerPort);
				if (msg.getMsgType().equals(ServerMsg.MsgType.NEW_PLAYER_ENROLLED)) {
					incrementSignedUp(msg.getId());
				}
				if (msg.getMsgType().equals(ServerMsg.MsgType.NEW_GAME_OFFERED)) {
					SerializableGame sg = msg.getGame();
					if (!player.getName().equals("Admin")) {
						addNewGameFromServer(toIDGame(sg));
					}
				}
			}
		}).start();
		this.player = player;

		sendObject(new GameQuery(Option.GET_GAMES), playerPort);
		this.tableData = FXCollections
				.observableArrayList(toIDGames((List<SerializableGame>) receiveObject(playerPort)));
	}

	private void addNewGameFromServer(IDGame game) {
		Platform.runLater(() -> tableData.add(game));
		triggerNotification(tableData);
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
		tableData.add(toIDGame((SerializableGame) receiveObject(playerPort)));
		triggerNotification(tableData);
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
				sendObject(new PlayersQuery(selectedGame.getId(), player), playerPort);
				One23 received = (One23) receiveObject(playerPort);
				switch (received.getI()) {
				case 1:
					player.commitTransaction(selectedGame.getBuyIn());
					return createSuccessMsg(selectedGame);
				case 2:
					return ERROR_MESSAGE_1;
				case 3:
					return ERROR_MESSAGE_2;
				default:
					throw new IllegalStateException("---------------- Illegal State ! ------------------");
				}
			}
		}
		return "No game selected!";
	}

	private void incrementSignedUp(int gameID) {
		tableData.get(gameID).incrementSignedUp();
		triggerNotification(tableData);
	}

	private String createSuccessMsg(IDGame selectedGame) {
		return SUCCESS_MSG_BEGIN + selectedGame.getMaxPlayers() + SUCCESS_MSG_END;

	}

	public void init() {
		triggerNotification(tableData);
	}
}