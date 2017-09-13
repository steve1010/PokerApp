package entities.gameplay;

import java.io.Serializable;

public final class Card implements Serializable {

	public enum Color {
		HEART, CLUBS, SPADES, DIAMONDS;
	}

	private static final long serialVersionUID = 906951800283865780L;

	private final int value;
	private final Color color;

	public Card(int value, Color color) {
		if (value > 12) {
			throw new IllegalArgumentException("No such card.");
		}
		this.value = value;
		this.color = color;
	}

	public int getValue() {
		return value;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public String toString() {
		return "Card [value=" + value + ", color=" + color.toString() + "]";
	}

	public static Color[] getColors() {
		Color[] colors = { Color.HEART, Color.CLUBS, Color.SPADES, Color.DIAMONDS };
		return colors;
	}
}
