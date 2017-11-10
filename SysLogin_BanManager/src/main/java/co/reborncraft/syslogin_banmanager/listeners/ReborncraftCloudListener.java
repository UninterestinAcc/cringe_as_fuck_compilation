package co.reborncraft.syslogin_banmanager.listeners;

import co.reborncraft.cloudcord.api.CloudListener;
import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.objects.Punishment;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReborncraftCloudListener implements CloudListener {
	@Override
	public void onMessageReceive(long messageId, String channel, String subChannel, String data) {
		if (channel.equals("Reborncraft")) {
			if (subChannel.equals("Kick")) {
				String playerName = data.substring(0, data.indexOf("|"));
				ProxiedPlayer player = SysLogin_BanManager.getInstance().getProxy().getPlayer(playerName);
				if (player != null && player.isConnected()) {
					player.disconnect(new Punishment(
							Punishment.PunishmentTarget.USERNAME,
							Punishment.PunishmentType.KICK,
							player.getName(),
							data.substring(data.indexOf("|") + 1, data.indexOf("|", data.indexOf("|", data.indexOf("|") + 1) + 1)),
							System.currentTimeMillis() / 1000,
							0,
							data.substring(data.indexOf("|") + 1, data.indexOf("|", data.indexOf("|") + 1))
					).toKickTextComponent());
				}
			}
		}
	}
}
