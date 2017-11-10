package co.reborncraft.stackperms.utils;

import org.bukkit.Bukkit;

public class ReflectionsUtil {
	public static Class<?> getNMSClass(String className) throws ClassNotFoundException {
		return Class.forName("net.minecraft.server." + (Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".") + className);
	}

	public static Class<?> getCBClass(String className) throws ClassNotFoundException {
		return Class.forName("org.bukkit.craftbukkit." + (Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".") + className);
	}
}
