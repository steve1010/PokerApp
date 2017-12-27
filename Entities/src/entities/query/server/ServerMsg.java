package entities.query.server;

import java.net.InetSocketAddress;
import java.util.List;

import entities.game.Game;
import entities.game.SafePlayer;
import entities.game.play.Card;
import entities.query.NetworkAddressBuilder;
import entities.query.Query;

public class ServerMsg extends Query {

	private static final long serialVersionUID = 2650348213355515896L;
	protected static final InetSocketAddress SERVER_STATIC_ADDRESS_REMOVE_AFTER_TESTING$ = new InetSocketAddress(50000);

	public enum MsgType {

		GAME, PLAYER;
		// NEW_PLAYER_ENROLLED, NEW_GAME_OFFERED, PLAYER_LOGOUT, PLAYER_LOGIN,
		// PLAYER_NEW_REGISTERED, GAMES_LIST, LAST_PLAYER_ENROLLED, GAMES_SERVER_MSG,
		// NEW_CARD, EVALUATION, PLAYERS_LIST, NONE, One23;
	}

	private final MsgType msgType;
	private final int id;
	private final Game game;
	private final String username;
	private final SafePlayer player;
	private final Card card;
	private final String[] evaluation;
	private final ServerResult one23;
	private List<SafePlayer> playersList;
	private List<Game> gamesList;

	protected ServerMsg(InetSocketAddress srcAddress, InetSocketAddress destAddress, MsgType msgType, int id, Game game,
			String username, SafePlayer player, Card card, String[] evaluation, ServerResult one23,
			List<SafePlayer> playersList, List<Game> gamesList) {
		super(srcAddress, destAddress);
		this.msgType = msgType;
		this.id = id;
		this.game = game;
		this.username = username;
		this.player = player;
		this.card = card;
		this.evaluation = evaluation;
		this.one23 = one23;
		this.playersList = playersList;
		this.gamesList = gamesList;
	}

	public static class SMBuilder extends NetworkAddressBuilder {

		private MsgType nestedMsgType;
		private int nestedID;
		private Game nestedGame;
		private String nestedUsername;
		private SafePlayer nestedPlayer;
		private Card nestedCard;
		private String[] nestedEvaluation;
		private ServerResult nestedOne23;
		private List<SafePlayer> nestedPlayersList;
		private List<Game> nestedGamesList;

		public SMBuilder(MsgType type) {
			this.nestedMsgType = type;
		}

		public SMBuilder id(int newID) {
			this.nestedID = newID;
			return this;
		}

		public SMBuilder game(Game newGame) {
			this.nestedGame = newGame;
			return this;
		}

		public SMBuilder pName(String newName) {
			this.nestedUsername = newName;
			return this;
		}

		public SMBuilder player(SafePlayer newPlayer) {
			this.nestedPlayer = newPlayer;
			return this;
		}

		public SMBuilder card(Card card) {
			this.nestedCard = card;
			return this;
		}

		public SMBuilder evaluation(String[] newEvaluation) {
			this.nestedEvaluation = newEvaluation;
			return this;
		}

		public SMBuilder one23(ServerResult newResult) {
			this.nestedOne23 = newResult;
			return this;
		}

		public SMBuilder players(List<SafePlayer> newPlayers) {
			this.nestedPlayersList = newPlayers;
			return this;
		}

		public SMBuilder games(List<Game> newGames) {
			this.nestedGamesList = newGames;
			return this;
		}

		@Override
		public Query build() {
			return new ServerMsg(getSrcAddress(), getDestAddress(), nestedMsgType, nestedID, nestedGame, nestedUsername,
					nestedPlayer, nestedCard, nestedEvaluation, nestedOne23, nestedPlayersList, nestedGamesList);
		}

	}

	public MsgType getMsgType() {
		return msgType;
	}

	public int getId() {
		return id;
	}

	public Game getGame() {
		return this.game;
	}

	public String getUsername() {
		return username;
	}

	public SafePlayer getPlayer() {
		return player;
	}

	@Override
	public String toString() {
		return "ServerMsg [msgType=" + msgType + ", id=" + id + ", game=" + game + ", username=" + username
				+ ", player=" + player + "]";
	}

	public List<Game> getGames() {
		return gamesList;
	}

	public Card getCard() {
		return card;
	}

	public String[] getEvaluation() {
		return evaluation;
	}

	public ServerResult getOneResult() {
		return one23;
	}

	public List<SafePlayer> getPlayers() {
		return this.playersList;
	}
}