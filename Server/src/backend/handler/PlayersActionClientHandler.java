package backend.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.List;

import entities.SafePlayer;
import entities.query.PlayersActionQuery;
import entities.query.Query;
import entities.query.server.GamesServerMsg;
import entities.query.server.GamesServerMsg.GameMsgType;
import entities.query.server.ServerMsg;
import entities.query.server.ServerMsg.MsgType;

public class PlayersActionClientHandler extends ClientHandler {

	private PlayersActionQuery playerQuery;
	private List<SafePlayer> playersList;

	public PlayersActionClientHandler(PlayersActionQuery playersActionQuery, List<SafePlayer> list) {
		super(new byte[1], list.get(0).getAdress());
		this.playerQuery = playersActionQuery;
		this.playersList = list;
	}

	@Override
	public void run() {
		switchQuery(playerQuery);
	}

	@Override
	public void switchQuery(Query received) {

		switch (((PlayersActionQuery) received).getOption()) {
		case CHECK:
			triggerServerMsg(new GamesServerMsg(MsgType.GAMES_SERVER_MSG, -5, GameMsgType.USER_CHECKS));
			break;
		case FOLD:
			triggerServerMsg(new GamesServerMsg(MsgType.GAMES_SERVER_MSG, -5, GameMsgType.USER_FOLDS));
			break;
		case CALL:
			triggerServerMsg(new GamesServerMsg(MsgType.GAMES_SERVER_MSG, -5, GameMsgType.USER_CALLS));
			break;
		case RAISE:
//			triggerServerMsg(new GamesServerMsg(MsgType.GAMES_SERVER_MSG, playerQuery.g, gameMsgType));
			break;
		default:
			break;
		}
	}

	@Override
	public void triggerServerMsg(ServerMsg serverMsg) {
		playersList.forEach(player -> sendObject(serverMsg, player.getAdress()));
	}

	private void sendObject(Object o, InetSocketAddress adress) {
		try (DatagramSocket clientSocket = new DatagramSocket();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(bos)) {
			os.writeObject(o);
			byte[] toSendData = bos.toByteArray();
			DatagramPacket toSendPacket = new DatagramPacket(toSendData, toSendData.length, adress);
			clientSocket.send(toSendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}