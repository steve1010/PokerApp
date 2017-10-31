package entities.gameplay.hand;

public final class TwoPair extends PokerHand {

	private final Pair pair1, pair2;
	private final HighCard highCard;

	public TwoPair(Pair pair1, Pair pair2, HighCard highCard) {
		super(2);
		this.pair1 = pair1;
		this.pair2 = pair2;
		this.highCard = highCard;
	}

	public Pair getPair1() {
		return pair1;
	}

	public Pair getPair2() {
		return pair2;
	}

	public HighCard getHighCard() {
		return highCard;
	}

	@Override
	public String toString() {
		return super.toString() + ", " + valueToName(pair1.getValue()) + "s & " + valueToName(pair2.getValue()) + "s, "
				+ valueToName(highCard.getValue0()) + " high";
	}
}
