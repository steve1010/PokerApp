package entities.query.game;

import java.net.InetSocketAddress;

import entities.game.Game;
import entities.game.SafePlayer;
import entities.query.NetworkAddressBuilder;
import entities.query.Query;

/**
 * Subclass of Query to give PlayersActions a semantic representation. Used
 * BuilderPattern here due to big amount of constructors with different
 * argumentlists would be required otherwise.
 */
public final class PlayerQuery extends Query {

	private static final long serialVersionUID = -7893151765580772416L;

	public enum Option {
		EXISTS, GETALL, LOGIN, REGISTER, LOGOUT, ENROLL;
	}

	private final Option option;
	private SafePlayer player;
	private final String playerName;
	private final String pw;
	private Game game;
	private int playerID;

	private PlayerQuery(InetSocketAddress srcAddress, InetSocketAddress destAddress, Option option, SafePlayer player,
			String playerName, String pw, Game game, int playerID) {
		super(srcAddress, destAddress);
		this.option = option;
		this.player = player;
		this.playerName = playerName;
		this.pw = pw;
		this.game = game;
		this.playerID = playerID;
	}

	public static class PQBuilder extends NetworkAddressBuilder {
		private Option nestedOption;
		private SafePlayer nestedPlayer;
		private String nestedPlayerName;
		private String nestedPw;
		private Game nestedGame;
		private int nestedPlayerID;

		public PQBuilder(PlayerQuery query) {
			if (query != null) {
				this.nestedOption = query.getOption();
				this.nestedPlayer = query.getPlayer();
				this.nestedPlayerName = query.getPlayerName();
				this.nestedPw = query.getPw();
				this.nestedGame = query.getGame();
				this.nestedPlayerID = query.getPlayerID();
			}
		}

		public PQBuilder(Option option) {
			this.nestedOption = option;
		}

		public PQBuilder player(SafePlayer newPlayer) {
			this.nestedPlayer = newPlayer;
			return this;
		}

		public PQBuilder playerId(int newID) {
			this.nestedPlayerID = newID;
			return this;
		}

		public PQBuilder pName(String newPName) {
			this.nestedPlayerName = newPName;
			return this;
		}

		public PQBuilder pW(String newPw) {
			this.nestedPw = newPw;
			return this;
		}

		public PQBuilder game(Game newGame) {
			this.nestedGame = newGame;
			return this;
		}

		@Override
		public PlayerQuery build() {
			return new PlayerQuery(getSrcAddress(), getDestAddress(), nestedOption, nestedPlayer, nestedPlayerName,
					nestedPw, nestedGame, nestedPlayerID);
		}

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

	@Override
	public String toString() {
		return "PlayersQuery [option=" + option + ", player=" + player + ", playerName=" + playerName + ", pw=" + pw
				+ ", game=" + game + ", playerID=" + playerID + "]";
	}
}