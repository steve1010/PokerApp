package entities.gameplay;

import java.io.Serializable;

public final class PlayerHand implements Serializable {

	private static final long serialVersionUID = 1573944013487175481L;
	private final Card card1, card2;

	public PlayerHand(Card card1, Card card2) {
		this.card1 = card1;
		this.card2 = card2;
	}

	public Card getCard1() {
		return card1;
	}

	public Card getCard2() {
		return card2;
	}

	@Override
	public String toString() {
		return "PlayerHand [card1=" + card1 + ", card2=" + card2 + "]";
	}

}
