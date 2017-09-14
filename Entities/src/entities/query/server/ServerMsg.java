package entities.query.server;

import java.io.Serializable;

import entities.SafePlayer;
import entities.lobby.SerializableGame;

public class ServerMsg implements Serializable {

	private static final long serialVersionUID = 2650348213355515896L;

	public enum MsgType {
		NEW_PLAYER_ENROLLED, NEW_GAME_OFFERED, PLAYER_LOGOUT, PLAYER_LOGIN, LAST_PLAYER_ENROLLED;
	}

	private final MsgType msgType;
	private final int id;
	private final SerializableGame game;
	private final String username;
	private final SafePlayer player;

	public ServerMsg(MsgType msgType, int id) {
		this.msgType = msgType;
		this.id = id;
		this.game = null;
		this.username = null;
		this.player = null;
	}

	public ServerMsg(MsgType msgType, int id, SerializableGame game) {
		this.msgType = msgType;
		this.id = id;
		this.game = game;
		this.username = null;
		this.player = null;
	}

	public ServerMsg(MsgType type, String username) {
		this.msgType = type;
		this.username = username;
		this.id = -1;
		this.game = null;
		this.player = null;
	}

	public ServerMsg(MsgType type, SafePlayer safePlayer) {
		this.msgType = type;
		this.username = safePlayer.getName();
		this.id = safePlayer.getId();
		this.player = safePlayer;
		this.game = null;
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

	public String getUsername() {
		return username;
	}

	public SafePlayer getPlayer() {
		return player;
	}
}