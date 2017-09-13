package entities.gameplay;

import java.io.Serializable;

public final class Board implements Serializable {

	private static final long serialVersionUID = -4982416325547448247L;
	private final Card card0, card1, card2, card3, card4;

	public Board(Card... cards) {
		if (cards.length != 5) {
			throw new IllegalArgumentException("Invalid number of cards.");
		}
		this.card0 = cards[0];
		this.card1 = cards[1];
		this.card2 = cards[2];
		this.card3 = cards[3];
		this.card4 = cards[4];
	}

	public Card getCard0() {
		return card0;
	}

	public Card getCard1() {
		return card1;
	}

	public Card getCard2() {
		return card2;
	}

	public Card getCard3() {
		return card3;
	}

	public Card getCard4() {
		return card4;
	}
}
