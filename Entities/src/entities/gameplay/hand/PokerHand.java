package entities.gameplay.hand;

public abstract class PokerHand {

	private final int val;

	public PokerHand(int val) {
		super();
		this.val = val;
	}

	public int getVal() {
		return val;
	}

	public String valueToName(int value) {
		switch (value) {
		case 12:
			return "Ace";
		case 11:
			return "King";
		case 10:
			return "Queen";
		case 9:
			return "Jack";
		case 8:
			return "10";
		case 7:
			return "9";
		case 6:
			return "8";
		case 5:
			return "7";
		case 4:
			return "6";
		case 3:
			return "5";
		case 2:
			return "4";
		case 1:
			return "3";
		case 0:
			return "2";
		}
		return null;
	}

	@Override
	public String toString() {
		switch (val) {
		case 9:
			return "Royal-Flush";
		case 8:
			return "Straight-Flush";
		case 7:
			return "Four of a kind";
		case 6:
			return "Full House";
		case 5:
			return "Flush";
		case 4:
			return "Straight";
		case 3:
			return "Three of a Kind";
		case 2:
			return "Two Pair";
		case 1:
			return "Pair";
		case 0:
			return "Highcard";
		default:
			break;
		}
		return null;
	}
}