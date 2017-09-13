package ui.gameplay;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import entities.gameplay.Board;
import entities.gameplay.Card;
import entities.gameplay.PlayerHand;
import entities.query.Evaluation;
import entities.query.GameQuery;
import entities.query.GameQuery.Option;
import ui.Model;

public class GameplayModel extends Model {

	public GameplayModel(InetSocketAddress serverAdress) {
		super(serverAdress);
	}

	public List<Card> deal() {
		List<Card> cards = new ArrayList<>(1);
		for (int i = 0; i < 25; i++) {
			sendObject(new GameQuery(Option.NEW_CARD));
			Object received = receiveObject();
			if (received instanceof Card) {
				cards.add((Card) received);
			}
		}
		return cards;

	}

	public void newRound() {
		sendObject(new GameQuery(Option.NEW_ROUND));
	}

	public String[] evaluate(Board board, List<PlayerHand> hands) {
		sendObject(new Evaluation(board, hands));
		Object receiced = receiveObject();
		if (receiced instanceof String[]) {
			return (String[]) receiced;
		}
		return null;
	}
}
