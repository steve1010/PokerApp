package ui.lobby;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;

import entities.Game;
import entities.Player;
import entities.SafePlayer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ui.Controller;
import ui.login.LoginView;

public class LobbyView implements Controller, Initializable, Observer {

	private LobbyCtrl controller;

	private PropertyValueFactory<Game, Integer> idColumnFactory, startChipsColumnFactory, playersMaxColumnFactory,
			paidColumnFactory, signedUpColumnFactory;
	private PropertyValueFactory<Game, Double> buyInColumnFactory;
	private PropertyValueFactory<Game, String> nameColumnFactory;

	@FXML
	private TableView<Game> gamesTable;
	@FXML
	private TableColumn<Game, Integer> iDColumn, startChipsColumn, paidColumn, playersMaxColumn, signedUpColumn;

	@FXML
	private TableColumn<Game, String> nameColumn;

	@FXML
	private TableColumn<Game, Double> buyInColumn;

	@FXML
	private TableView<SafePlayer> allPlayersTable, inGamePlayersTable;

	@FXML
	private TableColumn<SafePlayer, String> allPlayersColumn, inGameColumn;

	@FXML
	private HBox signedIpPlayersHbox;

	@FXML
	private TextField nameTxtField, buyInTxtField, startChipsTxtField, playersMaxTxtField, paidTxtField;

	@FXML
	private Button createBtn, enrollBtn, switchViewBtn, logoutBtn;

	@FXML
	private Label playerNameLbl, bankrollLbl;

	@FXML
	private VBox createVBox, signedInVbox;

	private Game currentlySelectedItem;

	private PropertyValueFactory<SafePlayer, String> allPlayersColumnFactory, inGameColumnFactory;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		idColumnFactory = new PropertyValueFactory<>("id");
		iDColumn.setCellValueFactory(idColumnFactory);
		nameColumnFactory = new PropertyValueFactory<>("name");
		nameColumn.setCellValueFactory(nameColumnFactory);
		buyInColumnFactory = new PropertyValueFactory<>("buyIn");
		buyInColumn.setCellValueFactory(buyInColumnFactory);
		startChipsColumnFactory = new PropertyValueFactory<>("startChips");
		startChipsColumn.setCellValueFactory(startChipsColumnFactory);
		playersMaxColumnFactory = new PropertyValueFactory<>("maxPlayers");
		playersMaxColumn.setCellValueFactory(playersMaxColumnFactory);
		paidColumnFactory = new PropertyValueFactory<>("paid");
		paidColumn.setCellValueFactory(paidColumnFactory);
		signedUpColumnFactory = new PropertyValueFactory<Game, Integer>("signedUp");
		signedUpColumn.setCellValueFactory(signedUpColumnFactory);

		allPlayersColumnFactory = new PropertyValueFactory<SafePlayer, String>("name");
		allPlayersColumn.setCellValueFactory(allPlayersColumnFactory);

		inGameColumnFactory = new PropertyValueFactory<SafePlayer, String>("name");
		inGameColumn.setCellValueFactory(inGameColumnFactory);

		gamesTable.setRowFactory(tv -> new TableRow<Game>() {
			@Override
			public void updateItem(Game game, boolean empty) {
				super.updateItem(game, empty);
				if (game == null) {
					setStyle("");
				} else if (game.getMaxPlayers().intValue() == game.getSignedUp().intValue()) {
					setStyle("-fx-background-color: tomato;");
				} else {
					setStyle("-fx-background-color: palegreen;");
				}
			}
		});
		gamesTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		gamesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldGame, newGame) -> {
			if (newGame != null) {
				Platform.runLater(
						() -> inGamePlayersTable.setItems(FXCollections.observableArrayList(newGame.getPlayersList())));
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable observable, Object o) {

		if (o instanceof LobbyClientInterna) {
			Platform.runLater(() -> {
				int type = ((LobbyClientInterna) o).getType();
				if (type == 0) {
					gamesTable.setItems((ObservableList<Game>) ((LobbyClientInterna) o).getTableData());
					gamesTable.refresh();
				}
				if (type == 1) {
					allPlayersTable.setItems((ObservableList<SafePlayer>) ((LobbyClientInterna) o).getTableData());
					allPlayersTable.refresh();
				}
			});
		}
	}

	@FXML
	void createBtnClicked(ActionEvent event) {
		controller.requestCreation(nameTxtField.getText(), buyInTxtField.getText(), startChipsTxtField.getText(),
				playersMaxTxtField.getText(), paidTxtField.getText());
		resetFields();
	}

	@FXML
	void enroll(ActionEvent event) {
		this.currentlySelectedItem = gamesTable.getSelectionModel().getSelectedItem();
		controller.enrollRequest(currentlySelectedItem);
	}

	@FXML
	void logoutClicked(ActionEvent event) {
		Alert confirm = new Alert(AlertType.CONFIRMATION, "Really log out and return to login?");
		Optional<ButtonType> result = confirm.showAndWait();
		if (result.isPresent() && result.get().equals(ButtonType.OK)) {
			controller.triggerLogout();
		}
		event.consume();
	}

	@FXML
	void switchView(ActionEvent event) {
		if (signedInVbox.isVisible()) {
			signedInVbox.setVisible(false);
			createVBox.setVisible(true);
		} else {
			signedInVbox.setVisible(true);
			createVBox.setVisible(false);
		}
	}

	public void wrongInputAlert() {
		new Alert(AlertType.ERROR);
	}

	public void setData(InetSocketAddress serverAdress, Player loggedInPlayer, String pw, Stage primaryStage,
			LoginView loginView) {
		this.controller = new LobbyCtrl(this, serverAdress, loggedInPlayer, pw, primaryStage, loginView);
		this.playerNameLbl.setText(loggedInPlayer.getName());
		this.bankrollLbl.setText(String.valueOf(loggedInPlayer.getBankRoll()));
		if (loggedInPlayer.getName().equals("Admin")) {
			this.createVBox.setVisible(true);
			signedInVbox.setVisible(false);
			switchViewBtn.setVisible(true);
		}
	}

	public void showEnrollAlert(String enrollPlayerIn) {
		if (enrollPlayerIn.startsWith("Y")) {
			new Alert(AlertType.WARNING, enrollPlayerIn).showAndWait();
		} else {
			new Alert(AlertType.INFORMATION, enrollPlayerIn).showAndWait();
			if (currentlySelectedItem != null) {
				double newValue = Double.parseDouble(bankrollLbl.getText()) - currentlySelectedItem.getBuyIn();
				bankrollLbl.setText(String.valueOf(newValue));
			}
			/**
			 * TODO: implement trigger to model here. Game should start on clicking ok.
			 */
		}
	}

	private void resetFields() {
		nameTxtField.clear();
		buyInTxtField.clear();
		startChipsTxtField.clear();
		playersMaxTxtField.clear();
		paidTxtField.clear();
	}

}