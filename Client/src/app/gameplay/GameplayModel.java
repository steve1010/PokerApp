package app.gameplay;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import app.gameplay.GameplayClientInterna.ActionType;
import app.gameplay.GameplayClientInterna.Type;
import app.ui.ClientModel;
import entities.game.Game;
import entities.game.SafePlayer;
import entities.game.play.Board;
import entities.game.play.Card;
import entities.game.play.PlayerHand;
import entities.query.game.GameQuery;
import entities.query.game.PlayerActionQuery;
import entities.query.game.PlayerActionQuery.Option;
import entities.query.server.GameServerMsg;

public class GameplayModel extends ClientModel {

	private final SafePlayer loggedInPlayer;
	private final String pw;
	private final Game game;
	private final int gamePlayPort;

	public GameplayModel(InetSocketAddress serverAdress, SafePlayer loggedInPlayer, String pw, Game game) {
		super(serverAdress);
		this.loggedInPlayer = loggedInPlayer;
		this.gamePlayPort = loggedInPlayer.getAdress().getPort() + 2;
		this.pw = pw;
		this.game = game;
		runAsyncListener();
	}

	private void runAsyncListener() {
		new Thread(() -> {
			boolean running = true;
			while (running) {
				System.err.print("GameplayModel ");
				GameServerMsg msg = (GameServerMsg) receiveMsgAsynchronous(loggedInPlayer.getAdress().getPort() + 1);
				if (msg == null) {
					running = false;
					break;
				}
				System.err.println("\nGameplayModel: type='" + msg.getGameMsgType() + "'");

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
					triggerNotification(
							new GameplayClientInterna(Type.POSITION, msg.getWinnerID(), game.getMaxPlayers()));
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
			addressAndSend(new GameQuery.GQBuilder(entities.query.game.GameQuery.Option.NEW_CARD).build());
			Object received = receiveMsg(gamePlayPort);
			if (received instanceof Card) {
				cards.add((Card) received);
			}
		}
		return cards;

	}

	public void newRound() {
		addressAndSend(new GameQuery.GQBuilder(entities.query.game.GameQuery.Option.NEW_ROUND).build());
	}

	/***
	 * 
	 * BIG TODO: !!!!wTHE HELL ARE PLAYER HANDS STORED ON CLIENT-SIDE !??! EACH
	 * PLAYER.. SHOULD ONLY RECEIVE playerHands on RESULT (and then also only if
	 * necessary regarding game rules.)! (EVALUATION) AND OWN HAND! MASSIVE SECURITY
	 * LAG! <br>
	 * <br>
	 * TODO: @MajorConstraint evaluation turned off here:
	 * 
	 * @param board
	 * @param hands
	 * @return
	 */
	public String[] evaluate(Board board, List<PlayerHand> hands) {
		// sendQuery(new Evaluation(board, hands), gamePlayPort);
		Object receiced = receiveMsg(gamePlayPort);
		if (receiced instanceof String[]) {
			return (String[]) receiced;
		}
		throw new IllegalArgumentException();
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
		addressAndSend(new PlayerActionQuery.PAQBuilder(Option.CALL).playerID(loggedInPlayer.getId()).build());
	}

	public void userChecked() {
		addressAndSend(new PlayerActionQuery.PAQBuilder(Option.CHECK).playerID(loggedInPlayer.getId()).build());
	}

	public void userFolded() {
		addressAndSend(new PlayerActionQuery.PAQBuilder(Option.FOLD).playerID(loggedInPlayer.getId()).build());
	}

	public void userRaised(double raiseValue) {
		addressAndSend(new PlayerActionQuery.PAQBuilder(Option.RAISE).playerID(loggedInPlayer.getId())
				.raiseValue(raiseValue).build());
	}

	public int getGamePlayPort() {
		return gamePlayPort;
	}
}