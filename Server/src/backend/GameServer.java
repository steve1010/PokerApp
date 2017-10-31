package backend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import backend.handler.ClientHandler;
import backend.handler.PlayersActionClientHandler;
import entities.Game;
import entities.SafePlayer;
import entities.gameplay.Board;
import entities.gameplay.PlayerHand;
import entities.query.PlayersActionQuery;
import entities.query.PlayersActionQuery.Option;
import entities.query.Query;
import entities.query.server.GamesServerMsg;
import entities.query.server.GamesServerMsg.GameMsgType;
import entities.query.server.MinRoundBet;
import entities.query.server.ServerMsg;
import entities.query.server.ServerMsg.MsgType;
import logic.gameplay.Dealer;

public class GameServer extends ClientHandler implements RemoteAccess {

	private boolean running = true;
	private Game game;
	private final Dealer dealer;
	private final List<PlayerHand> playerHandsList;
	private Board board;
	private final int port;
	private AtomicInteger readyCounter;
	private boolean notAllPlayersReady;

	public GameServer(Game idGame, int port) {
		super(new byte[] {}, idGame.getPlayersList().get(0).getAdress());
		this.game = idGame;
		this.dealer = new Dealer();
		this.playerHandsList = new ArrayList<>();
		this.port = port;
	}

	@Override
	public void run() {

		printPlayers();

		while (running) {
			List<SafePlayer> orderedPlayers = distributeCards();

			for (int roundCounter = 0; roundCounter < 5 && playerHandsList.size() > 1; roundCounter++) {
				MinRoundBet minRoundBet = new MinRoundBet(0);
				for (int player = 0; player < orderedPlayers.size(); player++) {

					InetSocketAddress asyncGameClientAddress = getClientAdress(orderedPlayers.get(player));
					if (roundCounter == 0) {
						/** pre-pre-flop */
						answer(new GamesServerMsg(null, player, GameMsgType.YOUR_POSITION), asyncGameClientAddress);
					} else {
						/**
						 * r=1:pre-flop<br>
						 * r=2:flop<br>
						 * r=3:turn<br>
						 * r=4:river
						 */
						answer(new GamesServerMsg(null, -5, GameMsgType.YOUR_TURN, minRoundBet),
								asyncGameClientAddress);
						new Thread(new PlayersActionClientHandler(receiveObject(), game.getPlayersList(),
								playerHandsList, minRoundBet)).start();
					}
				}
			}
			running = false;
		}
	}

	private InetSocketAddress getClientAdress(SafePlayer safePlayer) {
		InetSocketAddress asyncGameClientAddress = new InetSocketAddress(safePlayer.getAdress().getAddress(),
				(safePlayer.getAdress().getPort() + 2));
		return asyncGameClientAddress;
	}

	private List<SafePlayer> distributeCards() {
		List<SafePlayer> orderedPlayers = distributePlayerCardsServerside();
		triggerPlayerHands();
		setBoard(new Board(dealer.newCard(), dealer.newCard(), dealer.newCard(), dealer.newCard(), dealer.newCard()));
		return orderedPlayers;
	}

	@Override
	public void shutdown() {
		this.running = false;
	}

	/**
	 * timer functionality: Games start on last player registered plus 30seconds or
	 * on all players clicked 'im ready'
	 */
	private void triggerPlayerHands() {
		runPlayersReady();

		int playerID = 0;
		for (SafePlayer player : game.getPlayersList()) {
			InetSocketAddress asyncGameplayPlayerAddress = new InetSocketAddress(player.getAdress().getAddress(),
					(player.getAdress().getPort() + 2));
			answer(new GamesServerMsg(MsgType.GAMES_SERVER_MSG, -5, GameMsgType.YOUR_HAND,
					playerHandsList.get(playerID)), asyncGameplayPlayerAddress);

			System.err.println("GameServer answered to client-port: " + asyncGameplayPlayerAddress.toString());

			playerID++;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void runPlayersReady() {
		notAllPlayersReady = true;
		runCountingThread();
		while (notAllPlayersReady) {
			PlayersActionQuery playerQuery = receiveObject();
			if (Objects.nonNull(playerQuery)) {
				if (playerQuery.getOption().equals(Option.READY)) {
					readyCounter.incrementAndGet();
					if (readyCounter.get() == game.getPlayersList().size()) {
						break;
					}
				}
			}
		}
	}

	private void runCountingThread() {
		new Thread(() -> {
			try {/** 30 seconds */
				Thread.sleep(30000);
				answer(new ServerMsg(null, port), new InetSocketAddress("localhost", port));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private List<SafePlayer> distributePlayerCardsServerside() {
		List<SafePlayer> orderedPlayers = chooseBtnPlayerRandomly();
		game = new Game(game.getName(), game.getId(), game.getBuyIn(), game.getStartChips(), game.getMaxPlayers(),
				game.getPaid(), game.getSignedUp(), orderedPlayers);

		orderedPlayers.forEach(player -> {
			PlayerHand playerHand = new PlayerHand(player.getId(), dealer.newCard(), dealer.newCard());
			playerHandsList.add(playerHand);
		});
		return orderedPlayers;
	}

	/**
	 * Returns the re-ordered playersList with the Btn player at index 0.
	 */
	private List<SafePlayer> chooseBtnPlayerRandomly() {
		List<SafePlayer> players = new ArrayList<>();
		int size = game.getPlayersList().size() - 1;
		for (int i = size; i >= 0; i--) {
			int random = (int) (Math.random() * i);
			players.add(game.getPlayersList().get(random));
			System.out.println("player added..");
			game.getPlayersList().remove(random);
		}
		return players;
	}

	private PlayersActionQuery receiveObject() {
		try (DatagramSocket socket = new DatagramSocket(port)) {
			byte[] incomingData = new byte[3048];
			DatagramPacket incPacket = new DatagramPacket(incomingData, incomingData.length);
			socket.receive(incPacket);
			try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(incPacket.getData()))) {
				Object received = ois.readObject();
				if (received instanceof PlayersActionQuery) {
					return (PlayersActionQuery) received;
				}
				if (received instanceof ServerMsg) {
					// 30s later
					setNotAllPlayersReady(false);
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void printPlayers() {
		game.getPlayersList()
				.forEach(p -> System.out.println("Player: " + p.getName() + " joined game: " + game.getName() + ".\n"));
	}

	/**
	 * Getters & Setters
	 */

	public Game getGame() {
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

	public boolean isNotAllPlayersReady() {
		return notAllPlayersReady;
	}

	public void setNotAllPlayersReady(boolean notAllPlayersReady) {
		this.notAllPlayersReady = notAllPlayersReady;
	}
}