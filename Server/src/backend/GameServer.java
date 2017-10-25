package backend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import backend.handler.ClientHandler;
import backend.handler.PlayersActionClientHandler;
import entities.SafePlayer;
import entities.gameplay.Board;
import entities.gameplay.PlayerHand;
import entities.lobby.Game;
import entities.lobby.IDGame;
import entities.query.PlayersActionQuery;
import entities.query.Query;
import entities.query.server.GamesServerMsg;
import entities.query.server.GamesServerMsg.GameMsgType;
import entities.query.server.MinRoundBet;
import entities.query.server.ServerMsg;
import entities.query.server.ServerMsg.MsgType;
import logic.gameplay.Dealer;

public class GameServer extends ClientHandler implements RemoteAccess {

	private boolean running = true;
	private IDGame game;
	private final Dealer dealer;
	private final List<PlayerHand> playerHandsList;
	private Board board;
	private int gamesServerPort;

	public GameServer(IDGame idGame) {
		super(new byte[] {}, idGame.getPlayersList().get(0).getAdress());
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

			List<SafePlayer> orderedPlayers = chooseBtnPlayerRandomly();
			game = new IDGame(new Game(game.getName(), game.getBuyIn(), game.getStartChips(), game.getMaxPlayers(),
					game.getPaid(), game.getSignedUp()), game.getId(), orderedPlayers);

			orderedPlayers.forEach(
					player -> playerHandsList.add(new PlayerHand(player.getId(), dealer.newCard(), dealer.newCard())));
			triggerPlayerHands();
			setBoard(new Board(dealer.newCard(), dealer.newCard(), dealer.newCard(), dealer.newCard(),
					dealer.newCard()));
			for (int roundCounter = 0; roundCounter < 5 && playerHandsList.size() > 1; roundCounter++) {
				MinRoundBet minRoundBet = new MinRoundBet(0);
				for (int playerID = 0; playerID < orderedPlayers.size(); playerID++) {

					InetSocketAddress asyncGameClientAddress = new InetSocketAddress(
							orderedPlayers.get(playerID).getAdress().getAddress(),
							(orderedPlayers.get(playerID).getAdress().getPort() + 2));
					if (roundCounter == 0) {
						// pre-pre-flop
						answer(new GamesServerMsg(null, playerID, GameMsgType.YOUR_POSITION), asyncGameClientAddress);
					} else {
						// r=1:pre-flop
						// r=2:flop
						// r=3:turn
						// r=4:river
						answer(new GamesServerMsg(null, -5, GameMsgType.YOUR_TURN, minRoundBet),
								asyncGameClientAddress);
						new Thread(new PlayersActionClientHandler(receiveObject(), game.getPlayersList(),
								playerHandsList, minRoundBet)).start();
					}
				}
				roundCounter++;
			}
			running = false;
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
			System.out.println("playwe added.....");
			game.getPlayersList().remove(random);
		}
		return players;
	}

	private void triggerPlayerHands() {
		int playerID = 0;
		for (SafePlayer player : game.getPlayersList()) {
			InetSocketAddress asyncGameplayPlayerAddress = new InetSocketAddress(player.getAdress().getAddress(),
					player.getAdress().getPort() + 2);
			answer(new GamesServerMsg(MsgType.GAMES_SERVER_MSG, -5, GameMsgType.YOUR_HAND,
					playerHandsList.get(playerID)), asyncGameplayPlayerAddress);
			System.out.println("answered to: " + asyncGameplayPlayerAddress.getPort());
			playerID++;
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

	@Override
	public void switchQuery(Query received) {
		// ignored
	}

	@Override
	public void triggerServerMsg(ServerMsg serverMsg) {
		// ignored
	}
}