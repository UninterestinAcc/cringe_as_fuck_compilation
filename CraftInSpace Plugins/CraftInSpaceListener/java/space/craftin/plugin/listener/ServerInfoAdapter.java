/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import space.craftin.plugin.core.CraftInSpace;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ServerInfoAdapter extends PacketAdapter {
	private List<String> lore;
	private String motd;

	public ServerInfoAdapter(Plugin plugin) {
		super(PacketAdapter.params(plugin, PacketType.Status.Server.OUT_SERVER_INFO).optionAsync());
		updateInfo();
	}

	public void updateInfo() {
		YamlConfiguration conf = CraftInSpace.getInstance().getCustomConfig("serverlistping").getValue();
		lore = (List<String>) conf.getList("lore");
		motd = conf.getString("motd");
	}

	@Override
	public void onPacketSending(PacketEvent e) {
		final WrappedServerPing wsp = e.getPacket().getServerPings().read(0);
		wsp.setPlayers(lore.stream().map(m -> new WrappedGameProfile(UUID.randomUUID(), m.replaceAll("&sect;", "\u00a7"))).collect(Collectors.toList()));
		wsp.setMotD(motd.replaceAll("&sect;", "\u00a7"));
	}
}
