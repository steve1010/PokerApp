package entities.hand;

public final class Flush extends PokerHand {

	private final int fst, snd, trd, frd, fft;
	private final Integer[] cards;

	public Flush(Integer[] cards) {
		super(5);
		if (cards.length != 5) {
			throw new IllegalArgumentException("Must be 5 cards.");
		}
		this.cards = cards;
		this.fst = cards[0];
		this.snd = cards[1];
		this.trd = cards[2];
		this.frd = cards[3];
		this.fft = cards[4];
	}

	public int getFst() {
		return fst;
	}

	public int getSnd() {
		return snd;
	}

	public int getTrd() {
		return trd;
	}

	public int getFrd() {
		return frd;
	}

	public int getFft() {
		return fft;
	}

	public Integer[] getCards() {
		return cards;
	}

	@Override
	public String toString() {
		return super.toString() + ", " + valueToName(fft) + " high";
	}

}
