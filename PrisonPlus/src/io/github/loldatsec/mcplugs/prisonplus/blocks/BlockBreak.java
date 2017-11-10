package io.github.loldatsec.mcplugs.prisonplus.blocks;

import io.github.loldatsec.mcplugs.prisonplus.inventory.Shop;
import io.github.loldatsec.mcplugs.prisonplus.inventory.Smelt;
import io.github.loldatsec.mcplugs.prisonplus.inventory.Stack;
import io.github.loldatsec.mcplugs.prisonplus.rankup.Rankup;
import io.github.loldatsec.mcplugs.prisonplus.text.ActionBarChat;
import io.github.loldatsec.mcplugs.prisonplus.text.Title;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockBreak implements Listener {

	private Shop s = null;
	private Mines minesController = null;

	public BlockBreak(Shop s, Mines minesController) {
		this.s = s;
		this.minesController = minesController;
	}
	private List<String> autoSellPlayers = new ArrayList<String>();
	private List<String> autoOrganizePlayers = new ArrayList<String>();
	private List<String> superBreakPlayers = new ArrayList<String>();

	@EventHandler(priority = EventPriority.HIGH)
	public void interact(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (p.getWorld().getName().equalsIgnoreCase("prison") && p.getGameMode() == GameMode.SURVIVAL && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (!(p.getItemInHand() instanceof ItemStack) || p.getItemInHand().getType() == Material.AIR) { return; }
			if (p.getItemInHand().getItemMeta() == null) { return; }
			if (p.getItemInHand().getItemMeta().getDisplayName() == null) { return; }
			int factor = 4;
			if (p.getItemInHand().getItemMeta().getLore() != null) {
				try {
					factor = Integer.parseInt(p.getItemInHand().getItemMeta().getLore().get(0).replace("\u00a7aPower: \u00a7c", ""));
				} catch (NumberFormatException nfe) {
				}
			}
			if (factor > 30) {
				factor = 30;
			}
			String h = p.getItemInHand().getItemMeta().getDisplayName();
			Block base = e.getClickedBlock();
			Set<Material> valid = s.sellable(p).keySet();
			if (h.equalsIgnoreCase("\u00a7aMining Explosive")) {
				if (valid.contains(base.getType()) && minesController.canNukeMine(base.getLocation())) {
					if (factor > 10) {
						p.sendMessage("\u00a72\u00a7l\u00a7m=============================================");
						p.sendMessage("");
						p.sendMessage("      \u00a7f\u00a7lYou used some high powered explosive.");
						p.sendMessage("");
						p.sendMessage("      \u00a7b\u00a7lPower: " + factor);
						p.sendMessage("      \u00a7c\u00a7lYou may experience some lag.");
						p.sendMessage("");
						p.sendMessage("\u00a72\u00a7l\u00a7m=============================================");
					}
					ItemStack ph = p.getItemInHand();
					if (ph.getAmount() <= 1) {
						p.setItemInHand(null);
					} else {
						ph.setAmount(ph.getAmount() - 1);
					}
					long earned = 0;
					Location effectlocation = base.getLocation();
					p.getWorld().playEffect(effectlocation, Effect.EXPLOSION_HUGE, 10);
					p.getWorld().playSound(effectlocation, Sound.EXPLODE, 1, 10);
					for (int x = -factor; x <= factor; x++) {
						for (int y = -factor; y <= factor; y++) {
							for (int z = -factor; z <= factor; z++) {
								Block n = base.getRelative(x, y, z);
								if (n.getLocation().distance(base.getLocation()) < 0.7 * factor) {
									earned += exec(n, p, true, 7 * factor, true);
								}
							}
						}
					}
					p.sendMessage("\u00a7aYou earned \u00a7c$" + Rankup.numformat(earned) + " \u00a7afrom this blast!");
				} else {
					p.sendMessage("\u00a7aYou can not blow anything up here!");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (p.getWorld().getName().equalsIgnoreCase("prison") && p.getGameMode() == GameMode.SURVIVAL) {
			e.setCancelled(true);
			Block b = e.getBlock();
			exec(b, p, false, 1);
			if (isSuperBreakPlayer(e.getPlayer().getName())) {
				exec(b.getRelative(BlockFace.NORTH), p, false, 1);
				exec(b.getRelative(BlockFace.EAST), p, false, 1);
				exec(b.getRelative(BlockFace.SOUTH), p, false, 1);
				exec(b.getRelative(BlockFace.WEST), p, false, 1);
				exec(b.getRelative(BlockFace.NORTH_EAST), p, false, 1);
				exec(b.getRelative(BlockFace.NORTH_WEST), p, false, 1);
				exec(b.getRelative(BlockFace.SOUTH_EAST), p, false, 1);
				exec(b.getRelative(BlockFace.SOUTH_WEST), p, false, 1);
				exec(b.getRelative(BlockFace.UP), p, false, 1);
				exec(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH), p, false, 1);
				exec(b.getRelative(BlockFace.UP).getRelative(BlockFace.EAST), p, false, 1);
				exec(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH), p, false, 1);
				exec(b.getRelative(BlockFace.UP).getRelative(BlockFace.WEST), p, false, 1);
				exec(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_EAST), p, false, 1);
				exec(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST), p, false, 1);
				exec(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_EAST), p, false, 1);
				exec(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_WEST), p, false, 1);
				exec(b.getRelative(BlockFace.DOWN), p, false, 1);
				exec(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH), p, false, 1);
				exec(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST), p, false, 1);
				exec(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH), p, false, 1);
				exec(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST), p, false, 1);
				exec(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH_EAST), p, false, 1);
				exec(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH_WEST), p, false, 1);
				exec(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH_EAST), p, false, 1);
				exec(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH_WEST), p, false, 1);
			}
		}
		int occupied = Shop.occupiedSlots(p);
		String pgb = "\u00a76Inventory: \u00a78[\u00a76\u00a7l";
		if (occupied < 36) {
			int oc = (int) (occupied / 2);
			for (int c = 0; c < 18; c++) {
				if (c == oc) {
					pgb += "\u00a77\u00a7l";
				}
				pgb += "»";
			}
			pgb += "\u00a78]";
		} else {
			pgb += "»»»»»»»»»»»»»»»»»»\u00a78]";
		}
		ActionBarChat.send(p, pgb);
	}

	public void exec(Block b, Player p, boolean fas, int mult) {
		exec(b, p, fas, mult, false);
	}

	@SuppressWarnings("deprecation")
	public long exec(Block b, Player p, boolean fas, int mult, boolean nukeMode) {
		if (nukeMode && (b.getType() == Material.AIR || b.getType() == Material.REDSTONE_BLOCK)) { return 0; }
		ItemStack tool = p.getItemInHand();
		if (s.sellable(p).containsKey(b.getType()) && minesController.isInMine(b.getLocation())) {
			List<ItemStack> addStacks = new ArrayList<ItemStack>();
			double slotsNeeded = 0;
			for (ItemStack d : b.getDrops()) {
				if (tool.getEnchantmentLevel(Enchantment.SILK_TOUCH) >= 1) {
					d = new ItemStack(b.getType(), 1);
					d.setDurability(b.getData());
					if (d.getType() == Material.GLOWING_REDSTONE_ORE) {
						d.setType(Material.REDSTONE_ORE);
					} else if (d.getType() == Material.LOG || d.getType() == Material.LOG_2) {
						d.setDurability((short) (b.getData() % 4));
					}
				}
				d.setAmount(d.getAmount() * (tool.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)));
				d.setAmount(d.getAmount() + 1);
				if (mult > 1) {
					d.setAmount(d.getAmount() * mult);
				}
				addStacks.add(d);
				slotsNeeded += (d.getAmount() / d.getMaxStackSize());
			}
			long ret = 0;
			if (Shop.occupiedSlots(p) + slotsNeeded > 35) {
				boolean ao = isAutoOrganizePlayer(p.getName()) || fas;
				if (ao) {
					new Smelt(p, ao);
					new Stack(p, ao);
				}
			}
			if (Shop.occupiedSlots(p) + slotsNeeded > 35) {
				if (isAutoSellPlayer(p.getName()) || fas) {
					ret = s.rankSell(p, true);
				} else {
					Title.send(p, "\u00a74Full Inventory!", "\u00a7e/sellall\u00a7c to sell all your items.", 0, 100, 0);
					return 0;
				}
			}
			b.setType(Material.AIR);
			for (ItemStack d : addStacks) {
				p.getInventory().addItem(d);
			}
			incrementItemStat(p, Statistic.MINE_BLOCK, Material.STONE);
			if (!nukeMode) {
				luckyMine(p);
			}
			return ret;
		}
		return 0;
	}

	public List<String> getAutoSellPlayers() {
		return autoSellPlayers;
	}

	public boolean isAutoSellPlayer(String pn) {
		return autoSellPlayers.contains(pn);
	}

	public void addAutoSellPlayers(String pn) {
		this.autoSellPlayers.add(pn);
	}

	public void addAllAutoSellPlayers(List<String> pl) {
		if (pl != null) {
			this.autoSellPlayers.addAll(pl);
		}
	}

	public void remAutoSellPlayers(String pn) {
		this.autoSellPlayers.remove(pn);
	}

	public List<String> getAutoOrganizePlayers() {
		return autoOrganizePlayers;
	}

	public boolean isAutoOrganizePlayer(String pn) {
		return autoOrganizePlayers.contains(pn);
	}

	public void addAutoOrganizePlayers(String pn) {
		this.autoOrganizePlayers.add(pn);
	}

	public void addAllAutoOrganizePlayers(List<String> pl) {
		if (pl != null) {
			this.autoOrganizePlayers.addAll(pl);
		}
	}

	public void remAutoOrganizePlayers(String pn) {
		this.autoOrganizePlayers.remove(pn);
	}

	public List<String> getSuperBreakPlayers() {
		return superBreakPlayers;
	}

	public boolean isSuperBreakPlayer(String pn) {
		return superBreakPlayers.contains(pn);
	}

	public void addSuperBreakPlayers(String pn) {
		this.superBreakPlayers.add(pn);
	}

	public void addAllSuperBreakPlayers(List<String> pl) {
		if (pl != null) {
			this.superBreakPlayers.addAll(pl);
		}
	}

	public void remSuperBreakPlayers(String pn) {
		this.superBreakPlayers.remove(pn);
	}

	private void incrementItemStat(Player p, Statistic s, Material mat) {
		try {
			p.incrementStatistic(s, mat);
			ItemStack i = p.getItemInHand();
			// TODO Fix.
			if (i instanceof ItemStack && isTool(i.getType())) {
				ItemMeta m = i.getItemMeta();
				List<String> nl = new ArrayList<String>();
				if (m.hasLore()) {
					for (String l : m.getLore()) {
						if (!l.startsWith("§e")) {
							nl.add(l);
						}
					}
				}
				int stat = p.getStatistic(s, mat);
				nl.add("§eTool Owner: §6" + p.getName());
				nl.add("§eBlocks Broke: §6" + stat);
				m.setLore(nl);
				m.setDisplayName(m.getDisplayName().replaceAll("§([0-9a-r])[0-9]+§", "§$1" + stat + "§"));
				i.setItemMeta(m);
				p.setItemInHand(i);
				p.updateInventory();
			}
		} catch (Exception e) {
			e.getCause().printStackTrace();
		}
	}

	private boolean isTool(Material i) {
		return i.toString().endsWith("AXE") || i.toString().endsWith("SPADE");
	}

	public void luckyMine(Player p) {
		if (Math.random() <= 0.003) {
			int inc = (int) (Math.random() * 100000);
			s.econ.depositPlayer(p, inc);
			Title.send(p, "", "\u00a76\u00a7k||\u00a7eYou mined a lucky block and received \u00a7c$" + inc + "\u00a76\u00a7k||", 10, 50, 10);
		}
	}
}
