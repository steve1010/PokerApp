package backend;

import entities.SenderReceiver;
import entities.query.Query;

public abstract class Server extends SenderReceiver {

	public void answer(Query msg) {
		sendMsg(msg);
	}

}
