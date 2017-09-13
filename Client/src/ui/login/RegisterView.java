package ui.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class RegisterView {

	@FXML
	private TextField nameTxtField;

	@FXML
	private PasswordField pwField, pwConfirmField;

	@FXML
	private Button signUpBtn, backBtn;

	private LoginController loginController;

	@FXML
	void signUpBtnClicked(ActionEvent event) {
		
		
		
		String alertMessage = loginController.signUpRequest(nameTxtField.getText(), pwField.getText(),
				pwConfirmField.getText());

		if (alertMessage.endsWith(".")) {
			if (new Alert(AlertType.WARNING, alertMessage).showAndWait().isPresent()) {
				resetFields();
			}
		} else {
			if (new Alert(AlertType.INFORMATION, alertMessage).showAndWait().isPresent()) {
				loginController.openLobbyView(nameTxtField.getText());
				resetFields();
			}
		}
	}

	@FXML
	void backBtnClicked(ActionEvent event) {
		loginController.repaint("login");
		event.consume();
	}

	public void setController(LoginController loginController) {
		this.loginController = loginController;
	}

	private void resetFields() {
		nameTxtField.setText("");
		pwField.setText("");
		pwConfirmField.setText("");
	}
}
