package co.reborncraft.syslogin_banmanager.commands.utils.staff;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.commands.utils.players.PlayersCommand;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StaffsCommand extends Command {
	public StaffsCommand() {
		super("staffs", "", "onlinestaffs", "onlinestaff", "staffonline", "staffsonline");
	}

	public static void sendStaffsOnlineMessage(CommandSender sender) {
		List<String> staffs = SysLogin_BanManager.getInstance().getStaffList().stream()
				.sorted(Comparator.naturalOrder())
				.sorted(String::compareToIgnoreCase)
				.sorted((s1, s2) -> {
					boolean s1LogState = SysLogin_BanManager.getInstance().getGlobalOnlineStaffs().contains(s1);
					return s1LogState == SysLogin_BanManager.getInstance().getGlobalOnlineStaffs().contains(s2) ? 0 : s1LogState ? 1 : -1;
				})
				.collect(Collectors.toList());
		final long soc = staffs.stream().filter(staff -> SysLogin_BanManager.getInstance().getGlobalOnlineStaffs().contains(staff)).count();
		final TextComponent comp = Utils.parseIntoComp("\u00a78Staffs (" +
				"\u00a74" + (staffs.size() - soc) +
				"\u00a78:\u00a72" +
				soc +
				"\u00a78): ");
		staffs.forEach(staffName -> {
			TextComponent staffComp = PlayersCommand.formatDetailedPlayerInfo(staffName, true, true);
			if (SysLogin_BanManager.getInstance().getGlobalOnlineStaffs().contains(staffName)) {
				staffComp.setColor(ChatColor.GREEN);
			} else {
				staffComp.setColor(ChatColor.DARK_GRAY);
			}
			comp.addExtra(staffComp);
		});
		if (ComponentSerializer.toString(comp).length() > 32767) {
			sender.sendMessage(Utils.parseIntoComp(comp.toLegacyText()));
		} else {
			sender.sendMessage(comp);
		}
	}

	public static void sendProtip(CommandSender sender) {
		final TextComponent additionalInfo = Utils.buildTextComponent("", ChatColor.LIGHT_PURPLE);

		final TextComponent proTip = new TextComponent("PROTIP: ");
		proTip.setColor(ChatColor.DARK_PURPLE);
		proTip.setBold(true);
		additionalInfo.addExtra(proTip);

		additionalInfo.addExtra("You can visit our ");

		final TextComponent discordLink = new TextComponent("discord chat");
		discordLink.setColor(ChatColor.GOLD);
		discordLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{Utils.buildTextComponent("Click to goto our discord chat.", ChatColor.YELLOW)}));
		discordLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.reborncraft.co"));
		additionalInfo.addExtra(discordLink);

		additionalInfo.addExtra(" or ");

		final TextComponent siteLink = new TextComponent("site");
		siteLink.setColor(ChatColor.GOLD);
		siteLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{Utils.buildTextComponent("Click to goto our website.", ChatColor.YELLOW)}));
		discordLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://www.reborncraft.site"));
		additionalInfo.addExtra(siteLink);

		additionalInfo.addExtra(" for help at any time! Even when staff are offline.");

		sender.sendMessage(additionalInfo);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		sendStaffsOnlineMessage(sender);
		sendProtip(sender);
	}
}
