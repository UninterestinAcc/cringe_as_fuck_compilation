package co.reborncraft.syslogin_banmanager.sql;

import co.reborncraft.utils.Utils;

import java.io.EOFException;
import java.net.ConnectException;
import java.sql.*;

public class SQLManager {
	private static final String DB_NAME = "SysLogin_BanManager";
	private final String host;
	private final int port;
	private final String username;
	private final String password;
	private Connection con = null;

	public SQLManager() throws ClassNotFoundException {
		final String[] credentials = Utils.readFileWithoutComments("credentials.txt");
		this.host = credentials[0];
		this.port = Integer.parseInt(credentials[1]);
		this.username = credentials[2];
		this.password = credentials[3];
		Class.forName("com.mysql.jdbc.Driver");

		try (Connection createDB = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port, username, password)) {
			Statement existenceCheck = createDB.createStatement();
			ResultSet rs = existenceCheck.executeQuery("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + DB_NAME + "';");
			if (!rs.next()) { // Database does not exist, proceed to create it.
				Statement initialize = createDB.createStatement();
				initialize.execute("CREATE DATABASE `" + DB_NAME + "`");
				initialize.close();
			}
			rs.close();
			existenceCheck.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		getOrCreateConnection();
		if (con != null) {
			for (String sql : Utils.readResourceAsString("/initialize.sql").split(";(\\r\\n|\\r|\\n)+")) {
				if (!sql.matches("^\\s*$")) try (Statement createRow = con.createStatement()) {
					createRow.execute(sql);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Connection newConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + DB_NAME, username, password);
	}

	public Connection getOrCreateConnection() {
		try {
			return con == null || con.isClosed() ? (con = newConnection()) : con;
		} catch (SQLException e) {
			if (e.getCause() == null || !(e.getCause() instanceof ConnectException || e.getCause() instanceof EOFException)) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
