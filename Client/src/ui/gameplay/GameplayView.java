package ui.gameplay;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import entities.Game;
import entities.SafePlayer;
import entities.gameplay.Card;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ui.lobby.LobbyView;
import ui.login.LoginView;

public class GameplayView implements Observer {

	private GameplayCtrl controller;

	@FXML
	private Parent playersPane;

	@FXML
	PlayersView playersPaneController;

	@FXML
	private Pane pActionPane;

	@FXML
	private Button dealBtn, evalBtn, checkBtn, callBtn, raiseBtn, foldBtn, returnToLobbyBtn;

	@FXML
	private Slider raiseSlider;
	private Stage currentStage;

	@Override
	public void update(Observable obs, Object o) {
		if (o instanceof GameplayClientInterna) {
			GameplayClientInterna clientInterna = (GameplayClientInterna) o;
			switch (clientInterna.getType()) {
			case POSITION:
				break;// ignore
			case YOUR_TURN:
				pActionPane.setVisible(true);
				break;
			case ACTION:
				break;
			case ROUND_END:
				break;
			default:
				throw new IllegalArgumentException("unknown msg.");
			}
		}
	}

	@FXML
	void dealBtnClicked(ActionEvent event) {
		List<Card> cards = controller.adminDeals();
		playersPaneController.setCard(0, true, cards.get(0));
		playersPaneController.setCard(0, false, cards.get(1));
		playersPaneController.setCard(1, true, cards.get(2));
		playersPaneController.setCard(1, false, cards.get(3));
		playersPaneController.setCard(2, true, cards.get(4));
		playersPaneController.setCard(2, false, cards.get(5));
		playersPaneController.setCard(3, true, cards.get(6));
		playersPaneController.setCard(3, false, cards.get(7));
		playersPaneController.setCard(4, true, cards.get(8));
		playersPaneController.setCard(4, false, cards.get(9));
		playersPaneController.setCard(5, true, cards.get(10));
		playersPaneController.setCard(5, false, cards.get(11));
		playersPaneController.setCard(6, true, cards.get(12));
		playersPaneController.setCard(6, false, cards.get(13));
		playersPaneController.setCard(7, true, cards.get(14));
		playersPaneController.setCard(7, false, cards.get(15));
		playersPaneController.setCard(8, true, cards.get(16));
		playersPaneController.setCard(8, false, cards.get(17));
		playersPaneController.setCard(9, true, cards.get(18));
		playersPaneController.setCard(9, false, cards.get(19));
		playersPaneController.setBoardCard(0, cards.get(20));
		playersPaneController.setBoardCard(1, cards.get(21));
		playersPaneController.setBoardCard(2, cards.get(22));
		playersPaneController.setBoardCard(3, cards.get(23));
		playersPaneController.setBoardCard(4, cards.get(24));

		event.consume();
	}

	@FXML
	void evalBtnClicked(ActionEvent event) {
		String[] hands = controller.passCardsForEvaluation(playersPaneController.getBoard(),
				playersPaneController.getAllCards());
		for (int i = 0; i < hands.length; i++) {
			playersPaneController.setPlayerName(i, hands[i]);
		}
	}

	@FXML
	void callBtnClicked(ActionEvent event) {
		controller.userCalls();
		pActionPane.setVisible(false);
		event.consume();
	}

	@FXML
	void checkBtnClicked(ActionEvent event) {
		controller.userChecks();
		pActionPane.setVisible(false);
		event.consume();
	}

	@FXML
	void foldBtnClicked(ActionEvent event) {
		controller.userFolds();
		pActionPane.setVisible(false);
		event.consume();
	}

	@FXML
	void raiseBtnClicked(ActionEvent event) {
		if (raiseSlider.getValue() != 0.0) {
			controller.userRaises(raiseSlider.getValue());
			pActionPane.setVisible(false);
		} else {
			new Alert(AlertType.WARNING, "Bitte einen gültigen Raise-Betrag eingeben.").show();
		}
		event.consume();
	}

	@FXML
	void lobbyBtnClicked(ActionEvent event) {
		currentStage.close();
		event.consume();
	}

	public void setData(InetSocketAddress serverAdress, SafePlayer loggedInPlayer, String pw, Stage primaryStage,
			Stage runningGameStage, LoginView loginView, LobbyView lobbyView, Game idGame) {
		this.controller = new GameplayCtrl(serverAdress, loggedInPlayer, pw, primaryStage, playersPaneController,
				loginView, lobbyView, idGame);
		controller.addObserver(this);
		this.currentStage = runningGameStage;
		playersPaneController.setPlayersAmount(String.valueOf(idGame.getMaxPlayers()));
		switch (idGame.getMaxPlayers()) {
		case 1:
			for (int i = 1; i < 10; i++) {
				playersPaneController.disablePlayerSlot(i);
			}
			break;
		case 2:
			playersPaneController.disablePlayerSlot(1);
			playersPaneController.disablePlayerSlot(2);
			playersPaneController.disablePlayerSlot(3);
			playersPaneController.disablePlayerSlot(4);
			playersPaneController.disablePlayerSlot(6);
			playersPaneController.disablePlayerSlot(7);
			playersPaneController.disablePlayerSlot(8);
			playersPaneController.disablePlayerSlot(9);
			break;
		case 3:
			playersPaneController.disablePlayerSlot(0);
			playersPaneController.disablePlayerSlot(2);
			playersPaneController.disablePlayerSlot(3);
			playersPaneController.disablePlayerSlot(4);
			playersPaneController.disablePlayerSlot(6);
			playersPaneController.disablePlayerSlot(7);
			playersPaneController.disablePlayerSlot(8);
			break;
		case 4:
			playersPaneController.disablePlayerSlot(0);
			playersPaneController.disablePlayerSlot(2);
			playersPaneController.disablePlayerSlot(3);
			playersPaneController.disablePlayerSlot(5);
			playersPaneController.disablePlayerSlot(7);
			playersPaneController.disablePlayerSlot(8);
			break;
		case 5:
			playersPaneController.disablePlayerSlot(1);
			playersPaneController.disablePlayerSlot(3);
			playersPaneController.disablePlayerSlot(5);
			playersPaneController.disablePlayerSlot(7);
			playersPaneController.disablePlayerSlot(9);
			break;
		case 6:
			playersPaneController.disablePlayerSlot(3);
			playersPaneController.disablePlayerSlot(5);
			playersPaneController.disablePlayerSlot(7);
			playersPaneController.disablePlayerSlot(9);
			break;
		case 7:
			playersPaneController.disablePlayerSlot(2);
			playersPaneController.disablePlayerSlot(7);
			playersPaneController.disablePlayerSlot(8);
			break;
		case 8:
			playersPaneController.disablePlayerSlot(8);
			playersPaneController.disablePlayerSlot(9);
			break;
		case 9:
			playersPaneController.disablePlayerSlot(9);
			break;
		default:
			break;
		}
	}
}