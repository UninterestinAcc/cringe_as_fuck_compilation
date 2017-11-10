package org.reborncraft.gtowny.data.sql;

import javafx.util.Pair;
import org.reborncraft.gtowny.GTowny;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Instant;

public class SQLInstance {

	private Connection connection = null;

	public Connection getSQLConnection() throws SQLException {
		if (connection != null) {
			return connection;
		} else {
			throw new SQLException("Not connected.");
		}
	}

	public SQLInstance(String host, int port, String username, String password, String dbName) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + dbName, username, password);
	}

	@SuppressWarnings ("JDBCResourceOpenedButNotSafelyClosed")
	public static void createDB(String dbName, String host, int port, String username, String password) throws SQLException, ClassNotFoundException {
		SQLInstance inst = new SQLInstance(host, port, username, password, "");
		Statement s = inst.getSQLConnection().createStatement();
		s.execute("CREATE DATABASE `" + dbName.replaceAll("\\x1a|`", "") + "`");
		s.close();
		inst.close();
	}

	/**
	 * @return ResultSet MAKE SURE TO CLOSE IT
	 */
	@SuppressWarnings ("JDBCResourceOpenedButNotSafelyClosed")
	public Pair<Statement, ResultSet> doQuery(String sql) {
		Statement statement = null;
		ResultSet result = null;
		try {
			statement = getSQLConnection().createStatement();
			result = statement.executeQuery(sql);
		} catch (SQLException ex) {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
			}
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
			}
		}
		return new Pair<>(statement, result);
	}


	public int countQueryResults(String sql) {
		int i = 0;
		Pair<Statement, ResultSet> res = null;
		try {
			res = doQuery(sql);
			while (res.getValue().next()) {
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (res.getValue() != null) {
				try {
					res.getValue().close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (res.getKey() != null) {
				try {
					res.getKey().close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	public void doUpdate(String sql) {
		Statement statement = null;
		try {
			statement = getSQLConnection().createStatement();
			statement.executeUpdate(sql);
			if (sql.toUpperCase().startsWith("DELETE")) {
				try {
					Path file = Paths.get(GTowny.getGTownyDataFolder().getAbsolutePath() + new SimpleDateFormat("y-M-d").format(Date.from(Instant.now())) + "-DELETES.sql");
					if (!file.toFile().exists()) {
						file.toFile().createNewFile();
					}
					Files.write(file, sql.getBytes(), StandardOpenOption.APPEND);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} finally {
			try {
				if (statement != null) statement.close();
			} catch (SQLException se2) {
				se2.printStackTrace();
			}
		}
	}

	public void close() throws SQLException {
		if (connection != null) getSQLConnection().close();
	}
}
