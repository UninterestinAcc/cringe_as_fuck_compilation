package co.reborncraft.syslogin_banmanager.test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HTTPListener {
	public static void main(String[] args) throws IOException {
		ServerSocket ss = new ServerSocket(80);
		new Thread(() -> {
			while (true) {
				try {
					Socket sock = ss.accept();
					new Thread(() -> {
						try {
							sock.getOutputStream().write(("HTTP/1.1 301 Moved Permanently\n" +
									"Date: " + new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z").format(new Date()) + "\n" +
									"Location: https://www.reborncraft.co\n" +
									"Server: Reborncraft Edge Redirector (Stripped HTTP server only capable of returning this message)\n" +
									"Content-Length: 0\n"
							).getBytes(StandardCharsets.UTF_8));
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							try {
								sock.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}