package entities.hand;

public final class FullHouse extends PokerHand {

	private final ThreeOfAKind threeOfAKind;
	private final Pair pair;

	public FullHouse(ThreeOfAKind threeOfAKind, Pair pair) {
		super(6);
		this.threeOfAKind = threeOfAKind;
		this.pair = pair;
	}

	public ThreeOfAKind getThreeOfAKind() {
		return threeOfAKind;
	}

	public Pair getPair() {
		return pair;
	}

	@Override
	public String toString() {
		return super.toString() + ", " + valueToName(threeOfAKind.getValue()) + "s & "
				+ valueToName(pair.getValue() + 's');
	}

}
