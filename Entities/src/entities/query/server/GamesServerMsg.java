package entities.query.server;

public class GamesServerMsg extends ServerMsg {

	private static final long serialVersionUID = -8683652218666171970L;

	public enum GameMsgType {
		YOUR_TURN, USER_CHECKS, USER_FOLDS, USER_CALLS;
	}

	private final GameMsgType gameMsgType;

	public GamesServerMsg(MsgType msgType, int id, GameMsgType gameMsgType) {
		super(MsgType.GAMES_SERVER_MSG, id);
		this.gameMsgType = gameMsgType;
	}

	public GameMsgType getGameMsgType() {
		return gameMsgType;
	}

}
