package entities.query.server;

/**
 * <ul>
 * <li>1: true[success]
 * <li>2: game_full[error]
 * <li>3: already inGame[error]
 * <li>4: last player joined.[success]
 *
 */
public class ServerResult extends ServerMsg {

	private static final long serialVersionUID = 8413071542774685763L;

	public enum Type {
		SUCCESS, ERROR;
	}

	public enum other {
		here_other_values_insteead_of_i_and_b_// !!TODO: see here @ServerResult
	}

	private final int derivation;
	private final Type type;

	private ServerResult(ServerMsg sMsg, int derivation, Type success) {
		super(sMsg.getSrcAddress(), sMsg.getDestAddress(), sMsg.getMsgType(), sMsg.getId(), sMsg.getGame(),
				sMsg.getUsername(), sMsg.getPlayer(), sMsg.getCard(), sMsg.getEvaluation(), sMsg.getOneResult(),
				sMsg.getPlayers(), sMsg.getGames());
		this.derivation = derivation;
		this.type = success;
	}

	public static class ServerResultBuilder {
		private ServerMsg nestedmsg;
		private Type nestedType;
		private int nestedDerivation;

		public ServerResultBuilder(ServerMsg sMsg, Type type) {
			this.nestedmsg = sMsg;
			this.nestedType = type;
		}

		public ServerResultBuilder derivation(int d) {
			this.nestedDerivation = d;
			return this;
		}

		public ServerResult build() {
			return new ServerResult(nestedmsg, nestedDerivation, nestedType);
		}
	}

	public int getDerivation() {
		return derivation;
	}

	public Type getType() {
		return type;
	}

}
