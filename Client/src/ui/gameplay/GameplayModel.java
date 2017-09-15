package ui.gameplay;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import entities.SafePlayer;
import entities.gameplay.Board;
import entities.gameplay.Card;
import entities.gameplay.PlayerHand;
import entities.lobby.IDGame;
import entities.query.Evaluation;
import entities.query.GameQuery;
import entities.query.GameQuery.Option;
import ui.Model;

public class GameplayModel extends Model {

	private final SafePlayer loggedInPlayer;
	private final String pw;
	private final IDGame game;

	public GameplayModel(InetSocketAddress serverAdress, SafePlayer loggedInPlayer, String pw, IDGame game) {
		super(serverAdress);
		this.loggedInPlayer = loggedInPlayer;
		this.pw = pw;
		this.game = game;
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

	@Override
	public SafePlayer getLoggedInPlayer() {
		return this.loggedInPlayer;
	}

	@Override
	public String getPw() {
		return this.pw;
	}

	public IDGame getGame() {
		return game;
	}

	public void logout() {
		
	}
}