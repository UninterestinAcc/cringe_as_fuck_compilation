package co.reborncraft.stackperms.http;

import co.reborncraft.stackperms.StackPerms;
import co.reborncraft.stackperms.http.containers.RootContainer;
import co.reborncraft.stackperms.http.containers.modifierservers.GroupServerContainer;
import co.reborncraft.stackperms.http.containers.modifierservers.PlayerServerContainer;
import co.reborncraft.stackperms.http.containers.modifierservers.StackServerContainer;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.*;

public final class StackPermsHTTPServerlet {
	private final String serverletAuthCode;
	private HttpServer httpServer;
	private InetSocketAddress bindingAddr = null;
	private String authIP;

	public StackPermsHTTPServerlet() throws IOException {
		bindingAddr = new InetSocketAddress(StackPerms.getInstance().getConfig().getString("bind-addr"), (int) (50000 + (Math.random() * 10000)));

		byte[] b = new byte[48];
		new Random().nextBytes(b);
		serverletAuthCode = Base64.getEncoder().encodeToString(b).replaceAll("[^0-9a-zA-Z]", "");
		httpServer = HttpServer.create(bindingAddr, 0);
		httpServer.createContext("/auth", new AuthController(this));
		httpServer.createContext("/stack", new StackServerContainer(this));
		httpServer.createContext("/group", new GroupServerContainer(this));
		httpServer.createContext("/player", new PlayerServerContainer(this));
		httpServer.createContext("/reload", new ReloadContainer(this));
		httpServer.createContext("/", new RootContainer(this));
		httpServer.setExecutor(null);
		httpServer.start();
	}

	public static String readInputStreamAsString(InputStream is) {
		try (Scanner s = (new Scanner(is)).useDelimiter("\\A")) {
			return s.hasNext() ? s.next() : "";
		} catch (Throwable e) {
			throw e;
		}
	}

	public static Map<String, List<String>> readPOSTUriIntoMap(InputStream postContent) {
		String[] postStr = readInputStreamAsString(postContent).split("&");
		Map<String, List<String>> map = new HashMap<>();
		for (String s : postStr) {
			String fieldName = s.split("=")[0];
			if (fieldName.length() + 1 > s.length()) continue;
			List<String> list;
			if (map.containsKey(fieldName)) {
				list = map.get(fieldName);
			} else {
				list = new ArrayList<>();
				map.put(fieldName, list);
			}
			try {
				list.add(URLDecoder.decode(s.substring(fieldName.length() + 1), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	public InetSocketAddress getBindingAddr() {
		return bindingAddr;
	}

	public void destroy() {
		httpServer.stop(0);
	}

	public String getAuthIP() {
		return authIP;
	}

	public void setAuthIP(String authIP) {
		this.authIP = authIP;
	}

	public String getServerletAuthCode() {
		return serverletAuthCode;
	}
}
