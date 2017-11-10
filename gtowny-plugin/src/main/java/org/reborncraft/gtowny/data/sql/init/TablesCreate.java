package org.reborncraft.gtowny.data.sql.init;

import org.reborncraft.gtowny.data.sql.SQLInstance;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class TablesCreate {
	public static void main(String[] args) {
		Statement stmt = null;
		try {
			SQLCredentials creds = new SQLCredentials();
			SQLInstance sql = new SQLInstance(creds.getHost(), creds.getPort(), creds.getUser(), creds.getPass(), "GTowny");
			InputStream is = TablesCreate.class.getResourceAsStream("/sqlinit.sql");
			Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
			String[] query = (s.hasNext() ? s.next() : "").replaceAll("\r\n|\r|\n", "").split(";");
			stmt = sql.getSQLConnection().createStatement();
			for (String q : query) {
				try {
					System.out.print(q);
					stmt.executeUpdate(q);
					System.out.println(" | OK");
				} catch (SQLException e) {
					System.out.println(" | FAIL");
				}
			}
			is.close();
			sql.close();
		} catch (SQLException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
