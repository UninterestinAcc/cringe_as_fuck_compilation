package org.reborncraft.gtowny.data.local;


import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class BlockStack {
	private final Material block;
	private final int damage;

	@SuppressWarnings ("deprecation")
	public BlockStack(int blockId, int damage) throws IllegalArgumentException {
		this(Material.getMaterial(blockId), damage);
	}

	public BlockStack(ItemStack stack) throws IllegalArgumentException {
		this(stack.getType(), stack.getDurability());
	}

	public BlockStack(Material mat, int damage) throws IllegalArgumentException {
		if (!mat.isBlock()) {
			throw new IllegalArgumentException("Not a block.");
		}
		this.block = mat;
		this.damage = damage;
	}


	@SuppressWarnings ("deprecation")
	public int getBlockId() {
		return block.getId();
	}

	public Material getMaterial() {
		return block;
	}

	public int getDamage() {
		return damage;
	}

	@Override
	public boolean equals(Object anotherObject) {
		return anotherObject instanceof BlockStack && this.hashCode() == anotherObject.hashCode() && this.getBlockId() == ((BlockStack) anotherObject).getBlockId() && this.getDamage() == ((BlockStack) anotherObject).getDamage();
	}

	@Override
	public int hashCode() {
		return (int) (getBlockId() * 1e5 + getDamage());
	}

	public BlockStack forHashCode(int hash) {
		return new BlockStack((int) (hash / 1e5), (int) (hash % 1e5));
	}
}
