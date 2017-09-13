package ui.gameplay;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Observable;

import entities.gameplay.Board;
import entities.gameplay.Card;
import entities.gameplay.PlayerHand;
import ui.Controller;

public final class GameplayController implements Controller {

	private final GameplayModel model;

	public GameplayController(InetSocketAddress serverAdress) {
		this.model = new GameplayModel(serverAdress);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
	}

	public void userCalls() {
	}

	public void userChecks() {
	}

	public void userFolds() {
	}

	public void userRaises(double value) {
	}

	public List<Card> adminDeals() {
		model.newRound();
		return model.deal();
	}

	public String[] passCardsForEvaluation(Board board, List<PlayerHand> hands) {
		return model.evaluate(board, hands);
	}
}