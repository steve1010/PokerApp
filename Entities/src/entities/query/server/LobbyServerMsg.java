package entities.query.server;

import java.util.List;

import entities.game.SafePlayer;
import entities.query.NetworkAddressBuilder;

/**
 * TODO: Steve: urgent: delete all ServerMsgs, implement 1 Protocol to message.
 * 
 * @author -steve-
 *
 */
public class LobbyServerMsg extends ServerMsg {

	public enum LobbyMsgType {
		NEW_PLAYER_ENROLLED, NEW_GAME_OFFERED, PLAYER_LOGIN, PLAYER_LOGOUT, LAST_PLAYER_ENROLLED, PLAYER_EXISTS, PLAYER_EXISTS_NOT, PLAYERS

	}// TODO: @MediumConstraint extract enums to a extra enum file.

	private static final long serialVersionUID = -2853854047072376699L;

	private final LobbyMsgType lmType;

	public LobbyServerMsg(ServerMsg sMsg, LobbyMsgType lmt, String playerName, SafePlayer player) {
		super(sMsg.getSrcAddress(), sMsg.getDestAddress(), sMsg.getMsgType(), sMsg.getId(), sMsg.getGame(),
				sMsg.getUsername(), sMsg.getPlayer(), sMsg.getCard(), sMsg.getEvaluation(), sMsg.getOneResult(),
				sMsg.getPlayers(), sMsg.getGames());
		this.lmType = lmt;
	}

	public static class LSMBuilder extends NetworkAddressBuilder {

		private LobbyMsgType nestedLmType;
		private ServerMsg nestedsMsg;
		private String nestedPlayerName;
		private SafePlayer nestedPlayer;
		private List<SafePlayer> nestedPlayers;

		public LSMBuilder(ServerMsg sMsg, LobbyMsgType type) {
			this.nestedsMsg = sMsg;
			this.nestedLmType = type;
		}

		public LSMBuilder playerName(String name) {
			this.nestedPlayerName = name;
			return this;
		}

		public LSMBuilder player(SafePlayer player) {
			this.nestedPlayer = player;
			return this;
		}

		public LSMBuilder players(List<SafePlayer> newPlayers) {
			this.nestedPlayers = newPlayers;
			return this;
		}

		public LobbyServerMsg build() {
			if (nestedPlayers == null && nestedsMsg != null) {
				nestedPlayers = nestedsMsg.getPlayers();
			}
			return new LobbyServerMsg(new ServerMsg(nestedsMsg.getSrcAddress(), nestedsMsg.getDestAddress(),
					nestedsMsg.getMsgType(), nestedsMsg.getId(), nestedsMsg.getGame(), nestedPlayerName, nestedPlayer,
					nestedsMsg.getCard(), nestedsMsg.getEvaluation(), nestedsMsg.getOneResult(), nestedPlayers,
					nestedsMsg.getGames()), nestedLmType, nestedPlayerName, nestedPlayer);
		}
	}

	public LobbyMsgType getLobbyMsgType() {
		return this.lmType;
	}

}
