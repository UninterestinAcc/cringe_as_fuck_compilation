package co.reborncraft.stackperms.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class SecuredHttpHandler implements HttpHandler {
	protected StackPermsHTTPServerlet stackPermsHTTPServerlet;

	public SecuredHttpHandler(StackPermsHTTPServerlet stackPermsHTTPServerlet) {
		this.stackPermsHTTPServerlet = stackPermsHTTPServerlet;
	}

	public abstract String processRequest(HttpExchange exch, final String requestURI) throws IOException;

	@Override
	public final void handle(HttpExchange exch) throws IOException {
		String requestURI = exch.getRequestURI().toString().toLowerCase().replaceAll("/+", "/");
		OutputStream os = exch.getResponseBody();
		if (stackPermsHTTPServerlet.getAuthIP().equals(exch.getRemoteAddress().getAddress().getHostAddress())) {
			String cookies = exch.getRequestHeaders().getFirst("Cookie");
			if (cookies != null && cookies.contains("sp-auth=" + stackPermsHTTPServerlet.getServerletAuthCode())) {
				String referer = exch.getRequestHeaders().getFirst("Referer");
				String uriHead = "http://" + stackPermsHTTPServerlet.getBindingAddr().getAddress().getHostAddress() + ":" + stackPermsHTTPServerlet.getBindingAddr().getPort();
				if (referer == null || referer.startsWith(uriHead)) {
					if (requestURI.matches("^(/(stack|group|(search)?player)/?)$")) {
						String sendResponse = requestURI;
						if (requestURI.endsWith("/")) {
							sendResponse = sendResponse.substring(0, requestURI.length() - 1);
						}
						sendResponse += "s";
						exch.getResponseHeaders().add("Location", sendResponse);
						exch.sendResponseHeaders(302, 0);
					} else if (requestURI.matches("^(/(((stack|group|player)(s|/(\\w{3,64}))|reload)/?)?)$")) {
						try {
							String out = processRequest(exch, requestURI);
							if (out != null && !out.isEmpty()) {
								byte[] bytes = out.getBytes(StandardCharsets.UTF_8);
								exch.sendResponseHeaders(200, bytes.length);
								os.write(bytes);
							}
						} catch (Throwable t) {
							t.printStackTrace();
							String err = "<h1>500 Internal Server Error</h1><p>Woops! The server made a poo poo!</p> <b>" + t.getClass().getSimpleName() + "</b>";
							exch.sendResponseHeaders(500, err.length());
							os.write(err.getBytes(StandardCharsets.UTF_8));
						}
					} else {
						String out = "<h1>403 Forbidden</h1>Invalid URI.";
						exch.sendResponseHeaders(403, out.length());
						os.write(out.getBytes(StandardCharsets.UTF_8));
					}
				} else {
					String out = "<h1>403 Forbidden</h1>Cross-origin request detected.";
					exch.sendResponseHeaders(403, out.length());
					os.write(out.getBytes(StandardCharsets.UTF_8));
				}
				os.close();
				exch.close();
				return;
			}
		}
		String out = "<h1>401 Authorization Required</h1>Please authorize with <a href=\"/auth\">/auth</a> before continuing.";
		exch.sendResponseHeaders(401, out.length());
		os.write(out.getBytes(StandardCharsets.UTF_8));
		os.close();
		exch.close();
	}

	protected final String ifNullThenMakeEmpty(String in) {
		return in == null ? "" : in;
	}
}
