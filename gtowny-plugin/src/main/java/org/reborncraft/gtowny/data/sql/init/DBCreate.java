package org.reborncraft.gtowny.data.sql.init;

import org.reborncraft.gtowny.data.sql.SQLInstance;

import java.sql.SQLException;

public class DBCreate {
	public static void main(String[] args) {
		try {
			SQLCredentials creds = new SQLCredentials();
			SQLInstance.createDB("GTowny", creds.getHost(), creds.getPort(), creds.getUser(), creds.getPass());
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
