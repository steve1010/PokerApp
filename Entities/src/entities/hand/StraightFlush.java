package entities.hand;

public final class StraightFlush extends PokerHand {

	private final Straight straight;
	private final Flush flush;

	public StraightFlush(Straight straight, Flush flush) {
		super(8);
		this.straight = straight;
		this.flush = flush;
	}

	public Straight getStraight() {
		return straight;
	}

	public Flush getFlush() {
		return flush;
	}

	@Override
	public String toString() {
		return super.toString() + ", " + valueToName(straight.getBtm()) + " to " + valueToName(straight.getTop());
	}

}
