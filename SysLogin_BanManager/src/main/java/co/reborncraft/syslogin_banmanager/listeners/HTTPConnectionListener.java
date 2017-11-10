package co.reborncraft.syslogin_banmanager.listeners;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HTTPConnectionListener implements Closeable {
	private ServerSocket ss;

	public HTTPConnectionListener() {
		try {
			ss = new ServerSocket(80);
			final Plugin pl = SysLogin_BanManager.getInstance();
			final TaskScheduler sched = pl.getProxy().getScheduler();
			sched.runAsync(pl, () -> {
				while (true) {
					try {
						Socket sock = ss.accept();
						synchronized (pl.getProxy()) {
							pl.getProxy().getLogger().info("[" + sock.getRemoteSocketAddress() + "] <-> HTTP Edge redirector connected");
						}
						sched.runAsync(pl, () -> {
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
						});
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws IOException {
		if (ss != null && !ss.isClosed()) {
			ss.close();
		}
	}
}
