package io.github.loldatsec.mcplugs.prisonplus.rankup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Rankup implements CommandExecutor {

	public static String prefix = "\u00a7e[\u00a76RC\u00a7e] \u00a7a";
	public static final int A = 8000; // Unused
	public static final int B = 8000;
	public static final int C = 16000;
	public static final int D = 24000;
	public static final int E = 48000;
	public static final int F = 72000;
	public static final int G = 120000;
	public static final int H = 192000;
	public static final int I = 312000;
	public static final int J = 504000;
	public static final int K = 816000;
	public static final int L = 1320000;
	public static final int M = 3136000;
	public static final int N = 4456000;
	public static final int O = 7592000;
	public static final int P = 12048000;
	public static final int Q = 19640000;
	public static final int R = 31688000;
	public static final int S = 51328000;
	public static final int T = 83016000;
	public static final int U = 134344000;
	public static final int V = 214360000;
	public static final int W = 348704000;
	public static final int X = 563064000;
	public static final int Y = 911768000;
	public static final int Z = 1474832000;
	public static final long Freeman = 2386600000L;
	public static final int PrestigeA = 8000000;
	public static final int PrestigeB = 8000000;
	public static final int PrestigeC = 16000000;
	public static final int PrestigeD = 24000000;
	public static final int PrestigeE = 48000000;
	public static final int PrestigeF = 72000000;
	public static final int PrestigeG = 120000000;
	public static final int PrestigeH = 192000000;
	public static final int PrestigeI = 312000000;
	public static final int PrestigeJ = 50400000;
	public static final int PrestigeK = 816000000;
	public static final int PrestigeL = 1320000000;
	public static final long PrestigeM = 3136000000L;
	public static final long PrestigeN = 4456000000L;
	public static final long PrestigeO = 7592000000L;
	public static final long PrestigeP = 12048000000L;
	public static final long PrestigeQ = 19640000000L;
	public static final long PrestigeR = 31688000000L;
	public static final long PrestigeS = 51328000000L;
	public static final long PrestigeT = 83016000000L;
	public static final long PrestigeU = 134344000000L;
	public static final long PrestigeV = 214360000000L;
	public static final long PrestigeW = 348704000000L;
	public static final long PrestigeX = 563064000000L;
	public static final long PrestigeY = 911768000000L;
	public static final long PrestigeZ = 1474832000000L;
	public static final long Baron = 238660000000L;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("rankup")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("You ain't no player.");
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(prefix + "/rankup next");
				sender.sendMessage(prefix + "/rankup ranks <page number>");
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("next")) {
					PermissionUser user = PermissionsEx.getPermissionManager().getUser(sender.getName());
					List<String> groupList = Arrays.asList(user.getGroupsNames());
					if (groupList.contains("PrestigeZ")) {
						promote(sender.getName(), Baron, "Baron");
					} else if (groupList.contains("PrestigeY")) {
						promote(sender.getName(), PrestigeZ, "PrestigeZ");
					} else if (groupList.contains("PrestigeX")) {
						promote(sender.getName(), PrestigeY, "PrestigeY");
					} else if (groupList.contains("PrestigeW")) {
						promote(sender.getName(), PrestigeX, "PrestigeX");
					} else if (groupList.contains("PrestigeV")) {
						promote(sender.getName(), PrestigeW, "PrestigeW");
					} else if (groupList.contains("PrestigeU")) {
						promote(sender.getName(), PrestigeV, "PrestigeV");
					} else if (groupList.contains("PrestigeT")) {
						promote(sender.getName(), PrestigeU, "PrestigeU");
					} else if (groupList.contains("PrestigeS")) {
						promote(sender.getName(), PrestigeT, "PrestigeT");
					} else if (groupList.contains("PrestigeR")) {
						promote(sender.getName(), PrestigeS, "PrestigeS");
					} else if (groupList.contains("PrestigeQ")) {
						promote(sender.getName(), PrestigeR, "PrestigeR");
					} else if (groupList.contains("PrestigeP")) {
						promote(sender.getName(), PrestigeQ, "PrestigeQ");
					} else if (groupList.contains("PrestigeO")) {
						promote(sender.getName(), PrestigeP, "PrestigeP");
					} else if (groupList.contains("PrestigeN")) {
						promote(sender.getName(), PrestigeO, "PrestigeO");
					} else if (groupList.contains("PrestigeM")) {
						promote(sender.getName(), PrestigeN, "PrestigeN");
					} else if (groupList.contains("PrestigeL")) {
						promote(sender.getName(), PrestigeM, "PrestigeM");
					} else if (groupList.contains("PrestigeK")) {
						promote(sender.getName(), PrestigeL, "PrestigeL");
					} else if (groupList.contains("PrestigeJ")) {
						promote(sender.getName(), PrestigeK, "PrestigeK");
					} else if (groupList.contains("PrestigeI")) {
						promote(sender.getName(), PrestigeJ, "PrestigeJ");
					} else if (groupList.contains("PrestigeH")) {
						promote(sender.getName(), PrestigeI, "PrestigeI");
					} else if (groupList.contains("PrestigeG")) {
						promote(sender.getName(), PrestigeH, "PrestigeH");
					} else if (groupList.contains("PrestigeF")) {
						promote(sender.getName(), PrestigeG, "PrestigeG");
					} else if (groupList.contains("PrestigeE")) {
						promote(sender.getName(), PrestigeF, "PrestigeF");
					} else if (groupList.contains("PrestigeD")) {
						promote(sender.getName(), PrestigeE, "PrestigeE");
					} else if (groupList.contains("PrestigeC")) {
						promote(sender.getName(), PrestigeD, "PrestigeD");
					} else if (groupList.contains("PrestigeB")) {
						promote(sender.getName(), PrestigeC, "PrestigeC");
					} else if (groupList.contains("PrestigeA")) {
						promote(sender.getName(), PrestigeB, "PrestigeB");
					} else if (groupList.contains("FreeMan")) {
						promote(sender.getName(), PrestigeA, "PrestigeA");
					} else if (groupList.contains("Z")) {
						promote(sender.getName(), Freeman, "FreeMan");
					} else if (groupList.contains("Y")) {
						promote(sender.getName(), Z, "Z");
					} else if (groupList.contains("X")) {
						promote(sender.getName(), Y, "Y");
					} else if (groupList.contains("W")) {
						promote(sender.getName(), X, "X");
					} else if (groupList.contains("V")) {
						promote(sender.getName(), W, "W");
					} else if (groupList.contains("U")) {
						promote(sender.getName(), V, "V");
					} else if (groupList.contains("T")) {
						promote(sender.getName(), U, "U");
					} else if (groupList.contains("S")) {
						promote(sender.getName(), T, "T");
					} else if (groupList.contains("R")) {
						promote(sender.getName(), S, "S");
					} else if (groupList.contains("Q")) {
						promote(sender.getName(), R, "R");
					} else if (groupList.contains("P")) {
						promote(sender.getName(), Q, "Q");
					} else if (groupList.contains("O")) {
						promote(sender.getName(), P, "P");
					} else if (groupList.contains("N")) {
						promote(sender.getName(), O, "O");
					} else if (groupList.contains("M")) {
						promote(sender.getName(), N, "N");
					} else if (groupList.contains("L")) {
						promote(sender.getName(), M, "M");
					} else if (groupList.contains("K")) {
						promote(sender.getName(), L, "L");
					} else if (groupList.contains("J")) {
						promote(sender.getName(), K, "K");
					} else if (groupList.contains("I")) {
						promote(sender.getName(), J, "J");
					} else if (groupList.contains("H")) {
						promote(sender.getName(), I, "I");
					} else if (groupList.contains("G")) {
						promote(sender.getName(), H, "H");
					} else if (groupList.contains("F")) {
						promote(sender.getName(), G, "G");
					} else if (groupList.contains("E")) {
						promote(sender.getName(), F, "F");
					} else if (groupList.contains("D")) {
						promote(sender.getName(), E, "E");
					} else if (groupList.contains("C")) {
						promote(sender.getName(), D, "D");
					} else if (groupList.contains("B")) {
						promote(sender.getName(), C, "C");
					} else if (groupList.contains("A")) {
						promote(sender.getName(), B, "B");
					} else if (!groupList.contains("Voter")) {
						grantVoter(sender.getName());
					} else {
						sender.sendMessage("\u00a7cYou rank is not part of a rankup system");
					}
				} else if (args[0].equalsIgnoreCase("ranks")) {
					sender.sendMessage(prefix + "/rankup ranks <page number>");
				}
				return true;
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("ranks")) {
					if (args[1].equalsIgnoreCase("1") || args[1].equalsIgnoreCase("2") || args[1].equalsIgnoreCase("3") || args[1].equalsIgnoreCase("4") || args[1].equalsIgnoreCase("5") || args[1].equalsIgnoreCase("6")) {
						// do the pages
						sender.sendMessage(prefix + "Ranks Costs:");
						if (args[1].equalsIgnoreCase("1")) {
							sender.sendMessage("\u00a76Page: \u00a7e1/6");
							sender.sendMessage("\u00a7aB:\u00a77 " + B);
							sender.sendMessage("\u00a7aC:\u00a77 " + C);
							sender.sendMessage("\u00a7aD:\u00a77 " + D);
							sender.sendMessage("\u00a7aE:\u00a77 " + E);
							sender.sendMessage("\u00a7aF:\u00a77 " + F);
							sender.sendMessage("\u00a7aG:\u00a77 " + G);
							sender.sendMessage("\u00a7aH:\u00a77 " + H);
							sender.sendMessage("\u00a7aI:\u00a77 " + I);
							sender.sendMessage("\u00a7aJ:\u00a77 " + J);
							return true;
						} else if (args[1].equalsIgnoreCase("2")) {
							sender.sendMessage("\u00a76Page: \u00a7e2/6");
							sender.sendMessage("\u00a7aK:\u00a77 " + K);
							sender.sendMessage("\u00a7aL:\u00a77 " + L);
							sender.sendMessage("\u00a7aM:\u00a77 " + M);
							sender.sendMessage("\u00a7aN:\u00a77 " + N);
							sender.sendMessage("\u00a7aO:\u00a77 " + O);
							sender.sendMessage("\u00a7aP:\u00a77 " + P);
							sender.sendMessage("\u00a7aQ:\u00a77 " + Q);
							sender.sendMessage("\u00a7aR:\u00a77 " + R);
							sender.sendMessage("\u00a7aS:\u00a77 " + S);
							return true;
						} else if (args[1].equalsIgnoreCase("3")) {
							sender.sendMessage("\u00a76Page: \u00a7e3/6");
							sender.sendMessage("\u00a7aT:\u00a77 " + T);
							sender.sendMessage("\u00a7aU:\u00a77 " + U);
							sender.sendMessage("\u00a7aV:\u00a77 " + V);
							sender.sendMessage("\u00a7aW:\u00a77 " + W);
							sender.sendMessage("\u00a7aX:\u00a77 " + X);
							sender.sendMessage("\u00a7aY:\u00a77 " + Y);
							sender.sendMessage("\u00a7aZ:\u00a77 " + Z);
							sender.sendMessage("\u00a7aFreeman:\u00a77 " + Freeman);
							return true;
						} else if (args[1].equalsIgnoreCase("4")) {
							sender.sendMessage("\u00a76Page: \u00a7e4/6");
							sender.sendMessage("\u00a7aPrestigeA:\u00a77 " + PrestigeA);
							sender.sendMessage("\u00a7aPrestigeB:\u00a77 " + PrestigeB);
							sender.sendMessage("\u00a7aPrestigeC:\u00a77 " + PrestigeC);
							sender.sendMessage("\u00a7aPrestigeD:\u00a77 " + PrestigeD);
							sender.sendMessage("\u00a7aPrestigeE:\u00a77 " + PrestigeE);
							sender.sendMessage("\u00a7aPrestigeF:\u00a77 " + PrestigeF);
							sender.sendMessage("\u00a7aPrestigeG:\u00a77 " + PrestigeG);
							sender.sendMessage("\u00a7aPrestigeH:\u00a77 " + PrestigeH);
							sender.sendMessage("\u00a7aPrestigeI:\u00a77 " + PrestigeI);
							return true;
						} else if (args[1].equalsIgnoreCase("5")) {
							sender.sendMessage("\u00a76Page: \u00a7e5/6");
							sender.sendMessage("\u00a7aPrestigeJ:\u00a77 " + PrestigeJ);
							sender.sendMessage("\u00a7aPrestigeK:\u00a77 " + PrestigeK);
							sender.sendMessage("\u00a7aPrestigeL:\u00a77 " + PrestigeL);
							sender.sendMessage("\u00a7aPrestigeM:\u00a77 " + PrestigeM);
							sender.sendMessage("\u00a7aPrestigeN:\u00a77 " + PrestigeN);
							sender.sendMessage("\u00a7aPrestigeO:\u00a77 " + PrestigeO);
							sender.sendMessage("\u00a7aPrestigeP:\u00a77 " + PrestigeP);
							sender.sendMessage("\u00a7aPrestigeQ:\u00a77 " + PrestigeQ);
							sender.sendMessage("\u00a7aPrestigeR:\u00a77 " + PrestigeR);
							return true;
						} else if (args[1].equalsIgnoreCase("6")) {
							sender.sendMessage("\u00a76Page: \u00a7e6/6");
							sender.sendMessage("\u00a7aPrestigeS:\u00a77 " + PrestigeS);
							sender.sendMessage("\u00a7aPrestigeT:\u00a77 " + PrestigeT);
							sender.sendMessage("\u00a7aPrestigeU:\u00a77 " + PrestigeU);
							sender.sendMessage("\u00a7aPrestigeV:\u00a77 " + PrestigeV);
							sender.sendMessage("\u00a7aPrestigeW:\u00a77 " + PrestigeW);
							sender.sendMessage("\u00a7aPrestigeX:\u00a77 " + PrestigeX);
							sender.sendMessage("\u00a7aPrestigeY:\u00a77 " + PrestigeY);
							sender.sendMessage("\u00a7aPrestigeZ:\u00a77 " + PrestigeZ);
							sender.sendMessage("\u00a7aBaron:\u00a77 " + Baron);
							sender.sendMessage("\u00a72Voter:\u00a7a 180 Nether Stars");
							return true;
						}
					} else {
						sender.sendMessage("\u00a7e" + args[1] + " \u00a7cis not a valid page Number");
						return true;
					}
				}
			} else {
				sender.sendMessage(prefix + "/rankup next");
				sender.sendMessage(prefix + "/rankup ranks <page number>");
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	private void promote(String name, long amount, String nextrank) {
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		Economy econ = rsp.getProvider();
		EconomyResponse r = econ.withdrawPlayer(name, amount);
		if (r.transactionSuccess()) {
			PermissionUser pu = PermissionsEx.getPermissionManager().getUser(name);
			String uprefix = "";
			for (String pg : pu.getGroupsNames()) {
				if (pg.matches("^(([a-zA-Z0-9]+||)[A-Z])$") || pg.equalsIgnoreCase("FreeMan")) {
					pu.removeGroup(pg);
				} else {
					uprefix += PermissionsEx.getPermissionManager().getGroup(pg).getPrefix();
				}
			}
			pu.addGroup(nextrank);
			uprefix += PermissionsEx.getPermissionManager().getGroup(nextrank).getPrefix();
			if (!uprefix.equalsIgnoreCase(PermissionsEx.getPermissionManager().getGroup(nextrank).getPrefix())) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + name + " prefix \"" + uprefix + "\"");
			}
			Bukkit.broadcastMessage(prefix + "\u00a76Congradulations! " + name + " \u00a7ahas ranked up to \u00a7b" + nextrank);
			Bukkit.getPlayerExact(name).sendMessage(prefix + "\u00a7eYou ranked up to \u00a7b" + nextrank);
			if (nextrank.equalsIgnoreCase("PrestigeA")) {
				econ.withdrawPlayer(name, econ.getBalance(name));
			}
		} else {
			Bukkit.getPlayerExact(name).sendMessage("\u00a7cYou don't have Enought Money to rankup right now!");
		}
	}

	private void grantVoter(String pname) {
		List<Integer> starSlots = new ArrayList<Integer>();
		Player p = Bukkit.getPlayerExact(pname);
		for (int n = 0; n <= 35; n++) {
			try {
				ItemStack i = p.getInventory().getItem(n);
				if (i.getType() == Material.NETHER_STAR) {
					starSlots.add(n);
				}
			} catch (NullPointerException e) {
			}
		}
		int starNeeded = 180;
		for (int cs : starSlots) {
			if (starNeeded > 0) {
				ItemStack star = p.getInventory().getItem(cs);
				int removedstar = star.getAmount();
				if (removedstar > starNeeded) {
					removedstar = starNeeded;
				}
				star.setAmount(star.getAmount() - removedstar);
				starNeeded = starNeeded - removedstar;
				if (star.getAmount() <= 0) {
					p.getInventory().setItem(cs, null);
				} else {
					p.getInventory().setItem(cs, star);
				}
			}
		}
		if (starNeeded > 0) {
			p.sendMessage("\u00a7cYou need \u00a7b" + starNeeded + " \u00a7cmore star to rankup.");
			int starRetAmt = 180 - starNeeded;
			if (starRetAmt >= 1) {
				p.getInventory().addItem(new ItemStack(Material.NETHER_STAR, starRetAmt));
			}
		} else {
			PermissionUser pu = PermissionsEx.getUser(p);
			pu.addGroup("Voter");
			Bukkit.broadcastMessage(prefix + "\u00a76Congradulations! " + pname + " \u00a7ahas ranked up to \u00a72Voter");
			Bukkit.getPlayerExact(pname).sendMessage(prefix + "\u00a7eYou ranked up to \u00a72Voter");
		}
	}

	public static void sortPrefixes() {
		for (PermissionUser pu : PermissionsEx.getPermissionManager().getUsers()) {
			String uprefix = "";
			String usuffix = "";
			for (String pg : pu.getGroupsNames()) {
				uprefix = PermissionsEx.getPermissionManager().getGroup(pg).getPrefix() + uprefix;
				usuffix = PermissionsEx.getPermissionManager().getGroup(pg).getSuffix();
			}
			if (!uprefix.equalsIgnoreCase(pu.getPrefix())) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pu.getName() + " prefix \"" + uprefix + "\"");
			}
			if (!usuffix.equalsIgnoreCase(pu.getSuffix())) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pu.getName() + " suffix \"" + usuffix + "\"");
			}
		}
	}

	public static String getNextRank(String[] currentranks) {
		List<String> groupList = Arrays.asList(currentranks);
		String next = "";
		if (groupList.contains("PrestigeZ")) {
			next = "Baron";
		} else if (groupList.contains("PrestigeY")) {
			next = "PrestigeZ";
		} else if (groupList.contains("PrestigeX")) {
			next = "PrestigeY";
		} else if (groupList.contains("PrestigeW")) {
			next = "PrestigeX";
		} else if (groupList.contains("PrestigeV")) {
			next = "PrestigeW";
		} else if (groupList.contains("PrestigeU")) {
			next = "PrestigeV";
		} else if (groupList.contains("PrestigeT")) {
			next = "PrestigeU";
		} else if (groupList.contains("PrestigeS")) {
			next = "PrestigeT";
		} else if (groupList.contains("PrestigeR")) {
			next = "PrestigeS";
		} else if (groupList.contains("PrestigeQ")) {
			next = "PrestigeR";
		} else if (groupList.contains("PrestigeP")) {
			next = "PrestigeQ";
		} else if (groupList.contains("PrestigeO")) {
			next = "PrestigeP";
		} else if (groupList.contains("PrestigeN")) {
			next = "PrestigeO";
		} else if (groupList.contains("PrestigeM")) {
			next = "PrestigeN";
		} else if (groupList.contains("PrestigeL")) {
			next = "PrestigeM";
		} else if (groupList.contains("PrestigeK")) {
			next = "PrestigeL";
		} else if (groupList.contains("PrestigeJ")) {
			next = "PrestigeK";
		} else if (groupList.contains("PrestigeI")) {
			next = "PrestigeJ";
		} else if (groupList.contains("PrestigeH")) {
			next = "PrestigeI";
		} else if (groupList.contains("PrestigeG")) {
			next = "PrestigeH";
		} else if (groupList.contains("PrestigeF")) {
			next = "PrestigeG";
		} else if (groupList.contains("PrestigeE")) {
			next = "PrestigeF";
		} else if (groupList.contains("PrestigeD")) {
			next = "PrestigeE";
		} else if (groupList.contains("PrestigeC")) {
			next = "PrestigeD";
		} else if (groupList.contains("PrestigeB")) {
			next = "PrestigeC";
		} else if (groupList.contains("PrestigeA")) {
			next = "PrestigeB";
		} else if (groupList.contains("FreeMan")) {
			next = "PrestigeA";
		} else if (groupList.contains("Z")) {
			next = "FreeMan";
		} else if (groupList.contains("Y")) {
			next = "Z";
		} else if (groupList.contains("X")) {
			next = "Y";
		} else if (groupList.contains("W")) {
			next = "X";
		} else if (groupList.contains("V")) {
			next = "W";
		} else if (groupList.contains("U")) {
			next = "V";
		} else if (groupList.contains("T")) {
			next = "U";
		} else if (groupList.contains("S")) {
			next = "T";
		} else if (groupList.contains("R")) {
			next = "S";
		} else if (groupList.contains("Q")) {
			next = "R";
		} else if (groupList.contains("P")) {
			next = "Q";
		} else if (groupList.contains("O")) {
			next = "P";
		} else if (groupList.contains("N")) {
			next = "O";
		} else if (groupList.contains("M")) {
			next = "N";
		} else if (groupList.contains("L")) {
			next = "M";
		} else if (groupList.contains("K")) {
			next = "L";
		} else if (groupList.contains("J")) {
			next = "K";
		} else if (groupList.contains("I")) {
			next = "J";
		} else if (groupList.contains("H")) {
			next = "I";
		} else if (groupList.contains("G")) {
			next = "H";
		} else if (groupList.contains("F")) {
			next = "G";
		} else if (groupList.contains("E")) {
			next = "F";
		} else if (groupList.contains("D")) {
			next = "E";
		} else if (groupList.contains("C")) {
			next = "D";
		} else if (groupList.contains("B")) {
			next = "C";
		} else if (groupList.contains("A")) {
			next = "B";
		}
		return next;
	}

	public static long getNextRankPrice(String[] currentranks) {
		List<String> groupList = Arrays.asList(currentranks);
		long next = 0;
		if (groupList.contains("PrestigeZ")) {
			next = Baron;
		} else if (groupList.contains("PrestigeY")) {
			next = PrestigeZ;
		} else if (groupList.contains("PrestigeX")) {
			next = PrestigeY;
		} else if (groupList.contains("PrestigeW")) {
			next = PrestigeX;
		} else if (groupList.contains("PrestigeV")) {
			next = PrestigeW;
		} else if (groupList.contains("PrestigeU")) {
			next = PrestigeV;
		} else if (groupList.contains("PrestigeT")) {
			next = PrestigeU;
		} else if (groupList.contains("PrestigeS")) {
			next = PrestigeT;
		} else if (groupList.contains("PrestigeR")) {
			next = PrestigeS;
		} else if (groupList.contains("PrestigeQ")) {
			next = PrestigeR;
		} else if (groupList.contains("PrestigeP")) {
			next = PrestigeQ;
		} else if (groupList.contains("PrestigeO")) {
			next = PrestigeP;
		} else if (groupList.contains("PrestigeN")) {
			next = PrestigeO;
		} else if (groupList.contains("PrestigeM")) {
			next = PrestigeN;
		} else if (groupList.contains("PrestigeL")) {
			next = PrestigeM;
		} else if (groupList.contains("PrestigeK")) {
			next = PrestigeL;
		} else if (groupList.contains("PrestigeJ")) {
			next = PrestigeK;
		} else if (groupList.contains("PrestigeI")) {
			next = PrestigeJ;
		} else if (groupList.contains("PrestigeH")) {
			next = PrestigeI;
		} else if (groupList.contains("PrestigeG")) {
			next = PrestigeH;
		} else if (groupList.contains("PrestigeF")) {
			next = PrestigeG;
		} else if (groupList.contains("PrestigeE")) {
			next = PrestigeF;
		} else if (groupList.contains("PrestigeD")) {
			next = PrestigeE;
		} else if (groupList.contains("PrestigeC")) {
			next = PrestigeD;
		} else if (groupList.contains("PrestigeB")) {
			next = PrestigeC;
		} else if (groupList.contains("PrestigeA")) {
			next = PrestigeB;
		} else if (groupList.contains("FreeMan")) {
			next = PrestigeA;
		} else if (groupList.contains("Z")) {
			next = Freeman;
		} else if (groupList.contains("Y")) {
			next = Z;
		} else if (groupList.contains("X")) {
			next = Y;
		} else if (groupList.contains("W")) {
			next = X;
		} else if (groupList.contains("V")) {
			next = W;
		} else if (groupList.contains("U")) {
			next = V;
		} else if (groupList.contains("T")) {
			next = U;
		} else if (groupList.contains("S")) {
			next = T;
		} else if (groupList.contains("R")) {
			next = S;
		} else if (groupList.contains("Q")) {
			next = R;
		} else if (groupList.contains("P")) {
			next = Q;
		} else if (groupList.contains("O")) {
			next = P;
		} else if (groupList.contains("N")) {
			next = O;
		} else if (groupList.contains("M")) {
			next = N;
		} else if (groupList.contains("L")) {
			next = M;
		} else if (groupList.contains("K")) {
			next = L;
		} else if (groupList.contains("J")) {
			next = K;
		} else if (groupList.contains("I")) {
			next = J;
		} else if (groupList.contains("H")) {
			next = I;
		} else if (groupList.contains("G")) {
			next = H;
		} else if (groupList.contains("F")) {
			next = G;
		} else if (groupList.contains("E")) {
			next = F;
		} else if (groupList.contains("D")) {
			next = E;
		} else if (groupList.contains("C")) {
			next = D;
		} else if (groupList.contains("B")) {
			next = C;
		} else if (groupList.contains("A")) {
			next = B;
		}
		return next;
	}
}
