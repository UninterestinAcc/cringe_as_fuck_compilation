/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import space.craftin.plugin.core.CraftInSpace;

public class CraftInSpaceListenerPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new CraftInSpaceListener(), this);
		CraftInSpace.getInstance().getProtoManager().addPacketListener(new ServerInfoAdapter(this));
	}
}
