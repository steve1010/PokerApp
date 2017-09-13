package ui.lobby;

import java.net.InetSocketAddress;
import java.util.Observable;

import entities.Player;
import entities.lobby.IDGame;
import ui.Controller;
import ui.Model;

public class LobbyCtrl extends Observable implements Controller {

	private final Model model;
	private final LobbyView view;

	public LobbyCtrl(LobbyView lobbyView, InetSocketAddress serverAdress, Player player) {
		model = new LobbyModel(serverAdress, player);
		view = lobbyView;
		this.addObserver(view);
		model.addObserver(this);
		((LobbyModel) model).init();
	}

	@Override
	public void update(Observable observable, Object o) {
		this.setChanged();
		this.notifyObservers(o);
	}

	public void requestCreation(String name, String buyIn, String startChips, String maxPlayers, String paid) {
		double buy_in = 0;
		int start_chips = 0;
		int max_players = 0;
		int p4id = 0;
		try {
			buy_in = Double.parseDouble(buyIn);
			start_chips = Integer.parseInt(startChips);
			max_players = Integer.parseInt(maxPlayers);
			p4id = Integer.parseInt(paid);
		} catch (NumberFormatException e) {
			view.wrongInputAlert();
			e.printStackTrace();
		}
		((LobbyModel) model).createGame(name, buy_in, start_chips, max_players, p4id);
	}

	public void enrollRequest(IDGame selectedGame) {
		view.showEnrollAlert(((LobbyModel) model).enrollPlayerIn(selectedGame));
	}
}