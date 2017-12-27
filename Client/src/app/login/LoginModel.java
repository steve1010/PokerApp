package app.login;

import java.net.InetSocketAddress;

import app.ui.ClientModel;
import entities.game.Player;
import entities.game.SafePlayer;
import entities.query.game.PlayerQuery;
import entities.query.game.PlayerQuery.Option;
import entities.query.server.ServerMsg;

public class LoginModel extends ClientModel {

	private Player player;
	private int playerPort;

	public LoginModel(InetSocketAddress serverAdress) {
		super(serverAdress);
	}

	@Override
	public SafePlayer getLoggedInPlayer() {
		return Player.toSafePlayer(this.player);
	}

	public SafePlayer checkLoginData(String nameProposal, String pwProposal) {
		sendQuery(new PlayerQuery.PQBuilder(Option.GET).pName(nameProposal).pW(pwProposal).build());
		return ((ServerMsg) receiveMsg(playerPort)).getPlayer();
	}

	public SafePlayer isUserRegistered(String newUser) {
		sendQuery(new PlayerQuery.PQBuilder(Option.GET).pName(newUser).build());
		return ((ServerMsg) receiveMsg(playerPort)).getPlayer();
	}

	public Player registerUser(String userName, String pw) {
		sendQuery(new PlayerQuery.PQBuilder(Option.REGISTER).pName(userName).pW(pw).build());
		return Player.fromSafePlayer(((ServerMsg) receiveMsg(playerPort)).getPlayer());
	}

	public void setLoggedOut() {
		sendQuery(new PlayerQuery.PQBuilder(Option.LOGOUT).player(player).build());
	}

	@Override
	public String getPw() {
		return player.getPw();
	}

	public void setPlayer(Player loggedInPlayer) {
		this.player = loggedInPlayer;
		this.playerPort = player.getAdress().getPort();
	}
}