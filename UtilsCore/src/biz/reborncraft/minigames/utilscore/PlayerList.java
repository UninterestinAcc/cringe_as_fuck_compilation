package biz.reborncraft.minigames.utilscore;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class PlayerList implements PluginMessageListener {

	private List<String> staff = new ArrayList<String>();
	private Plugin main = null;

	public PlayerList(Plugin main) {
		this.main = main;
	}

	public List<String> getStaffs() {
		return this.staff;
	}

	public void list(CommandSender sender) {
		String send = "";
		send += ChatColor.GOLD + "[" + ChatColor.AQUA + Bukkit.getServerName() + ChatColor.GOLD + "] Online Players (" + ChatColor.RED + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ChatColor.GOLD + "):\n";
		for (World w : Bukkit.getWorlds()) {
			ArrayList<String> players = new ArrayList<String>();
			for (Player p : w.getPlayers()) {
				players.add(p.getName());
			}
			send += ChatColor.GOLD + "[" + ChatColor.RED + w.getName() + ChatColor.GOLD + "] " + ChatColor.AQUA + ChatColor.BOLD + "> " + ChatColor.YELLOW + String.join(", ", players) + "\n";
		}
		send += "\n" + staffList(sender, true);
		sender.sendMessage(send);
	}

	public String staffList(CommandSender sender, boolean returns) {
		ArrayList<String> staffs = new ArrayList<String>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.hasPermission("utils.isstaff") && (!p.hasPermission("utils.isstaff.hidden") || sender.hasPermission("utils.seehiddenstaff"))) {
				String dString = ChatColor.AQUA + p.getName();
				if (p.hasPermission("utils.isstaff.hidden")) {
					dString += ChatColor.GRAY + "[HIDDEN]";
				}
				ArrayList<String> pl = new ArrayList<String>();
				if (p.isOp()) {
					pl.add(ChatColor.DARK_RED + "OP");
				} else {
					if (p.hasPermission("bm.kick")) {
						pl.add(ChatColor.GREEN + "Kick");
					}
					if (p.hasPermission("bm.tempban")) {
						pl.add(ChatColor.DARK_GREEN + "Ban");
					}
					if (p.hasPermission("bm.unban")) {
						pl.add(ChatColor.DARK_RED + "Unban");
					}
				}
				dString += ChatColor.LIGHT_PURPLE + "[" + String.join(ChatColor.LIGHT_PURPLE + ", ", pl) + ChatColor.LIGHT_PURPLE + "]";
				staffs.add(dString);
			}
		}
		String m = ChatColor.GREEN + "Online" + ChatColor.GOLD + " staffs: ";
		if (staffs.size() < 1) {
			m += ChatColor.LIGHT_PURPLE + "Sorry, there aren't any staff online here in " + Bukkit.getServerName();
		} else {
			m += String.join(ChatColor.DARK_PURPLE + ", " + ChatColor.LIGHT_PURPLE, staffs);
		}
		m += "\n" + ChatColor.AQUA + "Other Staff on RebornCraft: ";
		if (staff.size() < 1) {
			m += ChatColor.LIGHT_PURPLE + "Sorry, there aren't any staff online on the network right now! Our site: http://reborncraft.info";
		} else {
			m += ChatColor.LIGHT_PURPLE + String.join(ChatColor.DARK_PURPLE + ", " + ChatColor.LIGHT_PURPLE, staff);
		}
		if (returns) {
			return m;
		}
		sender.sendMessage(m);
		return null;
	}

	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] bytes) {
		if (channel.equalsIgnoreCase("BungeeCord")) {
			ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
			String subchannel = in.readUTF();
			if (subchannel.equalsIgnoreCase("PlayerList")) {
				in.readUTF(); // Server name param
				String[] onlinePlayers = in.readUTF().split(", ");
				List<String> staffs = new ArrayList<String>();
				for (String pn : onlinePlayers) {
					if (PermissionsEx.getUser(pn).has("utils.isstaff")) {
						staffs.add(pn);
					}
				}
				this.staff = staffs;
			}
		}
	}

	public void requestPlayerlist() {
		try {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("PlayerList");
			out.writeUTF("ALL");
			Player player = (Player) Bukkit.getOnlinePlayers().toArray()[0];
			player.sendPluginMessage(this.main, "BungeeCord", out.toByteArray());
		} catch (ArrayIndexOutOfBoundsException ex) {
		}
	}
}
