package app.lobby;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Observable;
import java.util.stream.Collectors;

import app.gameplay.GameplayView;
import app.lobby.LobbyClientInterna.Type;
import app.login.LoginView;
import app.ui.Controller;
import app.ui.ClientModel;
import entities.game.Game;
import entities.game.Player;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LobbyCtrl extends Observable implements Controller {

	private final ClientModel model;
	private final LobbyView lobbyView;
	private final LoginView loginView;
	private final Stage primaryStage;

	public LobbyCtrl(LobbyView lobbyView, InetSocketAddress serverAdress, Player loggedInPlayer, String pw,
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
		if (o instanceof LobbyClientInterna && (((LobbyClientInterna) o).getType().equals(Type.GAME_START))) {
			openGameplayView(((LobbyClientInterna) o).getId());
		}
		this.setChanged();
		this.notifyObservers(o);
	}

	private void openGameplayView(int i) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../gameplay/Gameplay.fxml"));
			Parent p = loader.load();

			Platform.runLater(() -> {
				Stage runningGameStage = new Stage();
				runningGameStage.setScene(new Scene(p));
				runningGameStage.sizeToScene();
				runningGameStage.setX(200);
				runningGameStage.setY(1);
				runningGameStage.setTitle("Good Luck!");
				runningGameStage.show();
				runningGameStage.setOnCloseRequest((e) -> {// TODO: remove from server on disconnecting/leaving page
				});
				((GameplayView) loader.getController()).setData(model.getServerAdress(), model.getLoggedInPlayer(),
						model.getPw(), primaryStage, runningGameStage, loginView, lobbyView,
						((LobbyModel) model).getGamesTableData().stream().filter(game -> game.getId() == i)
								.collect(Collectors.toList()).get(0));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void requestCreation(String name, String buyIn, String startChips, String maxPlayers, String paid) {
		Game game = check(name, buyIn, startChips, maxPlayers, paid);
		if (game.getSignedUp() == -3) {
			return;
		} else {
			((LobbyModel) model).createGame(name, game.getBuyIn(), game.getStartChips(), game.getMaxPlayers(),
					game.getPaid());
		}
	}

	/**
	 * Checks the input on its validity.<br>
	 * <br>
	 * <i> (According to internal semantics)
	 * 
	 * @return Game whose signedUpPlayers-attribute is set to
	 *         <ul>
	 *         <li>-2, iff a valid game was created
	 *         <li>-3, iff something was wrong
	 *         </ul>
	 */
	private Game check(String name, String buyIn, String startChips, String maxPlayers, String paid) {
		if (Objects.isNull(name) || Objects.isNull(buyIn) || Objects.isNull(startChips) || Objects.isNull(maxPlayers)
				|| Objects.isNull(paid)) {
			lobbyView.wrongInputAlert();
		}
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
		if (buy_in <= 0 || buy_in > 10000 || start_chips < 0 || start_chips > 10000 || max_players > 10
				|| max_players <= 0 || p4id <= 0 || p4id > 3) {
			lobbyView.wrongInputAlert();
		}
		return new Game(name, ((LobbyModel) model).getGamesTableData().size(), buy_in, start_chips, max_players, p4id,
				0);
	}

	public void enrollRequest(Game selectedGame) {
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