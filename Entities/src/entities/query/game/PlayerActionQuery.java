package entities.query.game;

import java.net.InetSocketAddress;
import java.util.List;

import entities.game.SafePlayer;
import entities.game.play.MinRoundBet;
import entities.game.play.PlayerHand;
import entities.query.NetworkAddressBuilder;
import entities.query.Query;

public class PlayerActionQuery extends Query {

	private static final long serialVersionUID = 342128053777549408L;
	private final int playerID;
	private final Option option;
	private final double raiseValue;
	private final MinRoundBet minRoundBet;
	private final List<SafePlayer> players;
	private final List<PlayerHand> playerHands;

	public enum Option {
		CHECK, FOLD, CALL, RAISE, READY;
	}

	private PlayerActionQuery(InetSocketAddress destAddress, InetSocketAddress srcAddress, Option option, int playerID,
			double raiseValue, MinRoundBet minRoundBet, List<SafePlayer> players, List<PlayerHand> playerHands) {
		super(destAddress, srcAddress);
		this.playerID = playerID;
		this.option = option;
		this.raiseValue = raiseValue;
		this.minRoundBet = minRoundBet;
		this.players = players;
		this.playerHands = playerHands;
	}

	public static class PAQBuilder extends NetworkAddressBuilder {

		private int nestedPlayerID;
		private Option nestedOption;
		private double nestedRaiseValue;
		private MinRoundBet nestedMinRoundBet;
		private List<SafePlayer> nestedPlayers;
		private List<PlayerHand> nestedPlayerHands;

		public PAQBuilder(PlayerActionQuery query) {
			if (query != null) {
				this.nestedOption = query.getOption();
				this.nestedPlayerID = query.getId();
				this.nestedRaiseValue = query.getRaiseValue();
				this.nestedMinRoundBet = query.getMinRoundBet();
				this.nestedPlayers = query.getPlayers();
				this.nestedPlayerHands = query.getPlayerHands();
			}
		}

		public PAQBuilder(Option option) {
			this.nestedOption = option;
		}

		public PAQBuilder playerID(int newID) {
			this.nestedPlayerID = newID;
			return this;
		}

		public PAQBuilder raiseValue(double newRaiseValue) {
			this.nestedRaiseValue = newRaiseValue;
			return this;
		}

		public PAQBuilder minRoundBet(MinRoundBet mrb) {
			this.nestedMinRoundBet = mrb;
			return this;
		}

		public PAQBuilder players(List<SafePlayer> players) {
			this.nestedPlayers = players;
			return this;
		}

		public PAQBuilder playerHands(List<PlayerHand> playerHands) {
			this.nestedPlayerHands = playerHands;
			return this;
		}

		@Override
		public PlayerActionQuery build() {
			return new PlayerActionQuery(getSrcAddress(), getDestAddress(), nestedOption, nestedPlayerID,
					nestedRaiseValue, nestedMinRoundBet, nestedPlayers, nestedPlayerHands);
		}
	}

	public Option getOption() {
		return option;
	}

	public double getRaiseValue() {
		return raiseValue;
	}

	public int getId() {
		return playerID;
	}

	@Override
	public String toString() {
		return "PlayersActionQuery [id=" + playerID + ", option=" + option + ", amount=" + raiseValue + "]";
	}

	public MinRoundBet getMinRoundBet() {
		return this.minRoundBet;
	}

	public List<SafePlayer> getPlayers() {
		return this.players;
	}

	public List<PlayerHand> getPlayerHands() {
		return this.playerHands;
	}
}
