package entities.query;

import java.util.List;

import entities.gameplay.Board;
import entities.gameplay.PlayerHand;

public class Evaluation extends GameQuery {

	private static final long serialVersionUID = -863800550222648506L;

	private final Board board;
	private final List<PlayerHand> playerHands;

	public Evaluation(Board board, List<PlayerHand> hands) {
		super(GameQuery.Option.EVALUATION);
		this.board = board;
		this.playerHands = hands;
	}

	public Board getBoard() {
		return board;
	}

	public List<PlayerHand> getHands() {
		return playerHands;
	}

}
