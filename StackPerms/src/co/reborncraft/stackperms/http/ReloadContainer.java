package co.reborncraft.stackperms.http;

import co.reborncraft.stackperms.StackPerms;
import com.sun.net.httpserver.HttpExchange;
import org.bukkit.Bukkit;

import java.io.IOException;

public class ReloadContainer extends SecuredHttpHandler {
	public ReloadContainer(StackPermsHTTPServerlet stackPermsHTTPServerlet) {
		super(stackPermsHTTPServerlet);
	}

	@Override
	public String processRequest(HttpExchange exch, String requestURI) throws IOException {
		StackPerms.getInstance().onCommand(Bukkit.getConsoleSender(), null, null, new String[]{"reload"});
		exch.sendResponseHeaders(302, 0);
		exch.getResponseHeaders().add("Location", "/");
		return null;
	}
}
