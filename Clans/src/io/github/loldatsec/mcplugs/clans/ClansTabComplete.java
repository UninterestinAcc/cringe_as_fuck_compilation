package io.github.loldatsec.mcplugs.clans;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class ClansTabComplete implements TabCompleter {
	public Main main = null;

	public ClansTabComplete(Main p) {
		main = p;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd,
			String label, String[] args) {
		List<String> ret = new ArrayList<String>();
		if (args.length >= 2) {
			String opt = args[0];
			try {
				if (opt.isEmpty()) {
					ret.addAll(subcommands());
				} else if (opt.equalsIgnoreCase("list")
						|| opt.equalsIgnoreCase("ls")) {
					for (int c = 1; c <= Math.ceil(main.listClans().size() / 6); c++) {
						ret.add(c + "");
					}
				} else if (opt.equalsIgnoreCase("info")
						|| opt.equalsIgnoreCase("i")
						|| opt.equalsIgnoreCase("delete")
						|| opt.equalsIgnoreCase("join")
						|| opt.equalsIgnoreCase("j")) {
					ret.addAll(main.listClans());
				} else if (opt.equalsIgnoreCase("ally")) {
					List<String> p = new ArrayList<String>();
					p.addAll(main.listClans());
					p.removeAll(main.getAlliedClans(main.getClan(sender
							.getName())));
					ret.addAll(p);
				} else if (opt.equalsIgnoreCase("unally")) {
					ret.addAll(main.getAlliedClans(main.getClan(sender
							.getName())));
				} else if (opt.equalsIgnoreCase("who")) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						ret.add(p.getName());
					}
				} else if (opt.equalsIgnoreCase("promote")) {
					ret.addAll(main.getClanUnrankedMembers(main.getClan(sender
							.getName())));
				} else if (opt.equalsIgnoreCase("demote")) {
					ret.addAll(main.getClanMods(main.getClan(sender.getName())));
				} else if (opt.equalsIgnoreCase("add")
						|| opt.equalsIgnoreCase("a")
						|| opt.equalsIgnoreCase("invite")) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						ret.add(p.getName());
					}
					ret.removeAll(main.getClanMembers(main.getClan(sender
							.getName())));
				} else if (opt.equalsIgnoreCase("kick")
						|| opt.equalsIgnoreCase("k")) {
					ret.addAll(main.getClanMembers(main.getClan(sender
							.getName())));
				} else if (opt.equalsIgnoreCase("chat")
						|| opt.equalsIgnoreCase("c")) {
					ret.add("clan");
					ret.add("ally");
					ret.add("public");
				}
			} catch (NullPointerException npe) {
			}
			List<String> nr = new ArrayList<String>();
			for (String r : ret) {
				if (r.toLowerCase().startsWith(args[1].toLowerCase())) {
					nr.add(r);
				}
			}
			ret = nr;
		} else {
			ret.addAll(subcommands());
			List<String> nr = new ArrayList<String>();
			for (String r : ret) {
				if (r.toLowerCase().startsWith(args[0].toLowerCase())) {
					nr.add(r);
				}
			}
			ret = nr;
		}
		return ret;
	}

	public List<String> subcommands() {
		List<String> ret = new ArrayList<String>();
		ret.add("a");
		ret.add("c");
		ret.add("i");
		ret.add("j");
		ret.add("k");
		ret.add("l");
		ret.add("n");
		ret.add("ls");
		ret.add("add");
		ret.add("new");
		ret.add("who");
		ret.add("ally");
		ret.add("chat");
		ret.add("help");
		ret.add("info");
		ret.add("join");
		ret.add("kick");
		ret.add("list");
		ret.add("clean");
		ret.add("leave");
		ret.add("create");
		ret.add("delete");
		ret.add("demote");
		ret.add("invite");
		ret.add("rename");
		ret.add("unally");
		ret.add("disband");
		ret.add("promote");
		return ret;
	}
}
