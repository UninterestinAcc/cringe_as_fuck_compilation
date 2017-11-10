package org.reborncraft.gtowny;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;
import org.reborncraft.gtowny.cmds.out.MessageFormatter;
import org.reborncraft.gtowny.data.Chunk;
import org.reborncraft.gtowny.data.Town;
import org.reborncraft.gtowny.data.User;
import org.reborncraft.gtowny.data.internal.ChunkType;
import org.reborncraft.gtowny.data.internal.TownOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GTownyScoreboardManager implements Listener {
	private final Map<Player, Scoreboard> scoreboards = new HashMap<>();
	private int sidebarSwitch = 0;

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		updateOrCreateTag(e.getPlayer());
		updateSideBar(e.getPlayer());
	}

	@SuppressWarnings ("deprecation")
	public void updateOrCreateTag(Player p) {
		if (!scoreboards.containsKey(p)) {
			scoreboards.put(p, Bukkit.getScoreboardManager().getNewScoreboard());
		}
		User user = User.forPlayer(p);
		scoreboards.forEach((sbp, sb) -> {
			Team t = sb.getTeam(p.getName());
			if (t == null) {
				t = sb.registerNewTeam(p.getName());
				t.setNameTagVisibility(NameTagVisibility.ALWAYS);
			}
			if (User.forPlayer(sbp).getTownId() == user.getTownId() && user.getTownId() > 0) {
				t.setPrefix(ChatColor.DARK_GREEN + "T. " + GTowny.getTownRankPrefixForPlayer(p));
			} else {
				t.setPrefix(GTowny.getTownPrefixForPlayer(p));
			}
			if (user.isShieldActive()) {
				t.setSuffix(ChatColor.DARK_RED + " " + ChatColor.BOLD + "█");
			} else {
				t.setSuffix(ChatColor.DARK_GREEN + " " + ChatColor.BOLD + "█");
			}
			t.addPlayer(p);
			if (sbp.getScoreboard() != sb) {
				sbp.setScoreboard(sb);
			}
		});
	}

	public void updateScoreboards() {
		garbageClean();
		updateAllTags();
		updateAllSidebars();
	}

	public void garbageClean() {
		scoreboards.keySet().stream().filter(p -> !p.isOnline()).collect(Collectors.toList()).forEach(scoreboards::remove);
	}

	public void updateAllSidebars() {
		sidebarSwitch++;
		scoreboards.keySet().forEach(this::updateSideBar);
	}

	public void updateSideBar(Player p) {
		if (!scoreboards.containsKey(p)) {
			scoreboards.put(p, Bukkit.getScoreboardManager().getNewScoreboard());
		}
		Scoreboard sb = scoreboards.get(p);
		User user = User.forPlayer(p);
		Town town = user.getTown();
		Chunk onChunk = user.getCurrentChunk();
		Town onTown = onChunk.getTown();
		String name = "Sidebar_" + (System.currentTimeMillis() % 60000);
		Objective ob = sb.getObjective(name);
		if (ob != null) {
			ob.unregister();
		}
		ob = sb.registerNewObjective(name, "dummy");
		ob.setDisplayName(MessageFormatter.sbDisplayflowText("Reborncraft Towny", sidebarSwitch, ChatColor.GOLD, ChatColor.BOLD));
		int i = 99;
		ob.getScore(ChatColor.GOLD + "Town: " + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + town.getName() + ChatColor.DARK_GREEN + "]").setScore(i--);
		if (town.getId() > 0) {
			ob.getScore(ChatColor.GOLD + "Town Rank: " + ChatColor.YELLOW + user.getRank().getName()).setScore(i--);
		}
		ob.getScore(ChatColor.GOLD + "Chunk: " + MessageFormatter.getTownName(onTown)).setScore(i--);
		ob.getScore("         " + MessageFormatter.getPvPString(onTown.getOptions().contains(TownOptions.PvP))).setScore(i--);
		if (onChunk.canBuild(user)) {
			ob.getScore(ChatColor.LIGHT_PURPLE + "         [Can build]").setScore(i--);
		}
		if (onTown.getId() > 0 && onChunk.getType() != ChunkType.Normal) {
			ob.getScore(ChatColor.BLUE + "         <" + onChunk.getType() + ">").setScore(i--);
		}
		if (onChunk.getOwnerId() > 0) {
			ob.getScore(ChatColor.AQUA + "         [" + onChunk.getOwner().getName() + "]").setScore(i--);
		}
		if (onChunk.getSalePrice() > -1) {
			ob.getScore(ChatColor.GREEN + "         Selling @ $" + onChunk.getSalePrice()).setScore(i--);
		}
		ob.getScore(ChatColor.GOLD + "Shield: " + (user.isShieldActive() ? ChatColor.AQUA + MessageFormatter.millisToHMS(user.getShieldLastsUntil() - System.currentTimeMillis()) : (sidebarSwitch % 2 == 1 ? ChatColor.RED : ChatColor.GRAY) + "Deactivated")).setScore(i--);
		ob.setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	public void updateAllTags() {
		Bukkit.getOnlinePlayers().forEach(this::updateOrCreateTag);
	}
}
