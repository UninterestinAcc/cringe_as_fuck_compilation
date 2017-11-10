/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.api.physical.objects;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;
import space.craftin.plugin.core.CraftInSpace;
import space.craftin.plugin.utils.VectorUtil;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.*;

public interface IBlock {
	// Note Total Height is 1.8
	double FULL_ARMORSTAND_HEIGHT = 1.85;
	double EYE_HEIGHT = 1.65;
	double HEAD_WIDTH = 0.64;
	String NAME_PREFIX = "CISBlock_";

	static ItemStack getSkullForType(BlockType type) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		headMeta.setOwner(NAME_PREFIX + type.getId());
		try { // Reflection field access, not neat, but does the job.
			Field profF = headMeta.getClass().getDeclaredField("profile");
			profF.setAccessible(true);
			profF.set(headMeta, getGameProfile(type));
		} catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		headMeta.setDisplayName(type.getName());
		headMeta.setLore(type.getLores());
		head.setItemMeta(headMeta);
		return head;
	}

	static GameProfile getGameProfile(BlockType type) {
		GameProfile prof = new GameProfile(UUID.fromString("00000000-0000-3000-0000-C150" + new BigInteger(1, type.getId().getBytes())), NAME_PREFIX + type.getId());
		prof.getProperties().put("textures", new Property("textures", Base64.encodeBase64String((
				"{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/" + type.getSkinHash() + "\"}}}"
		).getBytes())));
		return prof;
	}

	void delete();

	Location getLocation();

	default void playParticle(Vector vec, Effect eff) {
		for (double d = VectorUtil.strength(vec); d > 0; d -= 0.05) {
			CraftInSpace.getInstance().playParticle(getEntity().getLocation().add(0, EYE_HEIGHT, 0).add(VectorUtil.setForce(vec, d)), eff);
		}
	}

	ArmorStand getEntity();

	BlockType getType();

	default void teleport(Location location) {
		getEntity().teleport(location);
	}

	enum BlockType { // !! Must be a 4 character string for the id.
		STEEL_BLOCK(ChatColor.GRAY + "Steel block", "Stel", 0, Collections.singletonList("\u00a77General purpose block, a good block for structures."), "9f17e1bddad25edb9a5178a1d67dc825a47b9126353c8ac237bfcaf175f836ee"),

		REACTOR_MK_I(ChatColor.DARK_GREEN + "Mark I Reactor", "Rea1", 10, Collections.singletonList("\u00a77The Mark I Reactor converts \u00a7a1 \u00a77unit of uranium into \u00a7a10 \u00a77units of energy."), "ad65dbf2382668642e9650cebfce9998286cb3e51b83448c2d2da21b7dd8"),
		REACTOR_MK_II(ChatColor.DARK_GREEN + "Mark II Reactor", "Rea2", 30, Collections.singletonList("\u00a77The Mark II Reactor converts \u00a7a2 \u00a77units of uranium into \u00a7a30 \u00a77units of energy."), "d0d9bf9480c529478e5b3ea75f152df4e84b2aabc07c48aec3836f7a3cfe2e9c"),
		REACTOR_MK_III(ChatColor.DARK_GREEN + "Mark III Reactor", "Rea3", 80, Collections.singletonList("\u00a77The Mark III Reactor converts \u00a7a5 \u00a77units of uranium into \u00a7a80 \u00a77units of energy."), "15d7ed30be11f93b41dc74482ab746f0e0653317e7763cefddf4b555bf57b1"),
		GYROSCOPE(ChatColor.YELLOW + "Gyroscope", "Gyro", -5, Collections.singletonList("\u00a77Device used to rotate a ship."), "f66e3e8b1359a9d6a138b5e93569aab9b63f818230d773af7fdf8b2f1ba75bd"),

		ROCKET_ENGINE(ChatColor.RED + "Rocket Engine", "REng", -10, Collections.singletonList("\u00a77Ancient technology from the 1960s, at least it's still working."), "e053326e12aeb835f3584668fafc5ab5f344aba5f83fda72e695ff8b594b871"), // Lava Particle,
		PLASMA_ENGINE(ChatColor.BLUE + "Plasma Engine", "PEng", -15, Collections.singletonList("\u00a77A more modern engine sourced from the early 2000s, uses a lot less energy to fire up."), "df74ea80c5aaeba6f3d9ab1b5b61e4f164d4b53149de9fabcb0b5f36bb4e89b"), // Aqua fireworks particle
		ELECTRON_ENGINE(ChatColor.AQUA + "Electron Engine", "EEng", -30, Collections.singletonList("\u00a77The most powerful & efficient engine known to man."), "361fe9a788afb6d31854ed6dbc6b37312396f733e242b1353f4fc46c805"), // Magic Crit Particle

		MISSILE_MICROCONTROLLER(ChatColor.GRAY + "Missile Micro-controller", "MiMc", 0, Collections.singletonList("\u00a77Allows adjustment maneuvers when the stage is in use."), "c97f32363b1c97442911c78d4fa6621c7b9e79dc86e7948d31256f42ef9665c"),
		MISSILE_STAGE(ChatColor.DARK_GRAY + "Missile Stage", "MisS", 0, Collections.emptyList(), "f1c0d7d8419aefd448f8c6aae116b37368a8c88594cf1ecafe6a31e6281"),

		NUCLEAR_WARHEAD(ChatColor.GRAY + "Nuclear Warhead", "NukW", 0, Arrays.asList("\u00a77The classic warhead.", "", "\u00a7dEnergy consumption: \u00a78[\u00a76+\u00a77--\u00a78]"), "3ebd7e73575e68e5921de9f7644e847d3fd4c7ee74be8f6f74d5379ec27da2"),
		TRIDENT_WARHEAD(ChatColor.GRAY + "Trident Warhead", "TriW", 0, Arrays.asList("\u00a77This warhead splits into 4 payloads before impact.", "", "\u00a7dEnergy consumption: \u00a78[\u00a76++\u00a77-\u00a78]"), "67edfd215f86e51a3248cfe5e891b5ffe82e8c675a6eac9e33c3943f794f3"),
		TSAR_WARHEAD(ChatColor.GRAY + "Tsar Warhead", "TsaW", 0, Arrays.asList("\u00a77The most powerful warhead ever created.", "", "\u00a7dEnergy consumption: \u00a78[\u00a76+++\u00a78]"), "d6a47ce4ee5dde2420f2d29b69a9f7dc90373541a3caa46d60c9193f86f79b4"),

		TRIDENT_PAYLOAD(ChatColor.GRAY + "Trident Payload", "TrPL", 0, Collections.singletonList("\u00a77This is the payload which the trident warhead splits into."), "6f3c29e5ef85d4011467e8dd7274eff3a1e661ba23cf7ee7dabf4c206069"),

		SHIP_CONTROLLER(ChatColor.GRAY + "Ship Controller", "ShpC", 0, Collections.singletonList("\u00a77A ship's starting component, destroying this block destroys the ship."), "862bd3a3a73144abdb3ff80493f886b7222b784cbc9f195f734aeb88f9ddc4"),
		// Plan Design the heads
		DRONE_COMPUTER(ChatColor.GRAY + "Drone Computer", "DroC", 0, Collections.singletonList("\u00a77A drone's starting component, destroying this block destroys the drone."), "e9eb9da26cf2d3341397a7f4913ba3d37d1ad10eae30ab25fa39ceb84bc"),

		EXCIMER_LASER_MOUNT(ChatColor.DARK_RED + "Excimer Laser Mount", "Lasr", -20, Collections.singletonList("\u00a77Heavy duty laser mount."), "e9eb9da26cf2d3341397a7f4913ba3d37d1ad10eae30ab25fa39ceb84bc"),

		MISSILE_LAUNCHER(ChatColor.GOLD + "Missile Launcher", "MisL", -50, Collections.singletonList("\u00a77Heavy duty missile launcher."), "e9eb9da26cf2d3341397a7f4913ba3d37d1ad10eae30ab25fa39ceb84bc"),

		WEAPONS_AUTOMATION_CONTROLLER(ChatColor.GRAY + "Automatic Weapons Controller", "AWCo", -5, Collections.singletonList("\u00a77Will make weapons in a 5 block radius fire upon foreign objects."), "e9eb9da26cf2d3341397a7f4913ba3d37d1ad10eae30ab25fa39ceb84bc");

		private final String name;
		private final String id;
		private final long energyRating;
		private final List<String> lores;
		private final String skinHash;

		BlockType(String name, String id, long energyRating, List<String> lores, String skinHash) {
			this.name = name;
			this.id = id;
			this.energyRating = energyRating;
			this.lores = new ArrayList<>();
			this.skinHash = skinHash;
			this.lores.addAll(lores);
			if (energyRating != 0) {
				this.lores.add("");
				this.lores.add("\u00a77Energy " + (energyRating > 0 ? "production\u00a77: \u00a78[\u00a7a" : "consumption\u00a77: \u00a78[\u00a7c") + Math.abs(energyRating) + "\u00a78]");
			}
		}

		public static BlockType forName(String name) {
			return (name.toLowerCase().startsWith(NAME_PREFIX.toLowerCase())) ? forId(name.substring(NAME_PREFIX.length())) : null;
		}

		public static BlockType forId(String id) {
			Optional<BlockType> type = Arrays.stream(values()).filter(t -> t.getId().equalsIgnoreCase(id)).findFirst();
			return type.isPresent() ? type.get() : null;
		}

		public static BlockType forItem(ItemStack helmet) {
			return forName(((SkullMeta) helmet.getItemMeta()).getOwner());
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id;
		}

		public ItemStack getSkull() {
			return getSkullForType(this);
		}

		public List<String> getLores() {
			return new ArrayList<>(lores);
		}

		public String getSkinHash() {
			return skinHash;
		}

		public long getEnergyRating() {
			return energyRating;
		}
	}
}
