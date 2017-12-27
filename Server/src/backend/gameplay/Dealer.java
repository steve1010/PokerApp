package backend.gameplay;

import java.util.LinkedList;
import java.util.List;

import entities.game.play.Board;
import entities.game.play.Card;
import entities.game.play.PlayerHand;
import entities.game.play.Card.Color;

public final class Dealer {

	private final LinkedList<Card> cardsList;
	private final Evaluator evaluator;

	public Dealer() {
		this.cardsList = new LinkedList<Card>();
		fill();
		this.evaluator = new Evaluator();
	}

	public Card newCard() {
		int random = (int) (Math.random() * (cardsList.size() - 1));
		Card tmp = cardsList.get(random);
		cardsList.remove(random);
		return tmp;
	}

	public synchronized void shuffle() {
		fill();
		for (int i = 0; i < 52; i++) {
			int rand = (int) (Math.random() * (51 - i));
			Card temp = cardsList.get(i);
			cardsList.add(i, cardsList.get(rand));
			cardsList.remove(i + 1);
			cardsList.add(rand, temp);
			cardsList.remove(rand + 1);
		}
	}

	private void fill() {
		cardsList.clear();
		for (Color color : Card.getColors()) {
			for (int value = 0; value < 13; value++) {
				cardsList.add(new Card(value, color));
			}
		}
	}

	public String[] evaluate(Board board, List<PlayerHand> hands) {
		shuffle();
		return evaluator.evaluate(board, hands);
	}
}