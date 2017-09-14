package ui.lobby;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import entities.SafePlayer;
import entities.lobby.IDGame;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
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

	private PropertyValueFactory<IDGame, Integer> idColumnFactory, startChipsColumnFactory, playersMaxColumnFactory,
			paidColumnFactory, signedUpColumnFactory;
	private PropertyValueFactory<IDGame, Double> buyInColumnFactory;
	private PropertyValueFactory<IDGame, String> nameColumnFactory;

	@FXML
	private TableView<IDGame> gamesTable;
	@FXML
	private TableColumn<IDGame, Integer> iDColumn, startChipsColumn, paidColumn, playersMaxColumn, signedUpColumn;

	@FXML
	private TableColumn<IDGame, String> nameColumn;

	@FXML
	private TableColumn<IDGame, Double> buyInColumn;

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

	private IDGame currentlySelectedItem;

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
		signedUpColumnFactory = new PropertyValueFactory<IDGame, Integer>("signedUp");
		signedUpColumn.setCellValueFactory(signedUpColumnFactory);

		allPlayersColumnFactory = new PropertyValueFactory<SafePlayer, String>("name");
		allPlayersColumn.setCellValueFactory(allPlayersColumnFactory);

		inGameColumnFactory = new PropertyValueFactory<SafePlayer, String>("name");
		inGameColumn.setCellValueFactory(inGameColumnFactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable observable, Object o) {

		if (o instanceof ClientInterna) {
			Platform.runLater(() -> {
				int type = ((ClientInterna) o).getType();
				if (type == 0) {
					gamesTable.setItems((ObservableList<IDGame>) ((ClientInterna) o).getTableData());
					gamesTable.refresh();
				}
				if (type == 1) {
					allPlayersTable.setItems((ObservableList<SafePlayer>) ((ClientInterna) o).getTableData());
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
		controller.triggerLogout();

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

	public void setData(InetSocketAddress serverAdress, SafePlayer loggedInPlayer, String pw, Stage primaryStage,
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