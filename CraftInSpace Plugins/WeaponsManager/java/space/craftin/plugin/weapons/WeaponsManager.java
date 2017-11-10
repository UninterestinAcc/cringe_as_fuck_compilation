/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.weapons;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import space.craftin.plugin.core.api.physical.objects.IBlock;
import space.craftin.plugin.core.api.physical.objects.IMissile;
import space.craftin.plugin.core.impl.core.ChatUtil;
import space.craftin.plugin.core.impl.core.Locatable;
import space.craftin.plugin.utils.VectorUtil;
import space.craftin.plugin.weapons.impl.Missile;
import space.craftin.plugin.weapons.listener.WeaponsListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WeaponsManager extends JavaPlugin {
	private static WeaponsManager instance = null;
	private final List<IMissile> missiles = new ArrayList<>();

	public WeaponsManager() {
		instance = this;
	}

	public static WeaponsManager getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new WeaponsListener(), this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> missiles.stream().filter(IMissile::isFired).forEach(IMissile::move), 0, 1);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> missiles.stream().filter(IMissile::hasExploded).collect(Collectors.toList()).forEach(missiles::remove), 0, 1);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender.isOp()) {
			Missile m = new Missile(((Player) sender).getEyeLocation(), new Locatable(((Player) sender).getEyeLocation()), new Locatable(VectorUtil.shiftLocation(((Player) sender).getEyeLocation(), 100)));
			m.addStage(
					IBlock.BlockType.NUCLEAR_WARHEAD,
					IBlock.BlockType.MISSILE_STAGE,
					IBlock.BlockType.MISSILE_STAGE,
					IBlock.BlockType.MISSILE_STAGE
			);
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "Missile placed.");
		} else {
			ChatUtil.send(sender, ChatUtil.ChatMessage.NO_PERMISSIONS);
		}
		return true;
	}

	public void registerMissile(Missile missile) {
		if (!missiles.contains(missile)) {
			missiles.add(missile);
		}
	}
}
