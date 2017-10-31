package app.gameplay;

import app.ui.ClientInterna;

public class GameplayClientInterna implements ClientInterna {
	public enum Type {
		POSITION, YOUR_TURN, ACTION, ROUND_END;// others following..
	}

	public enum ActionType {
		CALL, CHECK, RAISE, FOLD;
	}

	public enum Position {
		BUTTON, SMALL_BLIND, BIG_BLIND, BUTTON_BIG_BLIND, CUTOFF, UTG, CUTOFF_UTG, EARLY_POSITION_1, EARLY_POSITION_2, MIDDLE_POSITION_1, MIDDLE_POSITION_2, LATE_POSITION, FALSE;
	}

	private final Type type;
	private Position position;
	private ActionType actionType;
	private double raiseValue;
	private int winnerID;

	public GameplayClientInterna(Type position) {
		this.type = position;
	}

	public GameplayClientInterna(Type type, Position pos) {
		this.type = type;
		this.position = pos;
	}

	public GameplayClientInterna(Type type, ActionType action) {
		this.type = type;
		this.actionType = action;
	}

	public GameplayClientInterna(Type position, int yourPosition, int playersAmount) {
		this.type = position;
		switch (playersAmount) {
		case 1:
			this.position = Position.BUTTON_BIG_BLIND;
			break;
		case 2:
			if (yourPosition < playersAmount) {
				this.position = Position.BUTTON_BIG_BLIND;
				break;
			} else {
				this.position = Position.SMALL_BLIND;
				break;
			}
		case 3:
			switch (yourPosition) {
			case 0:
				this.position = Position.BUTTON;
				break;
			case 1:
				this.position = Position.SMALL_BLIND;
				break;
			case 2:
				this.position = Position.BIG_BLIND;
				break;
			default:
				throw new IllegalArgumentException(
						"IndexOutOfBounds: yourPosition: " + yourPosition + " maxPlayers: " + playersAmount);
			}
			break;
		case 4:
			switch (yourPosition) {
			case 0:
				this.position = Position.BUTTON;
				break;
			case 1:
				this.position = Position.SMALL_BLIND;
				break;
			case 2:
				this.position = Position.BIG_BLIND;
				break;
			case 3:
				this.position = Position.CUTOFF_UTG;
				break;
			default:
				throw new IllegalArgumentException(
						"IndexOutOfBounds: yourPosition: " + yourPosition + " maxPlayers: " + playersAmount);
			}
			break;
		case 5:
			switch (yourPosition) {
			case 0:
				this.position = Position.BUTTON;
				break;
			case 1:
				this.position = Position.SMALL_BLIND;
				break;
			case 2:
				this.position = Position.BIG_BLIND;
				break;
			case 3:
				this.position = Position.UTG;
				break;
			case 4:
				this.position = Position.CUTOFF;
				break;
			default:
				throw new IllegalArgumentException(
						"IndexOutOfBounds: yourPosition: " + yourPosition + " maxPlayers: " + playersAmount);
			}
			break;
		case 6:
			switch (yourPosition) {
			case 0:
				this.position = Position.BUTTON;
				break;
			case 1:
				this.position = Position.SMALL_BLIND;
				break;
			case 2:
				this.position = Position.BIG_BLIND;
				break;
			case 3:
				this.position = Position.UTG;
				break;
			case 4:
				this.position = Position.LATE_POSITION;
				break;
			case 5:
				this.position = Position.CUTOFF;
				break;
			default:
				throw new IllegalArgumentException(
						"IndexOutOfBounds: yourPosition: " + yourPosition + " maxPlayers: " + playersAmount);
			}
			break;
		case 7:
			switch (yourPosition) {
			case 0:
				this.position = Position.BUTTON;
				break;
			case 1:
				this.position = Position.SMALL_BLIND;
				break;
			case 2:
				this.position = Position.BIG_BLIND;
				break;
			case 3:
				this.position = Position.UTG;
				break;
			case 4:
				this.position = Position.EARLY_POSITION_1;
				break;
			case 5:
				this.position = Position.MIDDLE_POSITION_1;
				break;
			case 6:
				this.position = Position.CUTOFF;
				break;
			default:
				throw new IllegalArgumentException(
						"IndexOutOfBounds: yourPosition: " + yourPosition + " maxPlayers: " + playersAmount);
			}
			break;
		case 8:
			switch (yourPosition) {
			case 0:
				this.position = Position.BUTTON;
				break;
			case 1:
				this.position = Position.SMALL_BLIND;
				break;
			case 2:
				this.position = Position.BIG_BLIND;
				break;
			case 3:
				this.position = Position.UTG;
				break;
			case 4:
				this.position = Position.EARLY_POSITION_1;
				break;
			case 5:
				this.position = Position.MIDDLE_POSITION_1;
				break;
			case 6:
				this.position = Position.LATE_POSITION;
				break;
			case 7:
				this.position = Position.CUTOFF;
			default:
				throw new IllegalArgumentException(
						"IndexOutOfBounds: yourPosition: " + yourPosition + " maxPlayers: " + playersAmount);
			}
			break;
		case 9:
			switch (yourPosition) {
			case 0:
				this.position = Position.BUTTON;
				break;
			case 1:
				this.position = Position.SMALL_BLIND;
				break;
			case 2:
				this.position = Position.BIG_BLIND;
				break;
			case 3:
				this.position = Position.UTG;
				break;
			case 4:
				this.position = Position.EARLY_POSITION_1;
				break;
			case 5:
				this.position = Position.MIDDLE_POSITION_1;
				break;
			case 6:
				this.position = Position.MIDDLE_POSITION_2;
				break;
			case 7:
				this.position = Position.LATE_POSITION;
				break;
			case 8:
				this.position = Position.CUTOFF;
				break;
			default:
				throw new IllegalArgumentException(
						"IndexOutOfBounds: yourPosition: " + yourPosition + " maxPlayers: " + playersAmount);
			}
			break;
		case 10:
			switch (yourPosition) {
			case 0:
				this.position = Position.BUTTON;
				break;
			case 1:
				this.position = Position.SMALL_BLIND;
				break;
			case 2:
				this.position = Position.BIG_BLIND;
				break;
			case 3:
				this.position = Position.UTG;
				break;
			case 4:
				this.position = Position.EARLY_POSITION_1;
				break;
			case 5:
				this.position = Position.EARLY_POSITION_2;
				break;
			case 6:
				this.position = Position.MIDDLE_POSITION_1;
				break;
			case 7:
				this.position = Position.MIDDLE_POSITION_2;
				break;
			case 8:
				this.position = Position.LATE_POSITION;
				break;
			case 9:
				this.position = Position.CUTOFF;
				break;
			default:
				throw new IllegalArgumentException(
						"IndexOutOfBounds: yourPosition: " + yourPosition + " maxPlayers: " + playersAmount);
			}
			break;
		}
	}

	public GameplayClientInterna(Type action, ActionType actionType, double raiseValue) {
		this(action, actionType);
		this.raiseValue = raiseValue;
	}

	public GameplayClientInterna(int winnerID) {
		this.type = Type.ROUND_END;
		this.winnerID = winnerID;
	}

	public Type getType() {
		return type;
	}

	public Position getPosition() {
		return position;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public double getRaiseValue() {
		return raiseValue;
	}

	public int getWinnerID() {
		return winnerID;
	}

}
