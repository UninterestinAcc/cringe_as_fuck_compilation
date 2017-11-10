package io.github.loldatsec.mcplugs.opbook;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.server.v1_7_R4.NBTTagCompound;

public class OpBook extends JavaPlugin {

	public void onEnable() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					ItemStack i = p.getItemInHand();
					net.minecraft.server.v1_7_R4.ItemStack stack = CraftItemStack.asNMSCopy(i);
					if (stack != null && stack.hasTag()) {
						NBTTagCompound tag = stack.getTag();
						if (tag.hasKey("AttributeModifiers")) {
							tag.remove("AttributeModifiers");
							stack.setTag(tag);
							i = CraftItemStack.asCraftMirror(stack);
							p.setItemInHand(i);
						}
					}
				}
			}
		}, 0, 20);
	}
}
