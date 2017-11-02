package app.lobby;

import app.ui.ClientInterna;
import javafx.collections.ObservableList;

public class LobbyClientInterna implements ClientInterna {

	public enum Type {
		UPDATE_GAMES, UPDATE_PLAYERS, GAME_START;
	}

	private final ObservableList<?> tableData;
	private final Type type;
	private int id;

	public LobbyClientInterna(Type type, ObservableList<?> tableData) {
		this.type = type;
		this.tableData = tableData;
	}

	public LobbyClientInterna(Type type, int id) {
		this.type = type;
		this.tableData = null;
		this.id = id;
	}

	public ObservableList<?> getTableData() {
		return tableData;
	}

	public Type getType() {
		return type;
	}

	public int getId() {
		return id;
	}
}
