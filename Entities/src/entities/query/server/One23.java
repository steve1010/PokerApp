package entities.query.server;

import java.io.Serializable;

/**
 * <ul>
 * <li>1: true
 * <li>2: game_full
 * <li>3: already inGame
 *
 */
public class One23 implements Serializable {

	private static final long serialVersionUID = 8413071542774685763L;

	private final int i;

	public One23(int i) {
		this.i = i;
	}

	public int getI() {
		return i;
	}

}
