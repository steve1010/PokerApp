package ui.login;

import java.net.InetSocketAddress;

import entities.Player;
import entities.SafePlayer;
import entities.query.PlayersQuery;
import entities.query.PlayersQuery.Option;
import ui.Model;

public class LoginModel extends Model {

	private Player player;

	public LoginModel(InetSocketAddress serverAdress) {
		super(serverAdress);
	}

	@Override
	public SafePlayer getLoggedInPlayer() {
		return Player.toSafePlayer(this.player);
	}

	public SafePlayer checkLoginData(String nameProposal, String pwProposal) {
		sendObject(new PlayersQuery(nameProposal, pwProposal));
		return (SafePlayer) receiveObject();
	}

	public SafePlayer isUserRegistered(String newUser) {
		sendObject(new PlayersQuery(newUser));
		return (SafePlayer) receiveObject();
	}

	public Player registerUser(String userName, String pw) {
		sendObject(new PlayersQuery(Option.REGISTER, userName, pw));
		return Player.fromSafePlayer((SafePlayer) receiveObject());
	}

	public void setLoggedOut() {
		sendObject(new PlayersQuery(Option.LOGOUT, player.getName()));
	}

	@Override
	public String getPw() {
		return player.getPw();
	}

	public void setPlayer(Player loggedInPlayer) {
		this.player = loggedInPlayer;
	}
}