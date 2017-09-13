package entities.query;

import entities.Player;

public final class PlayersQuery extends Query {

	private static final long serialVersionUID = -7893151765580772416L;

	public enum Option {
		GET, GETALL, LOGIN, REGISTER, LOGOUT, ENROLL;
	}

	private final Option option;
	private Player player;
	private final String playerName;
	private final String pw;
	private int gameID;
	private int playerID;

	public PlayersQuery(Option option) {
		this.option = option;
		this.playerName = null;
		pw = null;
	}

	public PlayersQuery(Option option, String name) {
		this.option = option;
		this.playerName = name;
		this.pw = null;
	}

	public PlayersQuery(String name) {
		this.option = Option.GET;
		this.playerName = name;
		this.pw = null;
	}

	public PlayersQuery(String name, String pw) {
		this.option = Option.LOGIN;
		playerName = name;
		this.pw = pw;
	}

	/**
	 * TODO: maybe remove option parameter here..
	 */
	public PlayersQuery(Option option, String name, String pw) {
		this.option = Option.REGISTER;
		this.playerName = name;
		this.pw = pw;
	}

	public PlayersQuery(int gameID, Player player) {
		this.option = Option.ENROLL;
		this.gameID = gameID;
		this.player = player;
		this.playerName = player.getName();
		this.pw = null;
	}

	public Option getOption() {
		return option;
	}

	public String getPlayerName() {
		return playerName;
	}

	public String getPw() {
		return pw;
	}

	public int getGameId() {
		return gameID;
	}

	public int getPlayerID() {
		return playerID;
	}

	public Player getPlayer() {
		return this.player;
	}

}
