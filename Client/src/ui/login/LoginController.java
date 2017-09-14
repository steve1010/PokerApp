package ui.login;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Observable;

import entities.SafePlayer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import ui.Controller;
import ui.lobby.LobbyView;

public class LoginController implements Controller {

	private LoginModel model;
	private Stage primaryStage;
	private LoginView loginView;
	private Parent loginNode;
	private Parent registerNode;
	private boolean isResiterViewLoaded = false;
	private SafePlayer loggedInPlayer;

	public LoginController(LoginView loginView) {
		this.loginView = loginView;
	}

	@Override
	public void update(Observable o, Object arg) {
	}

	public void loginRequest(final String name, final String pw) {
		SafePlayer player = model.checkLoginData(name, pw);
		if (player.isLoggedIn()) {
			this.loggedInPlayer = player;
			openLobbyView(pw);
		} else {
			loginView.wrongLoginDataAlert();
		}
	}

	protected void openLobbyView(String pw) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../lobby/Lobby.fxml"));
			Parent p = loader.load();
			primaryStage.close();
			primaryStage.getScene().setRoot(p);
			primaryStage.sizeToScene();
			primaryStage.setTitle("Poker Lobby - Please choose your game!");
			primaryStage.show();
			((LobbyView) loader.getController()).setData(model.getServerAdress(), loggedInPlayer, pw, primaryStage,
					loginView);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void openRegisterView() {
		if (!isResiterViewLoaded) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("Register.fxml"));
				Parent p = loader.load();
				primaryStage.close();
				primaryStage.getScene().setRoot(p);
				primaryStage.sizeToScene();
				primaryStage.setTitle("Sign Up!  - Enter username & password");
				primaryStage.show();
				((RegisterView) loader.getController()).setController(this);
				isResiterViewLoaded = true;
				registerNode = p;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			repaint("register");
		}
	}

	public SafePlayer userRegistered(String userName) {
		return model.isUserRegistered(userName);
	}

	public String signUpRequest(String userName, String pw, String pwConfirm) {

		SafePlayer player = userRegistered(userName);
		if (!(player.getId() == -1)) {
			return "The choosen username is not available. Please try another one.";
		}
		if ("".equals(pw) || "".equals(pwConfirm)) {
			return "The password field(s) must not be empty. Please enter a password.";
		}
		if (pw.equals(pwConfirm)) {
			this.loggedInPlayer = model.registerUser(userName, pw);
			return "Congratulations! You were successfully registered!";
		} else {
			return "The entered password is not the same. Please try again.";
		}
	}

	public void logout() {
		model.setLoggedOut();
	}

	public void repaint(String view) {
		primaryStage.close();
		switch (view) {
		case "login":
			primaryStage.getScene().setRoot(loginNode);
			primaryStage.setTitle("Login - Enter username & password - or SIGN UP !");
			break;
		case "register":
			primaryStage.getScene().setRoot(registerNode);
			primaryStage.setTitle("Sign Up!  - Enter username & password");
			break;
		default:
			break;
		}
		primaryStage.sizeToScene();
		primaryStage.setX(490);
		primaryStage.setY(230);
		loginView.resetValues();
		primaryStage.show();
	}

	public Stage getPrimaryStage() {
		return this.primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.loginNode = primaryStage.getScene().getRoot();
	}

	public void setSessionData(InetSocketAddress serverAdress) {
		model = new LoginModel(serverAdress);
		model.addObserver(this);
	}
}