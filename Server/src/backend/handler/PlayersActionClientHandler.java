package backend.handler;

import java.net.InetSocketAddress;
import java.util.List;

import entities.SafePlayer;
import entities.gameplay.PlayerHand;
import entities.query.PlayersActionQuery;
import entities.query.Query;
import entities.query.server.GamesServerMsg;
import entities.query.server.GamesServerMsg.GameMsgType;
import entities.query.server.MinRoundBet;
import entities.query.server.ServerMsg;
import entities.query.server.ServerMsg.MsgType;

public class PlayersActionClientHandler extends ClientHandler {

	private final PlayersActionQuery playerQuery;
	private final List<SafePlayer> playersList;
	private final List<PlayerHand> playerHandsList;
	private final MinRoundBet minRoundBet;

	public PlayersActionClientHandler(PlayersActionQuery playersActionQuery, List<SafePlayer> list,
			List<PlayerHand> playerHandsList, MinRoundBet minRoundBet) {
		super(new byte[1], list.get(0).getAdress());
		this.playerQuery = playersActionQuery;
		this.playersList = list;
		this.playerHandsList = playerHandsList;
		this.minRoundBet = minRoundBet;
	}

	@Override
	public void run() {
		switchQuery(playerQuery);
	}

	@Override
	public void switchQuery(Query received) {
		PlayersActionQuery query = (PlayersActionQuery) received;
		switch (query.getOption()) {
		case CHECK:
			triggerServerMsg(new GamesServerMsg(MsgType.GAMES_SERVER_MSG, -5, GameMsgType.USER_CHECKS));
			break;
		case FOLD:
			playerHandsList.remove(query.getId());
			if (playerHandsList.size() == 1) {
				triggerServerMsg(new GamesServerMsg(MsgType.GAMES_SERVER_MSG, -5, GameMsgType.ROUND_END,
						playerHandsList.get(0).getId()));
			}
			triggerServerMsg(new GamesServerMsg(MsgType.GAMES_SERVER_MSG, -5, GameMsgType.USER_FOLDS));
			break;
		case CALL:
			triggerServerMsg(new GamesServerMsg(MsgType.GAMES_SERVER_MSG, -5, GameMsgType.USER_CALLS));
			break;
		case RAISE:
			this.minRoundBet.setMinRoundBet(query.getAmount());
			triggerServerMsg(new GamesServerMsg(MsgType.GAMES_SERVER_MSG, -5, GameMsgType.USER_RAISES,
					new MinRoundBet(query.getAmount())));
			break;
		default:
			break;
		}
	}

	@Override
	public void triggerServerMsg(ServerMsg serverMsg) {
		playersList.forEach(player -> answer(serverMsg,
				new InetSocketAddress(player.getAdress().getAddress(), player.getAdress().getPort() + 2)));
	}

	public MinRoundBet getMinRoundBet() {
		return minRoundBet;
	}
}