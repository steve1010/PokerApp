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

	public enum Option {
		CHECK, FOLD, CALL, RAISE, READY;
	}

	private PlayerActionQuery(InetSocketAddress destAddress, InetSocketAddress srcAddress, Option option, int playerID,
			double raiseValue) {
		super(destAddress, srcAddress);
		this.playerID = playerID;
		this.option = option;
		this.raiseValue = raiseValue;
	}

	public static class PAQBuilder extends NetworkAddressBuilder {
		private int nestedPlayerID;
		private Option nestedOption;
		private double nestedRaiseValue;

		public PAQBuilder(Option newOption) {
			this.nestedOption = newOption;
		}

		public PAQBuilder playerID(int newID) {
			this.nestedPlayerID = newID;
			return this;
		}

		public PAQBuilder raiseValue(double newRaiseValue) {
			this.nestedRaiseValue = newRaiseValue;
			return this;
		}

		@Override
		public PlayerActionQuery build() {
			return new PlayerActionQuery(getSrcAddress(), getDestAddress(), nestedOption, nestedPlayerID,
					nestedRaiseValue);
		}
	}

	public Option getOption() {
		return option;
	}

	public double getAmount() {
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
		// TODO Auto-generated method stub
		return null;
	}

	public List<SafePlayer> getPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<PlayerHand> getPlayerHands() {
		// TODO Auto-generated method stub
		return null;
	}
}
