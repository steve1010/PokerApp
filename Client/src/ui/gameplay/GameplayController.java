package ui.gameplay;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Observable;

import entities.SafePlayer;
import entities.gameplay.Board;
import entities.gameplay.Card;
import entities.gameplay.PlayerHand;
import entities.lobby.IDGame;
import javafx.stage.Stage;
import ui.Controller;
import ui.lobby.LobbyView;
import ui.login.LoginView;

public final class GameplayController implements Controller {

	private final GameplayModel model;
	private final Stage primaryStage;
	private final LoginView loginView;
	private final LobbyView lobbyView;

	public GameplayController(InetSocketAddress serverAdress, SafePlayer loggedInPlayer, String pw, Stage primaryStage,
			LoginView loginView, LobbyView lobbyView, IDGame idGame) {
		this.model = new GameplayModel(serverAdress, loggedInPlayer, pw, idGame);
		this.primaryStage = primaryStage;
		this.loginView = loginView;
		this.lobbyView = lobbyView;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
	}

	public void userCalls() {
	}

	public void userChecks() {
	}

	public void userFolds() {
	}

	public void userRaises(double value) {
	}

	public List<Card> adminDeals() {
		model.newRound();
		return model.deal();
	}

	public String[] passCardsForEvaluation(Board board, List<PlayerHand> hands) {
		return model.evaluate(board, hands);
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public LoginView getLoginView() {
		return loginView;
	}

	public LobbyView getLobbyView() {
		return lobbyView;
	}

	public void triggerLogout() {
model.logout();		
	}
}