package co.reborncraft.stackperms.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AuthController implements HttpHandler {
	private StackPermsHTTPServerlet stackPermsHTTPServerlet;

	public AuthController(StackPermsHTTPServerlet stackPermsHTTPServerlet) {
		this.stackPermsHTTPServerlet = stackPermsHTTPServerlet;
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		OutputStream os = httpExchange.getResponseBody();
		if (stackPermsHTTPServerlet.getAuthIP() == null) {
			stackPermsHTTPServerlet.setAuthIP(httpExchange.getRemoteAddress().getAddress().getHostAddress());
			String date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z").format(new Date(System.currentTimeMillis() + 86400000));
			httpExchange.getResponseHeaders().add("Set-Cookie", "sp-auth=" + stackPermsHTTPServerlet.getServerletAuthCode() + "; " +
					"path=/; " +
					"domain=" + stackPermsHTTPServerlet.getBindingAddr().getAddress().getHostAddress() + "; " +
					"expires=" + date);
			httpExchange.getResponseHeaders().add("Location", "http://" + stackPermsHTTPServerlet.getBindingAddr().getAddress().getHostAddress() + ":" + stackPermsHTTPServerlet.getBindingAddr().getPort());
			httpExchange.sendResponseHeaders(302, 0);
		} else {
			String out = "<h1>403 Forbidden</h1><p>Session already claimed.</p>";
			httpExchange.sendResponseHeaders(403, out.length());
			os.write(out.getBytes(StandardCharsets.UTF_8));
		}
		os.close();
		httpExchange.close();
	}
}
