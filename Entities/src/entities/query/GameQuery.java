package entities.query;

import entities.lobby.Game;

public class GameQuery extends Query {

	private static final long serialVersionUID = 4360075462574926945L;
	private final Option option;
	private Game game;

	public enum Option {
		NEW_ROUND, NEW_GAME, NEW_CARD, EVALUATION, GET_GAMES;
	}

	public GameQuery(Option option) {
		this.option = option;
	}

	public GameQuery(Game game) {
		this.option = Option.NEW_GAME;
		this.game = game;
	}

	public Option getOption() {
		return option;
	}

	public Game getGame() {
		return game;
	}

}
