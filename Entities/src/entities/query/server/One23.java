package entities.query.server;

/**
 * <ul>
 * <li>1: true[success]
 * <li>2: game_full[error]
 * <li>3: already inGame[error]
 * <li>4: last player joined.[success]
 *
 */
public class One23 implements ServerMsgObject {

	private static final long serialVersionUID = 8413071542774685763L;

	public enum Bool {
		SUCCESS, ERROR;
	}

	private final int i;
	private final Bool b;

	public One23(int i, Bool success) {
		this.i = i;
		this.b = success;
	}

	public int getI() {
		return i;
	}

	public Bool getB() {
		return b;
	}

}
