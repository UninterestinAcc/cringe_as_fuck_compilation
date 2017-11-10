package io.github.loldatsec.mcplugs.clans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {

	public Map<String, Map<String, List<String>>> clans = new HashMap<String, Map<String, List<String>>>();
	public Map<String, List<String>> pendingInvites = new HashMap<String, List<String>>();
	public List<String> clanChatter = new ArrayList<String>();
	public List<String> allyChatter = new ArrayList<String>();
	public boolean clanWars = false;
	public Map<String, Integer> cwStats = new HashMap<String, Integer>();

	@SuppressWarnings("unchecked")
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		getConfig().options().copyDefaults(true);
		saveConfig();
		if (!getConfig().contains("clans")) {
			getConfig().createSection("clans", new HashMap<String, Map<String, List<String>>>());
			saveConfig();
		} else {
			try {
				clans = (Map<String, Map<String, List<String>>>) getConfig().getMapList("clans").get(0);
			} catch (ClassCastException cce) {
				System.out.println("Failed to load clans config file...");
				System.out.println("Shutting down Clans...");
				Bukkit.getPluginManager().disablePlugin(this);
			}
		}
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			public void run() {
				// Run code 10 minutes
				pendingInvites = new HashMap<String, List<String>>();
				List<Map<String, Map<String, List<String>>>> a = new ArrayList<Map<String, Map<String, List<String>>>>();
				a.add(clans);
				if (!a.isEmpty()) {
					getConfig().set("clans", a);
				} else {
					System.out.println("Failed to save clans data because there isn't any.");
				}
				saveConfig();
			}
		}, 0, 12000);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			public void run() {
				// Run code 10 seconds
				updateTabCompleter();
			}
		}, 0, 200);
	}

	public void onDisable() {
		List<Map<String, Map<String, List<String>>>> a = new ArrayList<Map<String, Map<String, List<String>>>>();
		a.add(clans);
		if (!a.isEmpty()) {
			getConfig().set("clans", a);
			saveConfig();
		} else {
			System.out.println("Failed to save clans data because there isn't any.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("cw")) {
			if (args.length >= 1) {
				String opt = args[0];
				if (opt.equalsIgnoreCase("start")) {
					if (sender.hasPermission("clans.startclanwars")) {
						scheduleClanWars(1200);
					} else {
						sender.sendMessage("§cNo permission.");
					}
					return true;
				} else if (opt.equalsIgnoreCase("stop")) {
					if (sender.hasPermission("clans.startclanwars")) {
						scheduleEndClanWars(1200);
					} else {
						sender.sendMessage("§cNo permission.");
					}
					return true;
				} else if (opt.equalsIgnoreCase("s") || opt.equalsIgnoreCase("stat") || opt.equalsIgnoreCase("stats") || opt.equalsIgnoreCase("info") || opt.equalsIgnoreCase("i")) {
					sender.sendMessage("§e-------------- §6RcClanWars Status §e--------------");
					sender.sendMessage("§eRunning: §a" + clanWars);
					sender.sendMessage("§eClans killstats: §c" + cwStats.toString());
					return true;
				}
			}
			sender.sendMessage("§e-------------- §6RcClanWars Usage §e--------------");
			sender.sendMessage("§eCommand: §6/cw");
			sender.sendMessage("§6Subcommands:");
			sender.sendMessage("§c - start");
			sender.sendMessage("§c - stop");
			sender.sendMessage("§b - stats|stat|s|info|i");
			return true;
		} else if (cmd.getName().equalsIgnoreCase("c") || cmd.getName().equalsIgnoreCase("clan") || cmd.getName().equalsIgnoreCase("clans")) {
			if (args.length >= 1) {
				String opt = args[0];
				if (opt.equalsIgnoreCase("clean")) {
					if (sender.hasPermission("clans.clean")) {
						List<String> removal = new ArrayList<String>();
						for (String cname : listClans()) {
							if (getClanMembers(cname).size() <= 3) {
								removal.add(cname);
							} else if (!isValidString(cname)) {
								removal.add(cname);
							}
						}
						for (String cname : removal) {
							clans.remove(cname);
						}
						int remAlly = 0;
						for (String cname : listClans()) {
							List<String> delAlly = new ArrayList<String>();
							for (String aname : getAlliedClans(cname)) {
								if (getClanOwner(aname).isEmpty()) {
									delAlly.add(aname);
									remAlly++;
								}
							}
							for (String aname : delAlly) {
								clans.get(cname).get("Ally").remove(aname);
							}
						}
						sender.sendMessage("§6Deleted §e" + removal.size() + " §6clans and §e" + remAlly + " §6alliances.");
					} else {
						sender.sendMessage("§eYou do not have permission.");
					}
					return true;
				} else if (opt.equalsIgnoreCase("disband")) {
					String cname = getClan(sender.getName());
					if (getClanOwner(cname).contains(sender.getName())) {
						clans.remove(cname);
						for (String ac : listClans()) {
							if (getAlliedClans(ac).contains(cname)) {
								clans.get(ac).get("Ally").remove(cname);
							}
						}
						sender.sendMessage("§eYour clan §6" + cname + " §ehas been disbanded.");
						return true;
					} else if (!getClanOwner(cname).contains(sender.getName())) {
						sender.sendMessage("§cYou are not a owner of your clan.");
						return true;
					}
					sender.sendMessage("§cYou are not a part of any clan.");
					return true;
				} else if (opt.equalsIgnoreCase("leave") || opt.equalsIgnoreCase("l")) {
					String cname = getClan(sender.getName());
					if (getClanOwner(cname).contains(sender.getName())) {
						sender.sendMessage("§cYou cant leave your own clan, disband!.");
					} else {
						if (getClanUnrankedMembers(cname).contains(sender.getName())) {
							clans.get(cname).get("Member").remove(sender.getName());
							sender.sendMessage("§cYou left §4" + cname + "§c.");
						} else if (getClanMods(cname).contains(sender.getName())) {
							clans.get(cname).get("Mod").remove(sender.getName());
							sender.sendMessage("§cYou left §4" + cname + "§c.");
						} else {
							sender.sendMessage("§cYou are not a member of any clan.");
						}
					}
					return true;
				} else if (args.length >= 2) {
					if (opt.equalsIgnoreCase("list") || opt.equalsIgnoreCase("ls")) {
						try {
							int pg = Integer.parseInt(args[1]);
							int total = (int) Math.ceil(((float) listClans().size() / 6));
							List<String> cl = sortedListClans();
							sender.sendMessage("§e-------------- §6RcClans List §6(§a" + pg + "/" + total + "§6) §e--------------");
							if (pg <= total && pg > 0) {
								for (int cn = (pg - 1) * 6; (cn < pg * 6) && cn < listClans().size(); cn++) {
									String cname = cl.get(cn);
									sender.sendMessage("§6" + cname + "§e: §6(§a" + getOnline(getClanMembers(cname)).size() + "§b[" + getOnline(getClanMods(cname)).size() + "]§6/§c" + getClanMembers(cname).size() + "§6)");
								}
								if (pg < total) {
									sender.sendMessage("§e---------- §6Do §d/clans list " + (pg + 1) + " §6for next page. §e----------");
								} else {
									sender.sendMessage("§e-------------- §6Last page reached. §e--------------");
								}
							} else {
								sender.sendMessage("§e-------------- §6Page was not found. §e--------------");
							}
						} catch (NumberFormatException nfex) {
							sender.sendMessage("§cError: §4Failed to parse agrument 2 to a integer.");
						}
						return true;
					} else if (opt.equalsIgnoreCase("delete")) {
						if (sender.hasPermission("clans.delete")) {
							for (String cname : listClans()) {
								if (cname.equalsIgnoreCase(args[1])) {
									clans.remove(cname);
									for (String ac : listClans()) {
										if (getAlliedClans(ac).contains(cname)) {
											clans.get(ac).get("Ally").remove(cname);
										}
									}
									sender.sendMessage("§6" + cname + " §ehas been disbanded.");
									return true;
								}
							}
							sender.sendMessage("§cThat clan does not exist.");
						} else {
							sender.sendMessage("§cNo permission.");
						}
						return true;
					} else if (opt.equalsIgnoreCase("new") || opt.equalsIgnoreCase("n") || opt.equalsIgnoreCase("create")) {
						if (sender.hasPermission("clans.create")) {
							if (!isValidString(args[1])) {
								sender.sendMessage("§cThe clan name is not valid.");
							} else {
								for (String cname : listClans()) {
									if (cname.equalsIgnoreCase(args[1])) {
										sender.sendMessage("§cA clan called §4" + cname + "§c already exists.");
										return true;
									} else if (getClanMembers(cname).contains(sender.getName())) {
										sender.sendMessage("§cYou are already in §4" + cname + " §c.");
										return true;
									} else if (getClanOwner(cname).contains(sender.getName())) {
										sender.sendMessage("§cYou already own a clan.");
										return true;
									}
								}
								Map<String, List<String>> a = new HashMap<String, List<String>>();
								List<String> b = new ArrayList<String>();
								b.add(sender.getName());
								a.put("Owner", b);
								a.put("Mod", new ArrayList<String>());
								a.put("Member", new ArrayList<String>());
								a.put("Ally", new ArrayList<String>());
								a.put("OwnerLastOnline", new ArrayList<String>());
								clans.putIfAbsent(args[1], a);
								sender.sendMessage("§eYou created a clan called §6" + args[1] + "§e.");
							}
						} else {
							sender.sendMessage("§cYou do not have permission to create a clan.");
						}
						return true;
					} else if (opt.equalsIgnoreCase("info") || opt.equalsIgnoreCase("i")) {
						for (String cname : listClans()) {
							if (cname.equalsIgnoreCase(args[1])) {
								List<String> cmods = getClanMods(cname);
								try {
									for (String cowner : getClanOwner(cname)) {
										cmods.remove(cowner);
									}
								} catch (NullPointerException npe) {
								}
								sender.sendMessage("§e-------------- §6RcClans §e--------------");
								sender.sendMessage("§eClan name: §6" + cname);
								sender.sendMessage("§eClan owner: §e**" + String.join(", **", getClanOwner(cname)));
								try {
									sender.sendMessage("§eClan moderators: §b*" + String.join(", *", cmods));
								} catch (NullPointerException npe) {
								}
								sender.sendMessage("§eAllies: §d" + getAlliedClans(cname).toString());
								try {
									sender.sendMessage("§eOnline clan Member: §a" + String.join(", ", getOnline(getClanMembers(cname))));
								} catch (NullPointerException npe) {
								}
								try {
									List<String> cn = getClanMembers(cname);
									cn.removeAll(getOnline(getClanMembers(cname)));
									sender.sendMessage("§eOffline clan Member: §c" + String.join(", ", cn));
								} catch (NullPointerException npe) {
								}
								return true;
							}
						}
						sender.sendMessage("§cClan §4" + args[1] + "§c not found.");
						return true;
					} else if (opt.equalsIgnoreCase("rename")) {
						for (String cname : listClans()) {
							if (cname.equalsIgnoreCase(args[1])) {
								sender.sendMessage("§cA clan called §4" + cname + "§c already exists.");
								return true;
							}
						}
						String cname = getClan(sender.getName());
						if (cname == null) {
							sender.sendMessage("§cYou are not a part of any clan.");
						} else if (getClanOwner(cname).contains(sender.getName())) {
							if (!isValidString(args[1])) {
								sender.sendMessage("§cThe clan name is not valid.");
							} else {
								clans.put(args[1], clans.get(cname));
								clans.remove(cname);
								for (String ac : clans.keySet()) {
									try {
										if (getAlliedClans(ac).contains(cname)) {
											clans.get(ac).get("Ally").remove(cname);
											clans.get(ac).get("Ally").add(args[1]);
										}
									} catch (NullPointerException e) {
									}
								}
								sender.sendMessage("§eYour clan has been renamed to §6" + args[1] + "§e.");
							}
						} else {
							sender.sendMessage("§cYou are not a owner of your clan.");
						}
						return true;
					} else if (opt.equalsIgnoreCase("chat") || opt.equalsIgnoreCase("c")) {
						try {
							allyChatter.remove(sender.getName());
						} catch (NullPointerException npe) {
						}
						try {
							clanChatter.remove(sender.getName());
						} catch (NullPointerException npe) {
						}
						if (args[1].equalsIgnoreCase("ally") || args[1].equalsIgnoreCase("allies") || args[1].equalsIgnoreCase("a")) {
							allyChatter.add(sender.getName());
							sender.sendMessage("§eNow talking in §dallies §echat.");
						} else if (args[1].equalsIgnoreCase("clan") || args[1].equalsIgnoreCase("clans") || args[1].equalsIgnoreCase("c")) {
							clanChatter.add(sender.getName());
							sender.sendMessage("§eNow talking in §bclan §echat.");
						} else {
							sender.sendMessage("§eNow talking in §apublic §echat.");
						}
						return true;
					} else if (opt.equalsIgnoreCase("add") || opt.equalsIgnoreCase("a") || opt.equalsIgnoreCase("invite")) {
						String cname = getClan(sender.getName());
						if (cname == null) {
							sender.sendMessage("§cYou are not in any clan.");
						} else {
							List<String> Member = getClanMembers(cname);
							if (getClanMods(cname).contains(sender.getName())) {
								Player tgt = Bukkit.getPlayer(args[1]);
								if (tgt instanceof Player) {
									if (Member.contains(tgt.getName())) {
										sender.sendMessage("§cThat player is already in your clan.");
									} else {
										for (String pname : Member) {
											if (Bukkit.getPlayerExact(pname) instanceof Player) {
												Bukkit.getPlayerExact(pname).sendMessage("§2" + sender.getName() + "§e invited §2" + tgt.getName() + " §eto §6" + cname + "§e.");
											}
										}
										tgt.sendMessage("§2" + sender.getName() + "§e invited you to to §6" + cname + "§e. §d/clan join " + cname + "§e to accept the invite.");
										List<String> a = new ArrayList<String>();
										a.add(tgt.getName());
										if (pendingInvites.get(cname) != null) {
											a.addAll(pendingInvites.get(cname));
										}
										pendingInvites.put(cname, a);
									}
								} else {
									sender.sendMessage("§cThat player is not online.");
								}
							} else {
								sender.sendMessage("§cYou are not a moderator of your clan.");
							}
						}
						return true;
					} else if (opt.equalsIgnoreCase("join") || opt.equalsIgnoreCase("j")) {
						for (String cname : listClans()) {
							try {
								if (getClanMembers(cname).contains(sender.getName())) {
									sender.sendMessage("§cYou are already in a clan.");
									return true;
								}
							} catch (NullPointerException npe) {
							}
						}
						for (String in : pendingInvites.keySet()) {
							if (in.equalsIgnoreCase(args[1])) {
								if (pendingInvites.get(in).contains(sender.getName())) {
									if (!clans.get(in).containsKey("Member")) {
										clans.get(in).put("Member", new ArrayList<String>());
									}
									clans.get(in).get("Member").add(sender.getName());
									pendingInvites.get(in).remove(sender.getName());
									sender.sendMessage("§eYou joined §6" + in + "§e.");
									try {
										for (String o : clans.get(in).get("Owner")) {
											Bukkit.getPlayerExact(o).sendMessage("§2" + sender.getName() + " §ejoined your clan.");
										}
										for (String m : clans.get(in).get("Member")) {
											Bukkit.getPlayerExact(m).sendMessage("§2" + sender.getName() + " §ejoined your clan.");
										}
									} catch (NullPointerException npe) {
									}
									return true;
								}
							}
						}
						sender.sendMessage("§cYou are not invited to this clan / clan does not exist.");
						return true;
					} else if (opt.equalsIgnoreCase("ally")) {
						String cname = getClan(sender.getName());
						if (getClanMods(cname).contains(sender.getName())) {
							if (!args[1].equalsIgnoreCase(cname)) {
								for (String cn : listClans()) {
									if (cn.equalsIgnoreCase(args[1])) {
										if (!clans.get(cname).containsKey("Ally")) {
											clans.get(cname).put("Ally", new ArrayList<String>());
										}
										clans.get(cname).get("Ally").add(cn);
										sender.sendMessage("§eYou allied §6" + cn + "§e.");
										for (String pn : getClanMembers(cn)) {
											try {
												Bukkit.getPlayerExact(pn).sendMessage("§6" + cname + "§e allied you.");
											} catch (NullPointerException npe) {
											}
										}
										return true;
									}
								}
								sender.sendMessage("§cClan not found.");
								return true;
							} else {
								sender.sendMessage("§cYou can't ally yourself.");
							}
							return true;
						} else {
							sender.sendMessage("§cYou are not a moderator of your clan.");
						}
						return true;
					} else if (opt.equalsIgnoreCase("unally")) {
						String cname = getClan(sender.getName());
						if (getClanMods(cname).contains(sender.getName())) {
							if (args[1].equalsIgnoreCase(cname)) {
								sender.sendMessage("§cYou can't unally yourself.");
								return true;
							} else {
								try {
									for (String tgtc : getAlliedClans(cname)) {
										if (tgtc.equalsIgnoreCase(args[1])) {
											clans.get(cname).get("Ally").remove(tgtc);
											sender.sendMessage("§eYou unallied §6" + tgtc + "§e.");
											for (String pn : getClanMembers(tgtc)) {
												try {
													Bukkit.getPlayerExact(pn).sendMessage("§6" + cname + "§e unallied you.");
												} catch (NullPointerException npe) {
												}
											}
											return true;
										}
									}
								} catch (NullPointerException npe) {
								}
								sender.sendMessage("§cYou are not allied with that clan.");
							}
						} else {
							sender.sendMessage("§cYou are not a moderator of your clan.");
						}
						return true;
					} else if (opt.equalsIgnoreCase("kick") || opt.equalsIgnoreCase("k")) {
						String cname = getClan(sender.getName());
						if (getClanMods(cname).contains(sender.getName())) {
							if (getClanUnrankedMembers(cname).contains(args[1])) {
								clans.get(cname).get("Member").remove(args[1]);
								sender.sendMessage("§cYou kicked §4" + args[1] + "§c.");
								if (Bukkit.getPlayerExact(args[1]) instanceof Player) {
									Bukkit.getPlayerExact(args[1]).sendMessage("§cYou have been kicked from §4" + cname + "§c.");
								}
							} else {
								sender.sendMessage("§4" + args[1] + "§c is not kickable.");
							}
						} else {
							sender.sendMessage("§cYou are not a moderator of your clan.");
						}
						return true;
					} else if (opt.equalsIgnoreCase("who")) {
						try {
							String pn = Bukkit.getPlayer(args[1]).getName();
							for (String cname : listClans()) {
								if (getClanMembers(cname).contains(pn)) {
									Bukkit.dispatchCommand(sender, "clans info " + cname);
									return true;
								}
							}
							sender.sendMessage("§6" + pn + " §edoes not belong to a clan.");
						} catch (NullPointerException npe) {
							for (String cname : listClans()) {
								if (getClanMembers(cname).contains(args[1])) {
									Bukkit.dispatchCommand(sender, "clans info " + cname);
									return true;
								}
							}
							sender.sendMessage("§4" + args[1] + " §cis not online (Name is CaSe SeNsItIvE.)");
						}
						return true;
					} else if (opt.equalsIgnoreCase("promote")) {
						String cname = getClan(sender.getName());
						if (getClanOwner(cname).contains(sender.getName())) {
							for (String cmem : getClanMembers(cname)) {
								if (cmem.equalsIgnoreCase(args[1])) {
									if (clans.get(cname).get("Mod") == null) {
										clans.get(cname).put("Mod", new ArrayList<String>());
									}
									if (getClanOwner(cname).contains(cmem)) {
										sender.sendMessage("§cYou cant promote yourself.");
									}
									if (getClanMods(cname).contains(cmem)) {
										sender.sendMessage("§cThat player is already a mod in your clan.");
										return true;
									} else {
										clans.get(cname).get("Mod").add(cmem);
										clans.get(cname).get("Member").remove(cmem);
										for (String pn : getClanMembers(cname)) {
											try {
												Bukkit.getPlayerExact(pn).sendMessage("§2" + cmem + "§e was promoted to §6Moderator§e.");
											} catch (NullPointerException npe) {
											}
										}
									}
									return true;
								}
							}
							sender.sendMessage("§cThat player is not in your clan.");
						} else {
							sender.sendMessage("§cYou are not a owner of your clan.");
						}
						return true;
					} else if (opt.equalsIgnoreCase("demote")) {
						String cname = getClan(sender.getName());
						if (getClanOwner(cname).contains(sender.getName())) {
							if (sender.getName().equalsIgnoreCase(args[1])) {
								sender.sendMessage("§cYou cant demote yourself.");
							} else {
								for (String cmem : getClanMods(cname)) {
									if (cmem.equalsIgnoreCase(args[1])) {
										clans.get(cname).get("Mod").remove(cmem);
										clans.get(cname).get("Member").add(cmem);
										for (String pn : getClanMembers(cname)) {
											try {
												Bukkit.getPlayerExact(pn).sendMessage("§2" + cmem + "§e was demoted to §6Member§e.");
											} catch (NullPointerException npe) {
											}
										}
									}
								}
							}
						} else {
							sender.sendMessage("§cYou are not a owner of your clan.");
						}
						return true;
					}
				} else if (opt.equalsIgnoreCase("info") || opt.equalsIgnoreCase("i") || opt.equalsIgnoreCase("w") || opt.equalsIgnoreCase("who")) {
					String cname = getClan(sender.getName());
					if (cname != null) {
						Bukkit.dispatchCommand(sender, "clans info " + cname);
						return true;
					} else {
						sender.sendMessage("§eYou do not belong to any clan.");
						return true;
					}
				} else if (opt.equalsIgnoreCase("chat") || opt.equalsIgnoreCase("c")) {
					if (allyChatter.contains(sender.getName())) {
						allyChatter.remove(sender.getName());
						sender.sendMessage("§eNow talking in §apublic §echat.");
					} else if (clanChatter.contains(sender.getName())) {
						allyChatter.add(sender.getName());
						clanChatter.remove(sender.getName());
						sender.sendMessage("§eNow talking in §dallies §echat.");
					} else {
						clanChatter.add(sender.getName());
						sender.sendMessage("§eNow talking in §bclan §echat.");
					}
					return true;
				} else if (opt.equalsIgnoreCase("list") || opt.equalsIgnoreCase("ls")) {
					Bukkit.dispatchCommand(sender, "clans list 1");
					return true;
				}
			}
			tellUsage(sender);
			return true;
		}
		return false;
	}

	public void tellUsage(CommandSender sender) {
		sender.sendMessage("§e-------------- §6RcClans Usage §e--------------");
		sender.sendMessage("§eCommand: §6/c, /clan, /clans");
		sender.sendMessage("§6Subcommands:");
		sender.sendMessage("§c - Admin:");
		sender.sendMessage("§c  - clean");
		sender.sendMessage("§c  - delete <clanname>");
		sender.sendMessage("§3 - Clan Owner:");
		sender.sendMessage("§3  - create|new|n <clanname>");
		sender.sendMessage("§3  - rename <newclanname>");
		sender.sendMessage("§3  - promote <playername>");
		sender.sendMessage("§3  - demote <playername>");
		sender.sendMessage("§3  - disband");
		sender.sendMessage("§9 - Clan Moderator:");
		sender.sendMessage("§9  - ally <clanname>");
		sender.sendMessage("§9  - unally <clanname>");
		sender.sendMessage("§9  - invite|add|a <playername>");
		sender.sendMessage("§9  - kick|k <playername>");
		sender.sendMessage("§b - Clan Member:");
		sender.sendMessage("§b  - chat|c [a|ally|allies,c|clan|clans,p|public]");
		sender.sendMessage("§b  - leave|l");
		sender.sendMessage("§b  - join|j  <clanname>");
		sender.sendMessage("§b - Everyone:");
		sender.sendMessage("§b  - list|ls");
		sender.sendMessage("§b  - info|i [clanname]");
		sender.sendMessage("§b  - who [playername]");
		sender.sendMessage("§b  - help");
	}

	public void tellNoPermission(CommandSender sender, String st) {
		sender.sendMessage("§cYou do not have permission to " + st + ".");
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void asyncChat(AsyncPlayerChatEvent e) {
		if (e.isCancelled()) {
			return;
		}
		String cname = getClan(e.getPlayer().getName());
		String fcname = "§6";
		if (cname == null) {
			cname = "";
			fcname = "§4§l-";
		} else if (clanWars) {
			fcname = "§c";
		}
		String pf = "";
		if (getClanOwner(cname).contains(e.getPlayer().getName())) {
			pf = "*";
		}
		if (getClanMods(cname).contains(e.getPlayer().getName())) {
			pf += "*";
		}
		String dcname = cname + " ";
		if (clanWars) {
			dcname = "";
			if (allyChatter.contains(e.getPlayer().getName())) {
				allyChatter.remove(e.getPlayer().getName());
				clanChatter.add(e.getPlayer().getName());
				e.getPlayer().sendMessage("§eForcing §bclan §echat mode because it is clanwars.");
			}
		}
		boolean cc = clanChatter.contains(e.getPlayer().getName());
		boolean ac = allyChatter.contains(e.getPlayer().getName());
		if (cc || ac) {
			if (cname == "") {
				e.getPlayer().sendMessage("§cYou do not belong to a clan, do §e/clan chat public §cto leave clan chat mode.");
				e.setCancelled(true);
			} else {
				String message = "";
				if (cc) {
					message = "§3" + pf + e.getPlayer().getDisplayName() + "§b§l> §3" + e.getMessage();
				} else {
					message = "§d" + pf + dcname + e.getPlayer().getDisplayName() + "§5§l> §d" + e.getMessage();
				}
				if (cc || ac) {
					for (String cmem : getClanMembers(cname)) {
						try {
							Bukkit.getPlayerExact(cmem).sendMessage(message);
						} catch (NullPointerException npe) {
						}
					}
				}
				if (ac) {
					for (String amem : getAlliedMembers(cname)) {
						try {
							Bukkit.getPlayerExact(amem).sendMessage(message);
						} catch (NullPointerException npe) {
						}
					}
				}
				e.setCancelled(true);
				Bukkit.getConsoleSender().sendMessage(message);
			}
		} else {
			e.setFormat("§8[" + fcname + pf + cname + "§8] " + e.getFormat());
		}
	}

	@EventHandler
	public void damage(EntityDamageByEntityEvent e) {
		Player damaged = null;
		if (e.getEntity() instanceof Player) {
			damaged = (Player) e.getEntity();
		} else {
			return;
		}
		Player damager = null;
		if (e.getDamager() instanceof Player) {
			damager = (Player) e.getDamager();
		} else if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
			damager = (Player) ((Projectile) e.getDamager()).getShooter();
		} else {
			return;
		}
		if (getClanMembers(getClan(damager.getName())).contains(damaged.getName())) {
			e.setCancelled(true);
			ActionBarChat.send(damager, "§2" + damaged.getDisplayName() + " §6is your clan-mate!");
			return;
		}
		if (getAlliedMembers(getClan(damager.getName())).contains(damaged.getName())) {
			e.setCancelled(true);
			ActionBarChat.send(damager, "§2" + damaged.getDisplayName() + " §6is your ally!");
			return;
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (clanWars && e.getEntity().getKiller() instanceof Player) {
			String cname = getClan(e.getEntity().getKiller().getName());
			if (cname != null) {
				if (cwStats.containsKey(cname)) {
					cwStats.put(cname, cwStats.get(cname) + 1);
				} else {
					cwStats.put(cname, 1);
				}
			}
		}
	}

	public Set<String> listClans() {
		return clans.keySet();
	}

	public List<String> sortedListClans() {
		Map<String, Integer> clans = new HashMap<String, Integer>();
		int max = 0;
		for (String cname : listClans()) {
			int conline = 0;
			for (String pn : getClanMembers(cname)) {
				if (Bukkit.getPlayerExact(pn) != null) {
					conline++;
				}
			}
			clans.put(cname, conline);
			if (conline > max) {
				max = conline;
			}
		}
		List<String> ret = new ArrayList<String>();
		for (int m = max; m >= 0; m--) {
			for (String cname : clans.keySet()) {
				if (clans.get(cname) >= m) {
					ret.add(cname);
				}
			}
			for (String r : ret) {
				clans.remove(r);
			}
		}
		return ret;
	}

	public String getClan(String pname) {
		for (String cname : listClans()) {
			List<String> Member = new ArrayList<String>();
			try {
				Member.addAll(clans.get(cname).get("Owner"));
				Member.addAll(clans.get(cname).get("Mod"));
				Member.addAll(clans.get(cname).get("Member"));
			} catch (NullPointerException npe) {
			}
			if (Member.contains(pname)) {
				return cname;
			}
		}
		return null;
	}

	public List<String> getClanOwner(String cname) {
		try {
			return clans.get(cname).get("Owner");
		} catch (NullPointerException npe) {
			return new ArrayList<String>();
		}
	}

	public List<String> getClanMods(String cname) {
		List<String> Member = new ArrayList<String>();
		try {
			Member.addAll(clans.get(cname).get("Owner"));
			try {
				Member.addAll(clans.get(cname).get("Mod"));
			} catch (NullPointerException npe) {
			}
		} catch (NullPointerException npe) {
		}
		return Member;
	}

	public List<String> getClanMembers(String cname) {
		List<String> Member = new ArrayList<String>();
		try {
			Member.addAll(clans.get(cname).get("Owner"));
			try {
				Member.addAll(clans.get(cname).get("Mod"));
			} catch (NullPointerException npe) {
			}
			try {
				Member.addAll(clans.get(cname).get("Member"));
			} catch (NullPointerException npe) {
			}
		} catch (NullPointerException npe) {
		}
		return Member;
	}

	public List<String> getClanUnrankedMembers(String cname) {
		try {
			return clans.get(cname).get("Member");
		} catch (NullPointerException npe) {
			return new ArrayList<String>();
		}
	}

	public List<String> getAlliedClans(String cname) {
		try {
			clans.get(cname).put("Ally", new ArrayList<String>(new LinkedHashSet<String>(clans.get(cname).get("Ally"))));
			return clans.get(cname).get("Ally");
		} catch (NullPointerException npe) {
			return new ArrayList<String>();
		}
	}

	public List<String> getOnline(List<String> names) {
		List<String> ret = new ArrayList<String>();
		for (String name : names) {
			if (Bukkit.getPlayerExact(name) instanceof Player) {
				ret.add(name);
			}
		}
		return ret;
	}

	public List<String> getAlliedMembers(String cname) {
		List<String> allies = new ArrayList<String>();
		if (!clanWars) {
			for (String allyClan : getAlliedClans(cname)) {
				allies.addAll(getClanMembers(allyClan));
			}
		}
		return allies;
	}

	public void scheduleClanWars(long ticks) {
		Chat.bc("§7[§eRcClans§7]§6 Clan wars will start in " + (ticks / 20) + " seconds with the bounty of §e500 §6kills.");
		new BukkitRunnable() {

			@Override
			public void run() {
				clanWars = true;
				Chat.bc("§7[§eRcClans§7]§6 Clan wars had commenced! Bounty: §e500 §6kills!");
				scheduleEndClanWars(12000);
			}
		}.runTaskLater(this, ticks);
	}

	public void scheduleEndClanWars(final long ticks) {
		new BukkitRunnable() {

			@Override
			public void run() {
				if (!clanWars) {
					return;
				}
				Chat.bc("§7[§eRcClans§7]§6 Clan wars will end in " + (ticks / 200) + " seconds.");
			}
		}.runTaskLater(this, (ticks / 10 * 9));
		new BukkitRunnable() {

			@Override
			public void run() {
				if (!clanWars) {
					return;
				}
				clanWars = false;
				Chat.bc("§7[§eRcClans§7]§6 Clan wars had ended!");
				int max = 0;
				String cname = null;
				for (String a : cwStats.keySet()) {
					if (cwStats.get(a) > max) {
						cname = a;
						max = cwStats.get(a);
					}
				}
				cwStats = new HashMap<String, Integer>();
				if (cname != null) {
					Chat.bc("§7[§eRcClans§7]§e " + cname + " §6won ClanWars with §e" + max + " §6kills. Each clan member has been rewarded with §e500 §6kills!");
				} else {
					Chat.bc("§7[§eRcClans§7]§e No clans won this round of clan wars.");
				}
				for (String pn : getClanMembers(cname)) {
					try {
						Bukkit.getPlayerExact(pn).incrementStatistic(Statistic.PLAYER_KILLS, 500);
					} catch (NullPointerException npe) {
					}
				}
			}
		}.runTaskLater(this, ticks);
	}

	private void updateTabCompleter() {
		TabCompleter completer = new ClansTabComplete(this);
		getCommand("c").setTabCompleter(completer);
		getCommand("clan").setTabCompleter(completer);
		getCommand("clans").setTabCompleter(completer);
	}

	private boolean isValidString(String str) {
		return str.matches("^[a-zA-Z0-9_]*$") && str.length() < 16;
	}
}
