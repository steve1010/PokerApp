package entities.query.server;

import java.io.Serializable;

import entities.lobby.SerializableGame;

public class ServerMsg implements Serializable {

	private static final long serialVersionUID = 2650348213355515896L;

	public enum MsgType {
		NEW_PLAYER_ENROLLED, NEW_GAME_OFFERED;
	}

	private final MsgType msgType;
	private final int id;
	private final SerializableGame game;

	public ServerMsg(MsgType msgType, int id) {
		this.msgType = msgType;
		this.id = id;
		this.game = null;
	}

	public ServerMsg(MsgType msgType, int id, SerializableGame game) {
		this.msgType = msgType;
		this.id = id;
		this.game = game;
	}

	public MsgType getMsgType() {
		return msgType;
	}

	public int getId() {
		return id;
	}

	public SerializableGame getGame() {
		return this.game;
	}
}