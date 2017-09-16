package entities.query;

public class PlayersActionQuery extends Query {

	private static final long serialVersionUID = 342128053777549408L;
	private final Option option;
	private final int amount;

	public enum Option {
		CHECK, FOLD, CALL, RAISE;
	}

	public PlayersActionQuery(Option option ,int amount) {
		this.option = option;
		this.amount=amount;
	}

	public Option getOption() {
		return option;
	}
}
