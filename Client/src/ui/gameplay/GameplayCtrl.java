package ui.gameplay;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Observable;

import entities.Game;
import entities.SafePlayer;
import entities.gameplay.Board;
import entities.gameplay.Card;
import entities.gameplay.PlayerHand;
import javafx.stage.Stage;
import ui.Controller;
import ui.lobby.LobbyView;
import ui.login.LoginView;

public final class GameplayCtrl extends Observable implements Controller {

	private final GameplayModel model;
	private final Stage primaryStage;
	private final LoginView loginView;
	private final LobbyView lobbyView;

	public GameplayCtrl(InetSocketAddress serverAdress, SafePlayer loggedInPlayer, String pw, Stage primaryStage,
			PlayersView playersView, LoginView loginView, LobbyView lobbyView, Game idGame) {
		this.model = new GameplayModel(serverAdress, loggedInPlayer, pw, idGame);
		this.primaryStage = primaryStage;
		this.loginView = loginView;
		this.addObserver(playersView);
		model.addObserver(this);
		this.lobbyView = lobbyView;
	}

	@Override
	public void update(Observable arg0, Object o) {
		triggerNotification(o);
	}

	private void triggerNotification(Object o) {
		setChanged();
		notifyObservers(o);
	}

	public void userCalls() {
		model.userCalled();
	}

	public void userChecks() {
		model.userChecked();
	}

	public void userFolds() {
		model.userFolded();
	}

	public void userRaises(double value) {
		model.userRaised(value);
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

}