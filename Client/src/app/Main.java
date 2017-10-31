package app;

import java.io.IOException;
import java.net.InetSocketAddress;

import app.login.LoginView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	private static InetSocketAddress serverAdress;

	public static void main(String[] args) {
		serverAdress = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
		launch(args);
	}

	@Override
	public void start(Stage ps) throws Exception {
		loadLoginView(ps);
	}

	private void loadLoginView(Stage ps) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("login/Login.fxml"));
		Parent root = loader.load();
		ps.setScene(new Scene(root));
		ps.setTitle("Login - Enter username & password - or SIGN UP !");
		((LoginView) loader.getController()).setData(ps, serverAdress);
		ps.show();
	}
}