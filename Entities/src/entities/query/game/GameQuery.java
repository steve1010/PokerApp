package entities.query.game;

import java.net.InetSocketAddress;

import entities.game.Game;
import entities.query.NetworkAddressBuilder;
import entities.query.Query;

public class GameQuery extends Query {

	private static final long serialVersionUID = 4360075462574926945L;
	private final Option option;
	private final Game game;

	public enum Option {
		NEW_ROUND, NEW_GAME, NEW_CARD, EVALUATION, GET_GAMES;
	}

	private GameQuery(InetSocketAddress srcAddress, InetSocketAddress destAddress, Option option, Game game) {
		super(srcAddress, destAddress);
		this.option = option;
		this.game = game;
	}

	public static class GQBuilder extends NetworkAddressBuilder {
		private Option nestedOption;
		private Game nestedGame;

		public GQBuilder(GameQuery query) {
			if (query != null) {
				this.nestedOption = query.getOption();
				this.nestedGame = query.getGame();
			}
		}

		public GQBuilder(Option option) {
			this.nestedOption = option;
		}

		public GQBuilder game(Game newGame) {
			this.nestedGame = newGame;
			return this;
		}

		public GQBuilder option(Option newOption) {
			this.nestedOption = newOption;
			return this;
		}

		@Override
		public GameQuery build() {
			return new GameQuery(getSrcAddress(), getDestAddress(), nestedOption, nestedGame);
		}

	}

	public Option getOption() {
		return option;
	}

	public Game getGame() {
		return game;
	}

	@Override
	public String toString() {
		return "GameQuery [option=" + option + ", game=" + game + "]";
	}

}
