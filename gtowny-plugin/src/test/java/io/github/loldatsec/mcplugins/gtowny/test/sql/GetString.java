package io.github.loldatsec.mcplugins.gtowny.test.sql;

import javafx.util.Pair;
import org.reborncraft.gtowny.data.sql.SQLInstance;
import org.reborncraft.gtowny.data.sql.init.SQLCredentials;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GetString {
	public static void main(String[] args) {
		try {
			SQLCredentials creds = new SQLCredentials();
			SQLInstance inst = new SQLInstance(creds.getHost(), creds.getPort(), creds.getUser(), creds.getPass(), "gtowny");
			Pair<Statement, ResultSet> res = inst.doQuery("SELECT * FROM `Towns`");
			while (res.getValue().next()) {
				System.out.println(res.getValue().getNString(2));
				System.out.println(res.getValue().getNString(3));
			}
			res.getValue().close();
			res.getKey().close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}
