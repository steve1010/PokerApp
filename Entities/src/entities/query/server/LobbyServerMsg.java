package entities.query.server;

import entities.game.SafePlayer;

public class LobbyServerMsg extends ServerMsg {

	public enum LobbyMsgType {
		NEW_PLAYER_ENROLLED, NEW_GAME_OFFERED, PLAYER_LOGIN, PLAYER_LOGOUT, LAST_PLAYER_ENROLLED

	}// TODO: extract enums to a extra enum file.

	private static final long serialVersionUID = -2853854047072376699L;

	private final LobbyMsgType lmType;

	public LobbyServerMsg(ServerMsg sMsg, LobbyMsgType lmt, String playerName, SafePlayer player) {
		super(sMsg.getSrcAddress(), sMsg.getDestAddress(), sMsg.getMsgType(), sMsg.getId(), sMsg.getGame(),
				sMsg.getUsername(), sMsg.getPlayer(), sMsg.getCard(), sMsg.getEvaluation(), sMsg.getOneResult(),
				sMsg.getPlayers(), sMsg.getGames());
		this.lmType = lmt;
	}

	public static class LobbyServerMsgBuilder {

		private LobbyMsgType nestedLmType;
		private ServerMsg nestedsMsg;
		private String nestedPlayerName;
		private SafePlayer nestedPlayer;

		public LobbyServerMsgBuilder(ServerMsg sMsg, LobbyMsgType type) {
			this.nestedsMsg = sMsg;
			this.nestedLmType = type;
		}

		public LobbyServerMsgBuilder playerName(String name) {
			this.nestedPlayerName = name;
			return this;
		}

		public LobbyServerMsgBuilder player(SafePlayer player) {
			this.nestedPlayer = player;
			return this;
		}

		public LobbyServerMsg build() {
			return new LobbyServerMsg(
					new ServerMsg(nestedsMsg.getSrcAddress(), nestedsMsg.getDestAddress(), nestedsMsg.getMsgType(),
							nestedsMsg.getId(), nestedsMsg.getGame(), nestedsMsg.getPlayer().getName(),
							nestedsMsg.getPlayer(), nestedsMsg.getCard(), nestedsMsg.getEvaluation(),
							nestedsMsg.getOneResult(), nestedsMsg.getPlayers(), nestedsMsg.getGames()),
					nestedLmType, nestedPlayerName, nestedPlayer);
		}
	}

	public LobbyMsgType getLobbyMsgType() {
		return this.lmType;
	}

}
