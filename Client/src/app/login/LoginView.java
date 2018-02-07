package app.login;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LoginView implements Initializable {

	@FXML
	private LoginCtrl controller;

	@FXML
	private Pane loginPane;

	@FXML
	private TextField nameTxtField;
	@FXML
	private PasswordField pwField;
	@FXML
	private Button loginBtn, signUpBtn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		controller = new LoginCtrl(this);
	}

	@FXML
	void loginBtnClicked(ActionEvent event) {
		controller.loginRequest(nameTxtField.getText(), pwField.getText());
	}

	@FXML
	void signUpBtnClicked(ActionEvent event) {
		controller.openRegisterView();
	}

	@FXML
	void keyPressed(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			loginBtn.fire();
			event.consume();
		}
	}

	public void wrongLoginDataAlert() {
		Alert loginAlert = new Alert(AlertType.INFORMATION, "Invalid username or password!");
		if (loginAlert.showAndWait().isPresent()) {
			loginAlert.close();
			resetValues();
		}
	}

	public void setData(Stage primaryStage, InetSocketAddress serverAdress) {
		controller.setStage(primaryStage);
		controller.setData(serverAdress);
	}

	public void resetValues() {
		nameTxtField.setText("");
		pwField.setText("");
	}

	public Pane getLoginPane() {
		return loginPane;
	}

	public LoginCtrl getController() {
		return controller;
	}
}