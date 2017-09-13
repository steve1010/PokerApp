package ui.lobby;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import entities.Player;
import entities.lobby.IDGame;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import ui.Controller;

public class LobbyView implements Controller, Initializable, Observer {

	private LobbyCtrl controller;

	private PropertyValueFactory<IDGame, Integer> idColumnFactory, startChipsColumnFactory, playersMaxColumnFactory,
			paidColumnFactory, signedUpColumnFactory;
	private PropertyValueFactory<IDGame, Double> buyInColumnFactory;
	private PropertyValueFactory<IDGame, String> nameColumnFactory;

	@FXML
	private TableView<IDGame> contentTable;

	@FXML
	private TableColumn<IDGame, Integer> iDColumn, startChipsColumn, paidColumn, playersMaxColumn, signedUpColumn;

	@FXML
	private TableColumn<IDGame, String> nameColumn;

	@FXML
	private TableColumn<IDGame, Double> buyInColumn;

	@FXML
	private HBox createHBox;

	@FXML
	private TextField nameTxtField, buyInTxtField, startChipsTxtField, playersMaxTxtField, paidTxtField;

	@FXML
	private Button createBtn, enrollBtn;

	@FXML
	private Label playerNameLbl, bankrollLbl;

	@FXML
	private VBox createVBox;

	private ObservableList<IDGame> tableData;

	private IDGame currentlySelectedItem;

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

		this.tableData = FXCollections.observableArrayList();
		contentTable.setItems(tableData);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable observable, Object o) {
		Platform.runLater(() -> {
			contentTable.setItems((ObservableList<IDGame>) o);
			contentTable.refresh();
		});
	}

	@FXML
	void createBtnClicked(ActionEvent event) {
		controller.requestCreation(nameTxtField.getText(), buyInTxtField.getText(), startChipsTxtField.getText(),
				playersMaxTxtField.getText(), paidTxtField.getText());
		resetFields();
	}

	@FXML
	void enroll(ActionEvent event) {
		this.currentlySelectedItem = contentTable.getSelectionModel().getSelectedItem();
		controller.enrollRequest(currentlySelectedItem);
	}

	public void wrongInputAlert() {
		new Alert(AlertType.ERROR);
	}

	public void createModel(InetSocketAddress serverAdress, Player player) {
		this.controller = new LobbyCtrl(this, serverAdress, player);
		this.playerNameLbl.setText(player.getName());
		this.bankrollLbl.setText(String.valueOf(player.getBankRoll()));
		if (player.getName().equals("Admin")) {
			this.createVBox.setVisible(true);
		}
	}

	public void showEnrollAlert(String enrollPlayerIn) {
		if (enrollPlayerIn.startsWith("Y")) {
			new Alert(AlertType.WARNING, enrollPlayerIn).showAndWait();
		} else {
			new Alert(AlertType.INFORMATION, enrollPlayerIn).showAndWait();
			double newValue = Double.parseDouble(bankrollLbl.getText()) - currentlySelectedItem.getBuyIn();
			bankrollLbl.setText(String.valueOf(newValue));
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