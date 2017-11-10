package io.github.loldatsec.mcplugins.haloplus.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import io.github.loldatsec.mcplugins.haloplus.HaloPlus;
import io.github.loldatsec.mcplugins.haloplus.utils.SpecialChat;

public class GameManager {

	public static HashMap<World, Game> games = new HashMap<World, Game>();
	public static Map<Player, World> immoveable = new HashMap<Player, World>();

	@SuppressWarnings("deprecation")
	public static void scheduler() {
		for (World w : Bukkit.getWorlds()) {
			if (!w.getName().equalsIgnoreCase("Lobby")) {
				if (!games.containsKey(w) || games.get(w) == null || !(games.get(w) instanceof Game) || games.get(w).isEnded()) {
					w.getWorldBorder().setSize(201, 1);
					games.put(w, new Game(w));
				}
				Game g = games.get(w);
				if ((w.getPlayers().size() >= HaloPlus.playersToStartGame && g.vote2start.size() >= w.getPlayers().size() / 5 * 4) && !g.isRunning()) {
					g.startGame();
				}
				g.scheduler();
				if (g.isRunning() && !g.isEnded()) {
					for (GameTeam gt : g.gameTeams.values()) {
						for (OfflinePlayer p : gt.scoreboardTeam.getPlayers()) {
							if (!p.isOnline() || !p.getPlayer().getWorld().getName().equalsIgnoreCase(w.getName())) {
								gt.scoreboardTeam.removePlayer(p);
							}
						}
					}
				}
			}
		}
		List<Player> remove = new ArrayList<Player>();
		for (Player p : immoveable.keySet()) {
			if (!p.isOnline() && p.getWorld().getName().equalsIgnoreCase("Lobby") || !p.getWorld().getName().equalsIgnoreCase(immoveable.get(p).getName())) {
				remove.add(p);
			}
		}
		for (Player p : remove) {
			immoveable.remove(p);
		}
	}

	public static Game getGame(Player p) {
		for (Game g : games.values()) {
			if (g.w.getPlayers().contains(p)) { return g; }
		}
		return null;
	}

	public static GameTeamEnum getGameTeam(Player p) {
		for (Game g : games.values()) {
			try {
				if (g.gameTeams.get(GameTeamEnum.BLUE).members.containsKey(p)) {
					return GameTeamEnum.BLUE;
				} else if (g.gameTeams.get(GameTeamEnum.RED).members.containsKey(p)) {
					return GameTeamEnum.RED;
				} else if (g.gameTeams.get(GameTeamEnum.YELLOW).members.containsKey(p)) {
					return GameTeamEnum.YELLOW;
				} else if (g.gameTeams.get(GameTeamEnum.GREEN).members.containsKey(p)) {
					// Newline...
					return GameTeamEnum.GREEN;
				}
			} catch (NullPointerException npe) {
			}
		}
		return GameTeamEnum.SPECTATOR;
	}

	public static void sendKillEvent(Player k) {
		if (games.get(k.getWorld()) instanceof Game) {
			games.get(k.getWorld()).kill(k);
		}
	}

	public static void sendDeathEvent(Player p) {
		if (games.get(p.getWorld()) instanceof Game) {
			games.get(p.getWorld()).death(p);
		}
		for (Map<String, Integer> sa : HaloPlus.shoot.shots.values()) {
			if (sa.containsKey(p.getName())) {
				sa.remove(p.getName());
			}
		}
	}

	public static boolean sendEvent(InventoryClickEvent e) {
		if (games.get(e.getWhoClicked().getWorld()) instanceof Game) {
			// Newline
			return games.get(e.getWhoClicked().getWorld()).purchaseEvent(e);
		}
		return false;
	}

	public static boolean joinGame(Player player, World world) {
		if (!world.getName().equalsIgnoreCase("Lobby")) {
			if (!(games.get(world) instanceof Game) || games.get(world).isEnded()) {
				games.put(world, new Game(world));
			}
			if (games.containsKey(world) && games.get(world) instanceof Game) {
				player.teleport(world.getSpawnLocation());
				return true;
			}
		}
		return false;
	}

	public static boolean joinGame(Player player, String w) {
		World world = Bukkit.getWorld(w);
		if (world instanceof World) {
			return joinGame(player, world);
		} else {
			SpecialChat.tell(player, new String[] { "World not found." }, ChatColor.RED);
		}
		return false;
	}

	public static void joinGame(Player player) {
		World d = Bukkit.getWorld("Lobby");
		int pl = 0;
		for (Game g : games.values()) {
			if (g.w.getPlayers().size() >= pl && !g.isRunning() && !g.isEnded()) {
				d = g.w;
				pl = g.w.getPlayers().size();
			}
		}
		if (!d.getName().equalsIgnoreCase("Lobby")) {
			Game g = games.get(d);
			if (!(g instanceof Game)) {
				g = new Game(d);
			}
			if (!g.isEnded() && !g.isRunning()) {
				joinGame(player, d);
			}
			games.put(d, g);
		} else {
			SpecialChat.tell(player, new String[] { "\u00a7dThere aren't any available worlds." }, ChatColor.DARK_PURPLE);
		}
	}
}
