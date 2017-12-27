package entities.game.play;

public class MinRoundBet {

	private final double minRoundBet;

	public MinRoundBet(double minRoundBet) {
		this.minRoundBet = minRoundBet;
	}

	public synchronized double getMinRoundBet() {
		return minRoundBet;
	}
}