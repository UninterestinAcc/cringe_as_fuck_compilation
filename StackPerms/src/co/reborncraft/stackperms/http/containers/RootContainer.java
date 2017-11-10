package co.reborncraft.stackperms.http.containers;

import co.reborncraft.stackperms.http.StackPermsHTTPServerlet;
import co.reborncraft.stackperms.http.SecuredHttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class RootContainer extends SecuredHttpHandler {
	public RootContainer(StackPermsHTTPServerlet stackPermsHTTPServerlet) {
		super(stackPermsHTTPServerlet);
	}

	@Override
	public String processRequest(HttpExchange exch, final String requestURI) throws IOException {
		return "<a href=\"/stacks\">Inspect stacks.</a><br/>" +
				"<a href=\"/groups\">Inspect groups.</a><br/>" +
				"<a href=\"/players\">Inspect players.</a><br/><hr/><br/>" +
				"<a href=\"/reload\">Reload all config from disk.</a>";
	}
}
