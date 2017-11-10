package io.github.loldatsec.mcplugs.prisonplus.blocks;

import io.github.loldatsec.mcplugs.prisonplus.text.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mines {

	public static final String prefix = "\u00a79[\u00a73RcPrison\u00a79] \u00a7a";
	private Map<Integer, List<ItemStack>> mines = new HashMap<Integer, List<ItemStack>>();
	private List<Integer> noNukeMines = new ArrayList<Integer>();
	private List<Integer> spleefMines = new ArrayList<Integer>();
	private Map<String, Integer> minesAlias = new HashMap<String, Integer>();
	private Plugin plugin = null;

	public Mines() {
		plugin = Bukkit.getPluginManager().getPlugin("PrisonPlus");
	}

	public Map<Integer, List<ItemStack>> getMines() {
		return mines;
	}

	public void setMines(Map<Integer, List<ItemStack>> mines) {
		this.mines = mines;
	}

	public void registerAlias(String str, int mineId) {
		minesAlias.put(str.toLowerCase(), mineId);
	}

	public String getAliasById(int id) {
		for (String name : minesAlias.keySet()) {
			if (minesAlias.get(name) == id) {
				return name.toUpperCase();
			}
		}
		return "" + id;
	}

	public int getIdByAlias(String str) {
		str = str.toLowerCase();
		if (minesAlias.containsKey(str)) {
			return minesAlias.get(str);
		} else {
			return -1;
		}
	}

	public boolean hasMine(int id) {
		return mines.containsKey(id);
	}

	public void addMine(int id) {
		mines.put(id, new ArrayList<ItemStack>());
	}

	public void addComposition(int id, ItemStack i) {
		if (!mines.containsKey(id)) {
			addMine(id);
		}
		mines.get(id).add(i);
	}

	public void setNukeable(int id, boolean nukeable) {
		if (nukeable) {
			if (noNukeMines.contains(id)) {
				noNukeMines.remove(id);
			}
		} else {
			noNukeMines.add(id);
		}
	}

	public void setSpleef(int id, boolean spleef) {
		if (spleef) {
			if (spleefMines.contains(id)) {
				spleefMines.remove(id);
			}
		} else {
			spleefMines.add(id);
		}
	}

	public void trigger() {
		Chat.bc(prefix + "Resetting all mines...");
		int maxId = 0;
		for (int mineId : mines.keySet()) {
			if (mineId > maxId) {
				maxId = mineId;
			}
			resetMine(mineId);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			@Override
			public void run() {
				Chat.bc(prefix + "All mines resetted.");
			}
		}, 251);
	}

	public boolean resetMine(final int mineId) {
		if (mines.containsKey(mineId)) {
			final int minx = 968 + mineId * 100;
			final int maxx = 1000 + mineId * 100;
			Location minecenter = new Location(Bukkit.getWorld("prison"), maxx - 16, 73, 16);
			Location respawn = new Location(Bukkit.getWorld("prison"), maxx - 15.5, 102, -9.5, 0, 0);
			Bukkit.getOnlinePlayers().stream().filter(p -> p.getWorld().getName().equalsIgnoreCase("prison")).filter(p -> p.getLocation().distance(minecenter) <= 80).forEach(p -> {
				p.teleport(respawn);
				p.sendMessage(prefix + "This mine is resetting, teleporting you to \u00a7e" + respawn.getWorld().getName() + "/ X:" + respawn.getBlockX() + ", Y:" + respawn.getBlockY() + ", Z:" + respawn.getBlockZ());
			});
			for (int yp = 48; yp <= 98; yp++) {
				final int y = yp;
				Runnable run = () -> {
					for (int x = minx; x <= maxx; x++) {
						for (int z = 0; z <= 32; z++) {
							Block b = Bukkit.getWorld("prison").getBlockAt(x, y, z);
							ItemStack i = mines.get(mineId).get((int) (Math.random() * (mines.get(mineId).size())));
							b.setType(i.getType());
							b.setData((byte) i.getDurability());
						}
					}
				};
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, run, (98 - y) * 5);
			}
			return true;
		} else {
			Bukkit.getConsoleSender().sendMessage(prefix + "Mine not found: \u00a7e" + mineId);
			return false;
		}
	}

	public boolean isInMine(Location l) {
		double xp = l.getX();
		double yp = l.getY();
		double zp = l.getZ();
		boolean x = xp >= 968 && (xp % 100 >= 68 || xp % 100 == 0);
		boolean y = yp >= 48 && yp <= 98;
		boolean z = zp >= 0 && zp <= 32;
		return (x && y && z);
	}

	public int getIdByX(double x) {
		return (x < 968) ? 0 : (int) Math.abs((x - 968) / 100);
	}

	public boolean canNukeMine(Location l) {
		return isInMine(l) && canNukeMine(getIdByX(l.getX()));
	}

	public boolean canNukeMine(int id) {
		return !noNukeMines.contains(id);
	}
}
