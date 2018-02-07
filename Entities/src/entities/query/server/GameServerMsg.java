package entities.query.server;

import java.net.InetSocketAddress;
import java.util.List;

import entities.game.Game;
import entities.game.SafePlayer;
import entities.game.play.Card;
import entities.game.play.MinRoundBet;
import entities.game.play.PlayerHand;
import entities.query.NetworkAddressBuilder;

public class GameServerMsg extends ServerMsg {

	private static final long serialVersionUID = -8683652218666171970L;

	public enum GameMsgType {
		YOUR_TURN, USER_CHECKS, USER_FOLDS, USER_CALLS, USER_RAISES, YOUR_POSITION, ROUND_END, YOUR_HAND, TIMEOUT;
	}

	private final GameMsgType gameMsgType;
	private MinRoundBet minRoundBet;
	private int winnerID;
	private PlayerHand playerHand;

	private GameServerMsg(InetSocketAddress srcAddress, InetSocketAddress destAddress, MsgType msgType, int id,
			Game game, String username, SafePlayer player, Card card, String[] evaluation, ServerResult result,
			List<SafePlayer> playersList, List<Game> gamesList, GameMsgType gmt, MinRoundBet mrb, int winnerID,
			PlayerHand ph) {
		super(srcAddress, destAddress, msgType, id, game, username, player, card, evaluation, result, playersList,
				gamesList);
		this.gameMsgType = gmt;
		this.minRoundBet = mrb;
		this.winnerID = winnerID;
		this.playerHand = ph;
	}

	public static class GameServerMsgBuilder extends NetworkAddressBuilder {
		private GameMsgType nestedGameMsgType;
		private MinRoundBet nestedMinRoundBet;
		private int nestedWinnerID;
		private PlayerHand nestedPlayerHand;
		private final ServerMsg sMsg;

		public GameServerMsgBuilder(ServerMsg serverMsg) {
			this.sMsg = serverMsg;
		}

		public GameServerMsgBuilder gameMsgType(GameMsgType type) {
			this.nestedGameMsgType = type;
			return this;
		}

		public GameServerMsgBuilder minRoundBet(MinRoundBet mrb) {
			this.nestedMinRoundBet = mrb;
			return this;
		}

		public GameServerMsgBuilder winnerID(int winId) {
			this.nestedWinnerID = winId;
			return this;
		}

		public GameServerMsgBuilder playerHand(PlayerHand ph) {
			this.nestedPlayerHand = ph;
			return this;
		}

		public GameServerMsg build() {
			return new GameServerMsg(sMsg.getSrcAddress(), sMsg.getDestAddress(), sMsg.getMsgType(), sMsg.getId(),
					sMsg.getGame(), sMsg.getUsername(), sMsg.getPlayer(), sMsg.getCard(), sMsg.getEvaluation(),
					sMsg.getOneResult(), sMsg.getPlayers(), sMsg.getGames(), nestedGameMsgType, nestedMinRoundBet,
					nestedWinnerID, nestedPlayerHand);
		}
	}

	public GameMsgType getGameMsgType() {
		return gameMsgType;
	}

	public MinRoundBet getMinRoundBet() {
		return minRoundBet;
	}

	public int getWinnerID() {
		return winnerID;
	}

	public PlayerHand getPlayerHand() {
		return playerHand;
	}
}