package entities.query.server;

public class MinRoundBet implements ServerMsgObject {

	private static final long serialVersionUID = -2033576749613000390L;

	private double minRoundBet;

	public MinRoundBet(double minRoundBet) {
		this.setMinRoundBet(minRoundBet);
	}

	public synchronized double getMinRoundBet() {
		return minRoundBet;
	}

	public synchronized void setMinRoundBet(double minRoundBet) {
		this.minRoundBet = minRoundBet;
	}

}
