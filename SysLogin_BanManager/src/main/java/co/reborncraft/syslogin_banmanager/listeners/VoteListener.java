package co.reborncraft.syslogin_banmanager.listeners;

import co.reborncraft.cloudcord.binds.CloudBungee;
import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.vote.PullVoteDataFuture;
import co.reborncraft.syslogin_banmanager.api.futures.vote.PushVoteIncrementFuture;
import com.vexsoftware.votifier.bungee.events.VotifierEvent;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class VoteListener implements Listener {
	public static void trigger(String player, String site) {
		SysLogin_BanManager.getInstance().scheduleFuture(new PushVoteIncrementFuture(player));
		SysLogin_BanManager.getInstance().getVoteReminderManager().setVoteTime(player, System.currentTimeMillis() / 1000);
		CloudBungee.getInstance().tryVoteSend(player, site);
	}

	@EventHandler
	public void onVote(VotifierEvent e) {
		trigger(e.getVote().getUsername(), e.getVote().getServiceName());
	}

	@EventHandler
	public void onConnected(ServerConnectedEvent e) {
		SysLogin_BanManager.getInstance().scheduleFuture(new PullVoteDataFuture(e.getPlayer().getName(), i -> {
			Server server = e.getPlayer().getServer();
			if (server != null) {
				server.sendData("Vote", ("Sync|" + i.getValue()).getBytes());
			}
		}));
	}
}
