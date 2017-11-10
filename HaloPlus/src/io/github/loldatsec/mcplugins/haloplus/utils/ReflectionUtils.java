package io.github.loldatsec.mcplugins.haloplus.utils;

import org.bukkit.Bukkit;

public class ReflectionUtils {

	public static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
		String name = "net.minecraft.server." + version + nmsClassString;
		Class<?> nmsClass = Class.forName(name);
		return nmsClass;
	}
}
