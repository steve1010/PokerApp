package entities.hand;

public final class Pair extends PokerHand {

	private final int value;
	private final HighCard[] highCards;

	public Pair(int value, HighCard... highCards) {
		super(1);
		this.value = value;
		this.highCards = highCards;
	}

	public int getValue() {
		return value;
	}

	public HighCard[] getHighCards() {
		return highCards;
	}

	@Override
	public String toString() {
		return super.toString() + " of " + valueToName(value) + "s, "
				+ valueToName(highCards[highCards.length - 1].getValue0()) + " high";
	}

}
