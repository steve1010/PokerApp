package ui.lobby;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Observable;

import entities.SafePlayer;
import entities.lobby.IDGame;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.Controller;
import ui.Model;
import ui.gameplay.GameplayView;
import ui.login.LoginView;

public class LobbyCtrl extends Observable implements Controller {

	private final Model model;
	private final LobbyView lobbyView;
	private final LoginView loginView;
	private final Stage primaryStage;

	public LobbyCtrl(LobbyView lobbyView, InetSocketAddress serverAdress, SafePlayer loggedInPlayer, String pw,
			Stage primaryStage, LoginView loginView) {
		model = new LobbyModel(serverAdress, loggedInPlayer, pw);
		this.lobbyView = lobbyView;
		this.loginView = loginView;
		this.primaryStage = primaryStage;
		this.addObserver(lobbyView);
		model.addObserver(this);
		((LobbyModel) model).init();
	}

	@Override
	public void update(Observable observable, Object o) {
		if (o instanceof ClientInterna && (((ClientInterna) o).getType() == 2)) {
			// trigger for game starting ..
			openGameplayView();
		}
		this.setChanged();
		this.notifyObservers(o);
	}

	private void openGameplayView() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../gameplay/Gameplay.fxml"));
			Parent p = loader.load();
			Platform.runLater(() -> {
				Stage runningGameStage = new Stage();
				runningGameStage.setScene(new Scene(p));
				runningGameStage.sizeToScene();
				runningGameStage.setX(200);
				runningGameStage.setY(1);
				runningGameStage.setTitle("Game started. Good Luck!");
				System.out.println("LobbyCtrl.openGameplayView()");
				System.err.println("Game started. Good Luck!");
				runningGameStage.show();
			});
			((GameplayView) loader.getController()).setData(model.getServerAdress(), model.getLoggedInPlayer(),
					model.getPw(), primaryStage, loginView, lobbyView);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			lobbyView.wrongInputAlert();
			e.printStackTrace();
		}
		((LobbyModel) model).createGame(name, buy_in, start_chips, max_players, p4id);
	}

	public void enrollRequest(IDGame selectedGame) {
		lobbyView.showEnrollAlert(((LobbyModel) model).enrollPlayerIn(selectedGame));
	}

	public void triggerLogout() {
		((LobbyModel) model).logoutUser();
		primaryStage.close();
		primaryStage.setScene(new Scene(loginView.getLoginPane()));
		primaryStage.sizeToScene();
		loginView.resetValues();
		primaryStage.show();
	}
}