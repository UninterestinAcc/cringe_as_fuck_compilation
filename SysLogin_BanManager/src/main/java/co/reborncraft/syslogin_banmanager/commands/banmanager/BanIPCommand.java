package co.reborncraft.syslogin_banmanager.commands.banmanager;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.objects.Inet4AddressBlock;
import co.reborncraft.syslogin_banmanager.api.objects.Permissions;
import co.reborncraft.syslogin_banmanager.api.objects.Punishment;
import co.reborncraft.syslogin_banmanager.listeners.EventLogger;
import co.reborncraft.utils.Utils;
import jdk.nashorn.internal.runtime.ParserException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.text.ParseException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class BanIPCommand extends Command {
	public BanIPCommand() {
		super("banip", null, "tempbanip");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (Permissions.permissed(sender, Permissions.ADVANCED_MODERATOR)) {
			if (args.length >= 3) {
				AtomicReference<String> target = new AtomicReference<>(args[0]);
				final String endString = args[1];
				final String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
				long endTime;
				ProxiedPlayer p = SysLogin_BanManager.getInstance().getPlayer(target.get());
				if (p != null) {
					target.set(p.getPendingConnection().getAddress().getAddress().getHostAddress());
				}
				try {
					Inet4AddressBlock i4AB = new Inet4AddressBlock(target.get());// Tests the validity

					if (target.get().matches("^(127|1?0)(\\.\\d{1,3}){3}(/\\d)?$")) {
						sender.sendMessage(Utils.buildTextComponent("Invalid IP address.", ChatColor.RED));
						return;
					}
					if (endString.toLowerCase().matches(SysLogin_BanManager.PERMANENT_REGEX)) {
						endTime = 0;
					} else {
						endTime = System.currentTimeMillis() / 1000 + Utils.parseTime(endString);
					}
					Punishment punishment = new Punishment(
							Punishment.PunishmentTarget.IP,
							Punishment.PunishmentType.BAN,
							target.get(),
							reason,
							System.currentTimeMillis() / 1000,
							endTime,
							sender.getName()
					);
					if (SysLogin_BanManager.getInstance().getPunishmentCache().punish(sender, punishment)) {
						SysLogin_BanManager.getInstance().getProxy().getPlayers().stream()
								.filter(pp -> pp.getPendingConnection().getAddress().getAddress().getHostAddress().equals(target.get()))
								.forEach(pp -> pp.disconnect(punishment.toBanTextComponent()));
						sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_SUCCESS.replaceAll("\\{name}", target.get())));
						EventLogger.logPunishment(punishment);
					} else {
						sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_REJECTED.replaceAll("\\{name}", target.get())));
					}
				} catch (ParserException | NumberFormatException e) {
					sender.sendMessage(Utils.buildTextComponent(e.getClass().getSimpleName() + ": " + e.getMessage(), ChatColor.RED));
				} catch (ParseException e) {
					sender.sendMessage(Utils.buildTextComponent("Unknown/invalid IP address or IP range.", ChatColor.RED));
				}
			} else {
				sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_BANIP_MESSAGE));
			}
		} else {
			sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.PERMISSION_ERROR));
		}
	}
}
