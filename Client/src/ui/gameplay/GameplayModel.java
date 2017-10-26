package ui.gameplay;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import entities.Game;
import entities.SafePlayer;
import entities.gameplay.Board;
import entities.gameplay.Card;
import entities.gameplay.PlayerHand;
import entities.query.Evaluation;
import entities.query.GameQuery;
import entities.query.PlayersActionQuery;
import entities.query.PlayersActionQuery.Option;
import entities.query.server.GamesServerMsg;
import ui.Model;
import ui.gameplay.GameplayClientInterna.ActionType;
import ui.gameplay.GameplayClientInterna.Type;

public class GameplayModel extends Model {

	private final SafePlayer loggedInPlayer;
	private final String pw;
	private final Game game;

	public GameplayModel(InetSocketAddress serverAdress, SafePlayer loggedInPlayer, String pw, Game game) {
		super(serverAdress);
		this.loggedInPlayer = loggedInPlayer;
		this.pw = pw;
		this.game = game;
		runAsyncListener();
	}

	private void runAsyncListener() {
		new Thread(() -> {
			boolean running = true;
			while (running) {
				System.err.println("Gameplay-Client listening on port: " + (loggedInPlayer.getAdress().getPort() + 2));
				GamesServerMsg msg = (GamesServerMsg) receiveObjectAsynchronous(
						loggedInPlayer.getAdress().getPort() + 1);
				System.err.println("Gameplay-Client received.");
				if (msg == null) {
					running = false;
					break;
				}
				switch (msg.getGameMsgType()) {

				case YOUR_TURN:
					triggerNotification(new GameplayClientInterna(Type.YOUR_TURN));
					break;
				case USER_CHECKS:
					triggerNotification(new GameplayClientInterna(Type.ACTION, ActionType.CHECK));
					break;
				case USER_CALLS:
					triggerNotification(new GameplayClientInterna(Type.ACTION, ActionType.CALL));
					break;
				case USER_FOLDS:
					triggerNotification(new GameplayClientInterna(Type.ACTION, ActionType.FOLD));
					break;
				case USER_RAISES:
					triggerNotification(new GameplayClientInterna(Type.ACTION, ActionType.RAISE,
							msg.getMinRoundBet().getMinRoundBet()));
					break;
				case YOUR_POSITION:
					triggerNotification(new GameplayClientInterna(Type.POSITION, msg.getId(), game.getMaxPlayers()));
					break;
				case ROUND_END:
					triggerNotification(new GameplayClientInterna(msg.getWinnerID()));
					break;
				default:
					break;
				}
			}
		}).start();
	}

	public List<Card> deal() {
		List<Card> cards = new ArrayList<>(1);
		for (int i = 0; i < 25; i++) {
			sendObject(new GameQuery(entities.query.GameQuery.Option.NEW_CARD));
			Object received = receiveObject();
			if (received instanceof Card) {
				cards.add((Card) received);
			}
		}
		return cards;

	}

	public void newRound() {
		sendObject(new GameQuery(entities.query.GameQuery.Option.NEW_ROUND));
	}

	public String[] evaluate(Board board, List<PlayerHand> hands) {
		sendObject(new Evaluation(board, hands));
		Object receiced = receiveObject();
		if (receiced instanceof String[]) {
			return (String[]) receiced;
		}
		return null;
	}

	@Override
	public SafePlayer getLoggedInPlayer() {
		return this.loggedInPlayer;
	}

	@Override
	public String getPw() {
		return this.pw;
	}

	public Game getGame() {
		return game;
	}

	public void userCalled() {
		sendObject(new PlayersActionQuery(loggedInPlayer.getId(), Option.CALL));
	}

	public void userChecked() {
		sendObject(new PlayersActionQuery(loggedInPlayer.getId(), Option.CHECK));
	}

	public void userFolded() {
		sendObject(new PlayersActionQuery(loggedInPlayer.getId(), Option.FOLD));
	}

	public void userRaised(double raiseValue) {
		sendObject(new PlayersActionQuery(loggedInPlayer.getId(), raiseValue));
	}
}