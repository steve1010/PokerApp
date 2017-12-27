package entities.game.hand;

public final class HighCard extends PokerHand {

	private final int value0;
	private int value1, value2, value3, value4;

	public HighCard(int value) {
		super(0);
		this.value0 = value;
	}

	public HighCard(int... values) {
		super(0);
		if (values.length != 5) {
			throw new IllegalArgumentException("Invalid length.");
		}
		this.value0 = values[0];
		this.value1 = values[1];
		this.value2 = values[2];
		this.value3 = values[3];
		this.value4 = values[4];
	}

	public int getValue0() {
		return value0;
	}

	public int getValue1() {
		return value1;
	}

	public int getValue2() {
		return value2;
	}

	public int getValue3() {
		return value3;
	}

	public int getValue4() {
		return value4;
	}

	@Override
	public String toString() {
		return super.toString() + ' ' + valueToName(value0) + " high";
	}
}
