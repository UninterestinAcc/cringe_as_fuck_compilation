package co.reborncraft.stackperms.debug;

import co.reborncraft.stackperms.StackPerms;
import co.reborncraft.stackperms.impl.GroupImpl;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;

public class DebugWrapper implements Runnable, Listener {
	@Override
	public void run() {
		StackPerms.getInstance().getLogger().info("[DEBUG] Attaching debugger.");
		Bukkit.getPluginManager().registerEvents(this, StackPerms.getInstance());
		StackPerms.getInstance().getLogger().info("[DEBUG] Attached debugger.");

		StackPerms.getInstance().getLogger().info("[DEBUG] Dumping indexes.");
		StackPerms.getInterface().getIndexes().forEach((child, parents) -> {
			StackPerms.getInstance().getLogger().info(child + " ->");
			parents.forEach(permission -> {
				StackPerms.getInstance().getLogger().info(" > " + permission);
			});
		});
		StackPerms.getInstance().getLogger().info("[DEBUG] Index dump finished.");

		StackPerms.getInstance().getLogger().info("[DEBUG] Creating test group.");
		GroupImpl tg = new GroupImpl("TestGroup");
		StackPerms.getInstance().getLogger().info("[DEBUG] Test group created, adding permissions.");
		Map<String, Boolean> perms = new HashMap<>();
		perms.put("lmao", true);
		perms.put("notlmao", false);
		perms.put("less.than.<", true);
		perms.put("less.than.html.&lt;", true);
		tg.addPermissions(perms);
		StackPerms.getInstance().getLogger().info("[DEBUG] Saving test group.");
		tg.save();
		StackPerms.getInstance().getLogger().info("[DEBUG] Test group saved.");
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		StackPerms.getInstance().getLogger().info("[DEBUG] " + e.getPlayer().getName() + " joined, initiating test.");
		StackPerms.getInterface().getGroupByName("TestGroup").ifPresent(g -> StackPerms.getInterface().getOrCreateSavePlayer(e.getPlayer().getName()).addGroup(g));
		for (String pt : new String[]{"lmao", "obscured.not.declared.permission"}) {
			System.out.println(pt + ": " + e.getPlayer().hasPermission(pt));
		}
	}
}
