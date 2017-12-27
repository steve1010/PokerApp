package entities.query.game;

import java.net.InetSocketAddress;
import java.util.List;

import entities.game.play.Board;
import entities.game.play.PlayerHand;
import entities.query.Query;

public class EvaluationQuery extends Query {

	private static final long serialVersionUID = -863800550222648506L;

	private final Board board;
	private final List<PlayerHand> playerHands;

	public EvaluationQuery(InetSocketAddress srcAddress, InetSocketAddress destAddress, Board board,
			List<PlayerHand> hands) {
		super(srcAddress, destAddress);
		this.board = board;
		this.playerHands = hands;
	}

	public Board getBoard() {
		return board;
	}

	public List<PlayerHand> getHands() {
		return playerHands;
	}

	@Override
	public String toString() {
		return "Evaluation [board=" + board + ", playerHands=" + playerHands + "]";
	}

}
