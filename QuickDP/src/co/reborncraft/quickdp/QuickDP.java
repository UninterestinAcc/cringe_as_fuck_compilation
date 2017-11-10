package co.reborncraft.quickdp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class QuickDP extends JavaPlugin implements Listener {
	public static final String INVENTORY_NAME = "\u00a73\u00a7lQuickDP Contents";
	private final AtomicBoolean dpLock = new AtomicBoolean(false);
	private File dropsFile;
	private YamlConfiguration dropsConf;

	@Override
	public void onEnable() {
		dropsFile = new File(getDataFolder(), "drops.yml");
		saveResource("drops.yml", false);
		saveDefaultConfig();
		reloadDrops();
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (hasAdminPerms(sender)) {
				if (command.getName().equalsIgnoreCase("dodp")) {
					if (dpLock.get()) {
						sender.sendMessage("\u00a7cA DP is already in progress.");
						return true;
					}
					AtomicInteger replication = new AtomicInteger(1);
					if (args.length >= 1) {
						try {
							replication.set(Integer.parseUnsignedInt(args[0]));
						} catch (NumberFormatException e) {
							sender.sendMessage(e.getMessage());
							return true;
						}
					} else {
						sender.sendMessage("\u00a7cCommand usage: /dodp <?x the drops>");
						return true;
					}
					sender.sendMessage("\u00a73Starting a DP with with \u00a7b" + replication.get() + "x\u00a73 the drops.");
					sender.sendMessage("\u00a7c\u00a7lPlease get into position within \u00a74\u00a7l5\u00a7c\u00a7l seconds as items will drop from \u00a74\u00a7l2\u00a7c\u00a7l blocks below your feet!");
					final List<Map.Entry<ItemStack, Integer>> drops = getDrops().stream()
							.filter(Objects::nonNull)
							.map(stack -> new AbstractMap.SimpleEntry<>(stack, replication.get()))
							.collect(Collectors.toList());
					final int initialDropCount = getDropCount(drops);
					AtomicInteger counter = new AtomicInteger(0);
					AtomicInteger taskID = new AtomicInteger(0);
					dpLock.set(true);
					taskID.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
						int tickBeginDropCount = getDropCount(drops);
						if (tickBeginDropCount == initialDropCount) {
							p.sendMessage("\u00a7e[\u00a76DP\u00a7e]\u00a76 DP task is beginning.");
						}
						int tickDroppingCount = (tickBeginDropCount / (1200)) + 1;
						for (int dropsThisTick = 0; dropsThisTick < tickDroppingCount; dropsThisTick++) {
							final int ent = (int) (Math.random() * drops.size());
							Map.Entry<ItemStack, Integer> dropTarget = drops.get(ent);
							if (dropTarget.getValue() == 0) {
								drops.remove(ent);
								dropsThisTick--;
								continue;
							}
							dropTarget.setValue(dropTarget.getValue() - 1);
							p.getWorld().dropItem(p.getLocation().subtract(0, 2, 0), dropTarget.getKey());
						}
						int tickEndDropCount = getDropCount(drops);
						if (tickEndDropCount == 0) {
							Bukkit.getScheduler().cancelTask(taskID.get());
							dpLock.set(false);
							p.sendMessage("\u00a7e[\u00a76DP\u00a7e]\u00a76 DP task has completed.");
						} else if (counter.incrementAndGet() % 4 == 0) {
							p.sendMessage("\u00a7e[\u00a76DP\u00a7e]\u00a76 DP is \u00a7e" + new DecimalFormat("#.##").format(((initialDropCount - tickEndDropCount) / (double) initialDropCount) * 100) + "%\u00a76 complete.");
						}
					}, 100, 5));
				} else if (command.getName().equalsIgnoreCase("setdpcontents")) {
					Inventory inv = Bukkit.createInventory(p, 54, INVENTORY_NAME);
					List<ItemStack> drops = getDrops();
					int max = 54 < drops.size() ? 54 : drops.size();
					if (max > 0) {
						for (int i = 0; i < max; i++) {
							inv.setItem(i, drops.get(i));
						}
					}
					p.openInventory(inv);
				} else {
					sender.sendMessage("\u00a7cUnknown command.");
				}
			} else {
				sender.sendMessage("\u00a7cLacking permission: quickdp.admin or operator status.");
			}
		} else {
			sender.sendMessage("You're not a player.");
		}
		return true;
	}

	private int getDropCount(List<Map.Entry<ItemStack, Integer>> drops) {
		int count = 0;
		for (Map.Entry<ItemStack, Integer> drop : drops) {
			count += drop.getValue();
		}
		return count;
	}

	@Override
	public void onDisable() {
		saveDrops();
	}

	public void reloadDrops() {
		dropsConf = YamlConfiguration.loadConfiguration(dropsFile);
	}

	public void saveDrops() {
		try {
			dropsConf.save(dropsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void inventoryCloseEvent(InventoryCloseEvent e) {
		if (hasAdminPerms(e.getPlayer()) && e.getInventory().getName().equals(INVENTORY_NAME)) {
			setDrops(Arrays.stream(e.getInventory().getContents()).collect(Collectors.toList()));
			saveDrops();
			e.getPlayer().sendMessage("\u00a73Updating contents of the DP with what you had in the inventory.");
		}
	}

	private boolean hasAdminPerms(Permissible p) {
		return p.isOp() || p.hasPermission("quickdp.admin");
	}

	public List<ItemStack> getDrops() {
		return dropsConf.contains("drops") ? ((List<ItemStack>) dropsConf.getList("drops")) : new ArrayList<>();
	}

	public void setDrops(List<ItemStack> drops) {
		dropsConf.set("drops", drops);
	}
}
