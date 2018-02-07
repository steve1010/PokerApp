package backend.handler.sub;

import java.net.InetSocketAddress;
import java.util.List;

import backend.handler.TcpClientHandler;
import entities.game.SafePlayer;
import entities.game.play.MinRoundBet;
import entities.game.play.PlayerHand;
import entities.query.Query;
import entities.query.game.PlayerActionQuery;
import entities.query.server.GameServerMsg;
import entities.query.server.GameServerMsg.GameMsgType;
import entities.query.server.ServerMsg;
import entities.query.server.ServerMsg.MsgType;

public class PlayerActionClientHandler extends TcpClientHandler {

	private final PlayerActionQuery playerQuery;
	private final List<SafePlayer> playersList;
	private final List<PlayerHand> playerHandsList;
	private final MinRoundBet minRoundBet;

	public PlayerActionClientHandler(PlayerActionQuery playersActionQuery, List<SafePlayer> list,
			List<PlayerHand> playerHandsList, MinRoundBet minRoundBet) {
		super(playersActionQuery);
		this.playerQuery = playersActionQuery;
		this.playersList = list;
		this.playerHandsList = playerHandsList;
		this.minRoundBet = minRoundBet;
	}

	/**
	 * TODO: This will be the remaining constructor of this class.
	 * 
	 * @param query
	 */
	public PlayerActionClientHandler(PlayerActionQuery query) {
		super(query);
		this.playerQuery = query;
		this.minRoundBet = query.getMinRoundBet();
		this.playersList = query.getPlayers();
		this.playerHandsList = query.getPlayerHands();
	}

	@Override
	public void run() {
		switchQuery(playerQuery);
	}

	@Override
	public void switchQuery(Query received) {
		PlayerActionQuery query = (PlayerActionQuery) received;
		switch (query.getOption()) {

		case READY:
			// TODO: @MajorConstraint PlayerActions turned off here :
			break;
		case CHECK:
			// triggerServerMsg(new GameServerMsg(received.getSrcAddress(), MsgType.GAME,
			// -5, GameMsgType.USER_CHECKS));
			break;
		case FOLD:
			playerHandsList.remove(query.getId());
			if (playerHandsList.size() == 1) {
				// triggerServerMsg(new GameServerMsg(received.getSrcAddress(), MsgType.GAME,
				// -5, GameMsgType.ROUND_END,
				// playerHandsList.get(0).getId()));
			}
			// triggerServerMsg(new GameServerMsg(received.getSrcAddress(), MsgType.GAME,
			// -5, GameMsgType.USER_FOLDS));
			break;
		case CALL:
			// triggerServerMsg(new GameServerMsg(received.getSrcAddress(), MsgType.GAME,
			// -5, GameMsgType.USER_CALLS));
			break;
		case RAISE:
			// this.minRoundBet.setMinRoundBet(query.getAmount());
			// triggerServerMsg(new GameServerMsg(received.getSrcAddress(), MsgType.GAME,
			// -5, GameMsgType.USER_RAISES,
			// new MinRoundBet(query.getAmount())));
			break;
		default:
			break;
		}
	}

	@Override
	public void triggerServerMsg(ServerMsg serverMsg) {
		playersList.forEach(player -> answer(serverMsg));// TODO: @Urgent set dest in all msgs before sending..
		// new InetSocketAddress(player.getAdress().getAddress(),
		// player.getAdress().getPort() + 2)));
	}

	public MinRoundBet getMinRoundBet() {
		return minRoundBet;
	}
}