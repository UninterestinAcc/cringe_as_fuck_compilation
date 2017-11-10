package co.reborncraft.syslogin_banmanager.commands.utils.players;

import co.reborncraft.cloudcord.binds.CloudBungee;
import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.objects.Permissions;
import co.reborncraft.syslogin_banmanager.commands.utils.staff.StaffsCommand;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PlayersCommand extends Command {
	private static long lastCache = 0;
	private static List<String> cache;

	public PlayersCommand() {
		super("players", "", "online", "list", "who");
	}

	public static TextComponent formatDetailedPlayerInfo(String player, boolean staff, boolean endWithNewline) {
		if (System.currentTimeMillis() > lastCache + 2000) {
			cache = CloudBungee.getInstance().getGlobalPlayers();
			lastCache = System.currentTimeMillis();
		}
		final TextComponent playerComp = new TextComponent(player + " ");
		playerComp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getPlayerDetailLore(player, staff, endWithNewline).stream().toArray(TextComponent[]::new)));
		return playerComp;
	}

	public static List<TextComponent> getPlayerDetailLore(String player, boolean staff, boolean endWithNewLine) {
		String lineEnding = endWithNewLine ? "\n" : "";
		List<TextComponent> lores = new ArrayList<>();
		lores.add(Utils.parseIntoComp("\u00a7" + (staff ? "3" : "b") + player + lineEnding));
		if (cache.contains(player)) {
			lores.add(Utils.parseIntoComp("\u00a77Ontime: \u00a72" + (((System.currentTimeMillis() - SysLogin_BanManager.getInstance().getJoinTime(player)) / 60000) + 1) + " \u00a77min" + lineEnding));
			lores.add(Utils.parseIntoComp("\u00a77Edge origin: \u00a7e" + SysLogin_BanManager.getInstance().getPlayerEdge(player) + lineEnding));
			lores.add(Utils.parseIntoComp("\u00a77Server: \u00a73" + SysLogin_BanManager.getInstance().getServerOfPlayer(player) + lineEnding));
		}
		if (staff) {
			lores.add(Utils.parseIntoComp("\u00a78Staff Rank: \u00a76" + Permissions.getRank(SysLogin_BanManager.getInstance().getStaffPermissions(player)) + lineEnding));
		}
		return lores;
	}

	public static void showPlayerStats(CommandSender sender, String server) {
		List<String> players = SysLogin_BanManager.getInstance().getPlayersOnServer(server).stream()
				.sorted(Comparator.naturalOrder())
				.sorted(String::compareToIgnoreCase)
				.collect(Collectors.toList());
		boolean hasOnlinePlayers = players.size() > 0;
		final TextComponent comp = Utils.buildTextComponent("", ChatColor.AQUA,
				Utils.buildTextComponent("Players on \u00a7e[\u00a76" + server + "\u00a7e]\u00a77 (", ChatColor.GRAY,
						Utils.buildTextComponent(players.size() + "", hasOnlinePlayers ? ChatColor.GREEN : ChatColor.RED),
						new TextComponent(")" + (hasOnlinePlayers ? ": " : ""))));
		if (hasOnlinePlayers) {
			players.forEach(name -> {
				TextComponent playerComp;
				boolean isStaff = SysLogin_BanManager.getInstance().isStaff(name);
				playerComp = formatDetailedPlayerInfo(name, isStaff, true);
				if (isStaff) {
					playerComp.setColor(ChatColor.DARK_AQUA);
				}
				comp.addExtra(playerComp);
			});
		}
		if (ComponentSerializer.toString(comp).length() > 32767) {
			sender.sendMessage(Utils.parseIntoComp(comp.toLegacyText()));
		} else {
			sender.sendMessage(comp);
		}
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			showPlayerStats(sender, ((ProxiedPlayer) sender).getServer().getInfo().getName());
			StaffsCommand.sendStaffsOnlineMessage(sender);
			StaffsCommand.sendProtip(sender);
		} else {
			sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_PLAYER_ONLY_ERROR));
		}
	}
}
