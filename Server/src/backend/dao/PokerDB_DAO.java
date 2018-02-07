package backend.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Data Access Object for localhost:5433/pokerDB
 */
public class PokerDB_DAO {

	private final String URL = "jdbc:postgresql://localhost:5433/pokerDB";
	private final String SCHEMA = "public";
	private final String USER = "postgres";
	private final String PASSWORD = "pgPwJ87510x$";
	private Connection conn;

	public PokerDB_DAO() {
		init();
	}

	private void init() {
		Connection c = null;
		try {
			Class.forName("org.postgresql.Driver");
			c = getOrCreateConnection();

			PreparedStatement ps = c.prepareStatement(" - here the statement - ");
			ps.executeQuery();

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		this.conn = c;
		System.out.println("Opened database successfully");
	}

	private Connection getOrCreateConnection() throws SQLException {
		// set user, pw, schema and other connection properties
		Properties connectionProperties = new Properties();
		connectionProperties.setProperty("user", USER);
		connectionProperties.setProperty("password", PASSWORD);
		connectionProperties.setProperty("currentSchema", SCHEMA);

		Connection connection = DriverManager.getConnection(URL, connectionProperties);
		return connection;
	}
}
