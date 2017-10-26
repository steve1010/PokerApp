package entities.query;

import entities.Game;
import entities.SafePlayer;

public final class PlayersQuery extends Query {

	private static final long serialVersionUID = -7893151765580772416L;

	public enum Option {
		GET, GETALL, LOGIN, REGISTER, LOGOUT, ENROLL;
	}

	private final Option option;
	private SafePlayer player;
	private final String playerName;
	private final String pw;
	private Game game;
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

	public PlayersQuery(Option option, String name, String pw) {
		this.option = option;
		this.playerName = name;
		this.pw = pw;
	}

	public PlayersQuery(Game game, SafePlayer player) {
		this.option = Option.ENROLL;
		this.game = game;
		this.player = player;
		this.pw = null;
		this.playerName = player.getName();
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

	public Game getGame() {
		return game;
	}

	public int getPlayerID() {
		return playerID;
	}

	public SafePlayer getPlayer() {
		return this.player;
	}
}