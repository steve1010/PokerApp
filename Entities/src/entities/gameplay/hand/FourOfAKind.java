package entities.gameplay.hand;

public final class FourOfAKind extends PokerHand {

	private final int value;
	private final HighCard highCard;

	public FourOfAKind(int value, HighCard highCard) {
		super(7);
		this.value = value;
		this.highCard = highCard;
	}

	public int getValue() {
		return value;
	}

	public HighCard getHighCard() {
		return highCard;
	}

	@Override
	public String toString() {
		return super.toString() + " of " + valueToName(value) + ", " + valueToName(highCard.getValue0()) + " high";
	}
}
