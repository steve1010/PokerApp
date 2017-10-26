package entities.query;

public class PlayersActionQuery extends Query {

	private static final long serialVersionUID = 342128053777549408L;
	private final int id;
	private final Option option;
	private final double amount;

	public enum Option {
		CHECK, FOLD, CALL, RAISE, READY;
	}

	public PlayersActionQuery(int playerID, Option option) {
		this.id = playerID;
		this.option = option;
		this.amount = 0;
	}

	public PlayersActionQuery(int playerID, double amount) {
		this.id = playerID;
		this.option = Option.RAISE;
		this.amount = amount;
	}

	public Option getOption() {
		return option;
	}

	public double getAmount() {
		return amount;
	}

	public int getId() {
		return id;
	}
}
