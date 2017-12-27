package backend;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import backend.gameplay.Dealer;
import backend.handler.sub.PlayerActionClientHandler;
import entities.game.Game;
import entities.game.SafePlayer;
import entities.game.play.Board;
import entities.game.play.MinRoundBet;
import entities.game.play.PlayerHand;
import entities.query.game.PlayerActionQuery;
import entities.query.game.PlayerActionQuery.Option;
import entities.query.server.GameServerMsg;
import entities.query.server.GameServerMsg.GameMsgType;
import entities.query.server.ServerMsg;
import entities.query.server.ServerMsg.MsgType;

public class GameServer extends Server implements RemoteAccess {

	private boolean running = true;
	private Game game;
	private final Dealer dealer;
	private final List<PlayerHand> playerHandsList;
	private Board board;
	private final int port;
	private final AtomicInteger readyCounter;
	private boolean notAllPlayersReady;

	public GameServer(Game idGame, int port) {
		this.game = idGame;
		this.dealer = new Dealer();
		this.playerHandsList = new ArrayList<>();
		this.port = port;
		this.readyCounter = new AtomicInteger(0);
	}

	@Override
	public void run() {
		printPlayers();
		while (running) {
			List<SafePlayer> orderedPlayers = distributeCards();

			for (int roundCounter = 0; roundCounter < 5 && playerHandsList.size() > 1; roundCounter++) {
				MinRoundBet minRoundBet = new MinRoundBet(0);
				for (int playerID = 0; playerID < orderedPlayers.size(); playerID++) {

					InetSocketAddress asyncGameClientAddress = getClientAdress(orderedPlayers.get(playerID));
					if (roundCounter == 0) {
						/** pre-pre-flop */

						answer(new GameServerMsg.GameServerMsgBuilder(
								(ServerMsg) new ServerMsg.SMBuilder(MsgType.GAME)
										.destAddress(asyncGameClientAddress).build())
												.gameMsgType(GameMsgType.YOUR_POSITION).winnerID(playerID).build());
						// TODO:
						// called
						// method
						// should
						// recognize
						// playerID
						// as
						// winnerID--;
					} else {
						/**
						 * r=1:pre-flop<br>
						 * r=2:flop<br>
						 * r=3:turn<br>
						 * r=4:river
						 */
						answer(new GameServerMsg.GameServerMsgBuilder(
								(ServerMsg) new ServerMsg.SMBuilder(MsgType.GAME)
										.destAddress(asyncGameClientAddress).build()).gameMsgType(GameMsgType.YOUR_TURN)
												.minRoundBet(minRoundBet).build());
						new Thread(new PlayerActionClientHandler(receiveObject(), game.getPlayersList(),
								playerHandsList, minRoundBet)).start();
					}
				}
				if (roundCounter == 0) {
					triggerPlayerHands();
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

			answer(new GameServerMsg.GameServerMsgBuilder((ServerMsg) new ServerMsg.SMBuilder(MsgType.GAME)
					.destAddress(asyncGameplayPlayerAddress).build()).gameMsgType(GameMsgType.YOUR_HAND)
							.playerHand(playerHandsList.get(playerID)).build());

			System.err.println("GameServer answered to client-port: " + asyncGameplayPlayerAddress.toString()
					+ "\nMessageType: YOUR_HAND");

			playerID++;
			sleep(500);
		}
	}

	private void runPlayersReady() {
		notAllPlayersReady = true;
		// players have 30s
		runCountingThread(30);
		while (notAllPlayersReady) {
			PlayerActionQuery playerQuery = receiveObject();
			if (Objects.nonNull(playerQuery)) {
				if (playerQuery.getOption().equals(Option.READY)) {
					readyCounter.incrementAndGet();
					if (readyCounter.get() == game.getPlayersList().size()) {
						notAllPlayersReady = false;
					}
				}
			}
		}
	}

	private void runCountingThread(int sec) {
		new Thread(() -> {
			sleep(sec * 1000);
			// TODO: add one digit after testing.
			sendTimeout();
		}).start();
	}

	private void sendTimeout() {
		answer(new GameServerMsg.GameServerMsgBuilder((ServerMsg) new ServerMsg.SMBuilder(MsgType.GAME)
				.destAddress(createLocalhost(port)).build()).gameMsgType(GameMsgType.TIMEOUT).build());
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
	 * TODO: outsource: Returns the re-ordered playersList with the Btn player at
	 * index 0.
	 */
	private List<SafePlayer> chooseBtnPlayerRandomly() {
		List<SafePlayer> players = new ArrayList<>();
		int size = game.getPlayersList().size() - 1;
		for (int i = size; i >= 0; i--) {
			int random = (int) (Math.random() * i);
			players.add(game.getPlayersList().get(random));
			game.getPlayersList().remove(random);
		}
		return players;
	}

	private PlayerActionQuery receiveObject() {
		return (PlayerActionQuery) receiveMsg(port);
	}

	private void printPlayers() {
		game.getPlayersList()
				.forEach(p -> System.out.println("Player: " + p.getName() + " joined game: " + game.getName() + ".\n"));
	}

	private void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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

	public boolean isNotAllPlayersReady() {
		return notAllPlayersReady;
	}

	public void setNotAllPlayersReady(boolean notAllPlayersReady) {
		this.notAllPlayersReady = notAllPlayersReady;
	}
}