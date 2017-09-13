package entities.query;

public class PlayersActionQuery extends Query {

	private static final long serialVersionUID = 342128053777549408L;
	private final Option option;

	public enum Option {
		CHECK, FOLD, CALL, RAISE;
	}

	public PlayersActionQuery(Option option) {
		this.option = option;
	}

	public Option getOption() {
		return option;
	}
}
