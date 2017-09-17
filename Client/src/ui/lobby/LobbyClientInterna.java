package ui.lobby;

import javafx.collections.ObservableList;
import ui.ClientInterna;

public class LobbyClientInterna implements ClientInterna {

	private final ObservableList<?> tableData;
	private final int type;
	private int id;

	public LobbyClientInterna(int type, ObservableList<?> tableData) {
		validate(type);
		this.type = type;
		this.tableData = tableData;
	}

	public LobbyClientInterna(int type, int id) {
		validate(type);
		this.type = type;
		this.tableData = null;
		this.id = id;
	}

	public ObservableList<?> getTableData() {
		return tableData;
	}

	public int getType() {
		return type;
	}

	private void validate(int type) {
		if (type < 0 || type > 3) {
			throw new IllegalArgumentException();
		}
	}

	public int getId() {
		return id;
	}
}
