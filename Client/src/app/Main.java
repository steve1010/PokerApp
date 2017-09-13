package app;

import java.io.IOException;
import java.net.InetSocketAddress;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.login.LoginView;

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
		loader.setLocation(getClass().getResource("../ui/login/Login.fxml"));
		Parent root = loader.load();
		ps.setScene(new Scene(root));
		ps.setTitle("Login - Enter username & password - or SIGN UP !");
		((LoginView) loader.getController()).setData(ps, serverAdress);
		ps.show();
	}
}
/**
 * HandController: currentHand.setMinBetValueToBigBlind(getBigBlind()); /**
 * WHILE (verbleibendeSpielerAnzahl > 1) { Hand hand =
 * Dealer.newHand(verbleibendeSpielerAnzahl WHILE(Hand
 */

// 8.) Dealer den Button auswählen lassen (Zufall), Spielerpositionen
// bestimmen/ändern, Small- & Big Blind setzen

// 9.) Aktiviere GameAction-GUI bei UTG-Player

/**
 * Algorithmus:Spieler UTG beginnt zu handeln -> 1 min Zeitbank, nach 20s
 * Warnung, dass Zeitbank aktiviert wird. { wenn Zeit>20s aktiviere
 * Zeitbank(Spieler). wenn Zeit(Zeitbank(Spieler))>60s führe PlayerAction.FOLD
 * automatisch aus.} 10.) ;;; SWITCH(Player.getAction()){ CASE
 * playerAction.FOLD: handcontroller.fold(ruft auf model
 * removeFromCurrentHand(currentPlayer); CASE playerAction.CHECK:
 * handcontroller.check(currentPlayer); CASE playerAction.RAISE:
 * handController.raise(currentPlayer, toValue(a_valid_value)); CASE
 * playerAction.CALL: handController.call(currentPlayer);
 */