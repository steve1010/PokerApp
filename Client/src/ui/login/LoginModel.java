package ui.login;

import java.net.InetSocketAddress;

import entities.Player;
import entities.query.PlayersQuery;
import entities.query.PlayersQuery.Option;
import ui.Model;

public class LoginModel extends Model {

	private Player player;

	public LoginModel(InetSocketAddress serverAdress) {
		super(serverAdress);
	}

	public Player checkLoginData(String nameProposal, String pwProposal) {
		sendObject(new PlayersQuery(nameProposal, pwProposal));
		return (Player) receiveObject();

	}

	public Player isUserRegistered(String newUser) {
		sendObject(new PlayersQuery(newUser));
		return (Player) receiveObject();
	}

	public Player registerUser(String userName, String pw) {
		sendObject(new PlayersQuery(Option.REGISTER, userName, pw));
		return (Player) receiveObject();
	}

	// public void setLoggedIn(String name) {
	// sendObject(new PlayersQuery(Option.LOGIN, name));
	// }

	public void setLoggedOut() {
		sendObject(new PlayersQuery(Option.LOGOUT, player.getName()));
	}
}
