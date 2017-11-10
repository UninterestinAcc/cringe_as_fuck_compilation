package co.reborncraft.syslogin_banmanager.commands.login;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.login.AltCheckFuture;
import co.reborncraft.syslogin_banmanager.api.objects.Permissions;
import co.reborncraft.syslogin_banmanager.api.responses.states.AltCheckState;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class AltCheckCommand extends Command {
	public AltCheckCommand() {
		super("altcheck");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (Permissions.permissed(sender, Permissions.ADMIN)) {
			if (args.length >= 1) {
				sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_ALTCHECK_STARTING.replaceAll("\\{name}", args[0])));
				SysLogin_BanManager.getInstance().scheduleFuture(new AltCheckFuture(sender, args[0], result -> {
					if (result.getState() == AltCheckState.ALTS_FOUND) {
						if (args.length >= 2) {
							if (args[1].toLowerCase().startsWith("ip")) {
								showIPAlts(sender, result);
							} else if (args[1].toLowerCase().startsWith("pass")) {
								showPassAlts(sender, result);
							}
						} else {
							showIPAlts(sender, result);
							showPassAlts(sender, result);
							sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_ALTCHECK_COLLAPSE_MESSAGE));
						}
					} else {
						sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.LOGIN_PREFIX + " " + result.getState().getMessage()));
					}
				}));
			} else {
				sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_ALTCHECK_MESSAGE));
			}
		} else {
			sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.PERMISSION_ERROR));
		}
	}

	private void showIPAlts(CommandSender sender, AltCheckFuture.AltCheckResult result) {
		final TextComponent foundIPAlts = new TextComponent(SysLogin_BanManager.LOGIN_PREFIX + " ");
		final Map<String, List<String>> ipAlts = result.getIpAlts();
		foundIPAlts.addExtra(new TextComponent("\u00a7aIP-Based Alt Check: "));
		ipAlts.keySet().stream()
				.sorted(Comparator.naturalOrder())
				.sorted(String::compareToIgnoreCase)
				.sorted(Comparator.comparingInt(alt -> ipAlts.get(alt).size()))
				.map(alt -> {
					final List<String> ips = ipAlts.get(alt);
					TextComponent comp = new TextComponent(alt);
					comp.setColor(ChatColor.LIGHT_PURPLE);
					TextComponent ipCountComp = new TextComponent("[" + ips.size() + "] ");
					ipCountComp.setColor(ChatColor.YELLOW);
					comp.addExtra(ipCountComp);

					comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, ips.parallelStream()
							.map(Utils::loadASNList)
							.flatMap(Arrays::stream)
							.distinct()
							.map(asn -> {
								TextComponent ispComp = new TextComponent("AS" + asn + ": " + Utils.loadASNName(asn) + "\n");
								ispComp.setColor(ChatColor.GOLD);
								return ispComp;
							}).toArray(TextComponent[]::new))
					);
					comp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/userinfo " + alt));

					return comp;
				}).forEach(foundIPAlts::addExtra);
		sender.sendMessage(foundIPAlts);
	}

	private void showPassAlts(CommandSender sender, AltCheckFuture.AltCheckResult result) {
		final TextComponent foundPasswordAlts = new TextComponent(SysLogin_BanManager.LOGIN_PREFIX + " ");
		final List<String> passAlts = result.getPassAlts();
		foundPasswordAlts.addExtra(new TextComponent("\u00a7aPassword-Based Alt Check: "));
		passAlts.stream().map(alt -> {
			TextComponent comp = new TextComponent(alt + " ");
			comp.setColor(ChatColor.LIGHT_PURPLE);
			comp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/userinfo " + alt));
			return comp;
		}).forEach(foundPasswordAlts::addExtra);
		sender.sendMessage(foundPasswordAlts);
	}
}
