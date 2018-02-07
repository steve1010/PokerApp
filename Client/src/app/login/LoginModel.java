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

	/**
	 * Used for user authentification.
	 * 
	 * @param nameProposal
	 * @param pwProposal
	 * @return
	 */
	public SafePlayer loginPlayerIfAuthentificated(String nameProposal, String pwProposal) {
		addressAndSend(new PlayerQuery.PQBuilder(Option.EXISTS).pName(nameProposal).pW(pwProposal).build());
		return ((ServerMsg) receiveMsg(playerPort)).getPlayer();
	}

	public void logoutUser() {
		addressAndSend(new PlayerQuery.PQBuilder(Option.LOGOUT).pName(player.getName()).pW(player.getPw()).build());
	}

	/**
	 * Used to check if prefered name is already in use.
	 * 
	 * @param newUser
	 * @return
	 */
	public SafePlayer userRegistered(String newUser) {
		addressAndSend(new PlayerQuery.PQBuilder(Option.EXISTS).pName(newUser).build());
		return ((ServerMsg) receiveMsg(playerPort)).getPlayer();
	}

	/**
	 * Used to register users.
	 * 
	 * @param userName
	 * @param pw
	 * @return
	 */
	public Player registerUser(String userName, String pw) {
		addressAndSend(new PlayerQuery.PQBuilder(Option.REGISTER).pName(userName).pW(pw).build());
		return Player.fromSafePlayer(((ServerMsg) receiveMsg(playerPort)).getPlayer());
	}

	/**
	 * Getter & Setter
	 */

	@Override
	public SafePlayer getLoggedInPlayer() {
		return Player.toSafePlayer(player);
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