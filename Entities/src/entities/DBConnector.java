package entities;

import java.util.List;

import entities.game.Game;
import entities.game.SafePlayer;

/**
 * Establishes DB Connection and delivers desired results on request.
 */
public interface DBConnector {

	public void connect();

	public List<SafePlayer> getPlayers();

	public List<Game> getGames();
}