package entities.game.hand;

public final class ThreeOfAKind extends PokerHand {

	private final int value;
	private final HighCard[] highCards;

	public ThreeOfAKind(int value, HighCard... highCards) {
		super(3);
		this.value = value;
		if (highCards.length != 2) {
			throw new IllegalArgumentException("Must be 2 highcards.");
		}
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
		return super.toString() + ", " + valueToName(value) + "s, "
				+ valueToName(highCards[highCards.length - 1].getValue0()) + " high";
	}

}
