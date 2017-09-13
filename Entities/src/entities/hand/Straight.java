package entities.hand;

import entities.gameplay.Card;

public final class Straight extends PokerHand {

	private Card[] cards;

	public Straight(Card... cards) {
		super(4);
		this.cards = cards;
	}

	public Card[] getCards() {
		return cards;
	}

	public int getTop() {
		return cards[4].getValue();
	}

	public int getBtm() {
		return cards[0].getValue();
	}

	@Override
	public String toString() {
		return super.toString() + ", " + valueToName(getBtm()) + " to " + valueToName(getTop());
	}
}
