package backend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import backend.handler.PlayersActionClientHandler;
import entities.SafePlayer;
import entities.gameplay.Board;
import entities.gameplay.PlayerHand;
import entities.lobby.IDGame;
import entities.query.PlayersActionQuery;
import entities.query.PlayersActionQuery.Option;
import entities.query.server.GamesServerMsg;
import entities.query.server.GamesServerMsg.GameMsgType;
import logic.gameplay.Dealer;

public class GameServer implements RemoteAccess {

	private boolean running = true;
	private final IDGame game;
	private final Dealer dealer;
	private final List<PlayerHand> playerHandsList;
	private Board board;
	private int gamesServerPort;

	public GameServer(IDGame idGame) {
		this.game = idGame;
		this.dealer = new Dealer();
		this.playerHandsList = new ArrayList<>();
	}

	@Override
	public void run() {
		// game starting..
		game.getPlayersList()
				.forEach(p -> System.out.println("Player: " + p.getName() + " joined game: " + game.getName() + ".\n"));

		while (running) {
			game.getPlayersList()
					.forEach(player -> playerHandsList.add(new PlayerHand(dealer.newCard(), dealer.newCard())));
			setBoard(new Board(dealer.newCard(), dealer.newCard(), dealer.newCard(), dealer.newCard(),
					dealer.newCard()));
			triggerPlayerHands();
			List<SafePlayer> orderedPlayers = chooseBtnPlayerRandomly();
			for (SafePlayer player : orderedPlayers) {
				sendObject(new GamesServerMsg(null, -5, GameMsgType.YOUR_TURN),
						new InetSocketAddress(player.getAdress().getAddress(), player.getAdress().getPort() + 2));
				new Thread(new PlayersActionClientHandler(receiveObject(), game.getPlayersList())).start();
			}
		}
	}

	private PlayersActionQuery receiveObject() {
		try (DatagramSocket socket = new DatagramSocket(gamesServerPort)) {
			byte[] incomingData = new byte[2048];
			DatagramPacket incPacket = new DatagramPacket(incomingData, incomingData.length);
			socket.receive(incPacket);
			// process received data
			try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(incPacket.getData()))) {
				Object received = ois.readObject();
				if (received instanceof PlayersActionQuery) {
					return (PlayersActionQuery) received;
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<SafePlayer> chooseBtnPlayerRandomly() {
		List<SafePlayer> players = new ArrayList<>();
		for (int i = game.getPlayersList().size(); i > 0; i--) {
			int random = (int) (Math.random() * i);
			players.add(game.getPlayersList().get(random));
			game.getPlayersList().remove(random);
		}
		return players;
	}

	private void triggerPlayerHands() {
		int playerID = 0;
		for (SafePlayer player : game.getPlayersList()) {
			sendObject(playerHandsList.get(playerID), player.getAdress());
			playerID++;
		}
	}

	private void sendObject(Object o, InetSocketAddress adress) {
		try (DatagramSocket clientSocket = new DatagramSocket(gamesServerPort);
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

	@Override
	public void shutdown() {
		this.running = false;
	}

	public IDGame getGame() {
		return game;
	}

	public boolean isRunning() {
		return running;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public Dealer getDealer() {
		return dealer;
	}

}
