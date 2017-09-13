package entities.hand;

import entities.gameplay.Card.Color;

public final class RoyalFlush extends PokerHand {

	private final Color color;

	public RoyalFlush(Color color) {
		super(9);
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public String toString() {
		return super.toString() + ", " + color.name().toLowerCase();
	}
}
