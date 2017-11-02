package entities.query.server;

import entities.gameplay.PlayerHand;

public class GameServerMsg extends ServerMsg implements ServerMsgObject {

	private static final long serialVersionUID = -8683652218666171970L;

	public enum GameMsgType {
		YOUR_TURN, USER_CHECKS, USER_FOLDS, USER_CALLS, USER_RAISES, YOUR_POSITION, ROUND_END, YOUR_HAND;
	}

	private final GameMsgType gameMsgType;
	private MinRoundBet minRoundBet;
	private int winnerID;
	private PlayerHand playerHand;

	public GameServerMsg(MsgType msgType, int id, GameMsgType gameMsgType) {
		super(MsgType.GAMES_SERVER_MSG, id);
		this.gameMsgType = gameMsgType;
	}

	public GameServerMsg(MsgType msgType, int id, GameMsgType gamesMsgType, MinRoundBet minRoundBet) {
		this(msgType, id, gamesMsgType);
		this.minRoundBet = minRoundBet;
	}

	public GameServerMsg(MsgType msgType, int id, GameMsgType gamesMsgType, int winnerID) {
		this(msgType, id, gamesMsgType);
		this.winnerID = winnerID;
	}

	public GameServerMsg(MsgType gamesServerMsg, int i, GameMsgType yourHand, PlayerHand playerHand) {
		super(gamesServerMsg, i);
		this.gameMsgType = yourHand;
		this.playerHand = playerHand;
	}

	public GameMsgType getGameMsgType() {
		return gameMsgType;
	}

	public MinRoundBet getMinRoundBet() {
		return minRoundBet;
	}

	public int getWinnerID() {
		return winnerID;
	}

	public PlayerHand getPlayerHand() {
		return playerHand;
	}

}
