package co.reborncraft.syslogin_banmanager.commands.banmanager;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.objects.Permissions;
import co.reborncraft.syslogin_banmanager.api.objects.Punishment;
import co.reborncraft.syslogin_banmanager.listeners.EventLogger;
import co.reborncraft.utils.Utils;
import jdk.nashorn.internal.runtime.ParserException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class BanISPCommand extends Command {
	public BanISPCommand() {
		super("banisp", null, "tempbanisp");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (Permissions.permissed(sender, Permissions.ADVANCED_MODERATOR)) {
			if (args.length >= 3) {
				String targetIP = args[0];
				final String endString = args[1];
				final String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
				long endTime;
				ProxiedPlayer p = SysLogin_BanManager.getInstance().getPlayer(targetIP);
				if (p != null) {
					targetIP = p.getPendingConnection().getAddress().getAddress().getHostAddress();
				}
				try {
					if (endString.toLowerCase().matches(SysLogin_BanManager.PERMANENT_REGEX)) {
						endTime = 0;
					} else {
						endTime = System.currentTimeMillis() / 1000 + Utils.parseTime(endString);
					}
					Integer[] isp;
					if (targetIP.matches("(as)?\\d+")) {
						isp = new Integer[]{Integer.parseUnsignedInt(targetIP.replaceAll("(as)?(\\d+)", "$2"))};
					} else {
						isp = Utils.loadASNList(InetAddress.getByName(targetIP));
					}
					if (isp.length != 0) {
						for (int asn : isp) {
							if (asn != 0) {
								Punishment punishment = new Punishment(
										Punishment.PunishmentTarget.ISP,
										Punishment.PunishmentType.BAN,
										asn + "",
										reason,
										System.currentTimeMillis() / 1000,
										endTime,
										sender.getName()
								);
								String ispString = "AS" + asn + ": " + Utils.loadASNName(asn);
								if (SysLogin_BanManager.getInstance().getPunishmentCache().punish(sender, punishment)) {
									sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_SUCCESS.replaceAll("\\{name}", ispString)));
									EventLogger.logPunishment(punishment);
								} else {
									sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_REJECTED.replaceAll("\\{name}", ispString)));
								}
							}
						}
					} else {
						sender.sendMessage(Utils.buildTextComponent("Invalid ISP.", ChatColor.RED));
					}
				} catch (UnknownHostException e) {
					sender.sendMessage(Utils.buildTextComponent("Unknown/invalid IP address.", ChatColor.RED));
				} catch (ParserException | NumberFormatException e) {
					sender.sendMessage(Utils.buildTextComponent(e.getClass().getSimpleName() + ": " + e.getMessage(), ChatColor.RED));
				}
			} else {
				sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_BANISP_MESSAGE));
			}
		} else {
			sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.PERMISSION_ERROR));
		}
	}
}
