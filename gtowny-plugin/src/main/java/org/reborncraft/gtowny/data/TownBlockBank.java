package org.reborncraft.gtowny.data;


import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.reborncraft.gtowny.data.local.BlockStack;
import org.reborncraft.gtowny.data.inheritable.ModifiableTownyObject;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public final class TownBlockBank implements ModifiableTownyObject {
	private final int townId;

	public int getTownId() {
		return townId;
	}

	private final ConcurrentMap<BlockStack, Integer> blockList;

	public ConcurrentMap<BlockStack, Integer> getBlockList() {
		return new ConcurrentHashMap<>(blockList);
	}

	public TownBlockBank(int townId, ConcurrentHashMap<BlockStack, Integer> blockList) {
		this.townId = townId;
		this.blockList = blockList;
	}

	public boolean addBlock(ItemStack stack) {
		return addBlock(stack.getType(), stack.getDurability(), stack.getAmount());
	}

	public boolean addBlock(Material mat, int damage, int amount) {
		try {
			BlockStack block = new BlockStack(mat, damage);
			createOrIncrement(block, amount);
			pushUpdate();
			return true;
		} catch (IllegalArgumentException ex) {
			return false;
		}
	}

	private int createOrIncrement(BlockStack block, int amount) {
		List<BlockStack> mapBlockList = blockList.keySet().stream().filter(b -> b.getMaterial() == block.getMaterial() && b.getDamage() == block.getDamage()).collect(Collectors.toList());
		BlockStack mapBlock = mapBlockList.size() >= 1 ? mapBlockList.get(0) : block;
		int adjustedAmount = (blockList.containsKey(mapBlock) ? blockList.get(mapBlock) : 0) + amount;
		if (adjustedAmount >= 1) {
			blockList.put(mapBlock, adjustedAmount);
		} else if (blockList.containsKey(mapBlock)) {
			blockList.remove(mapBlock);
		}
		return adjustedAmount;
	}

	public boolean decrement(BlockStack block) {
		int r = createOrIncrement(block, -1);
		if (r < 0) {
			createOrIncrement(block, 1);
			return false;
		}
		pushUpdate();
		return true;
	}

	public boolean decrement(BlockStack block, int amt) {
		int r = createOrIncrement(block, -amt);
		if (r < 0) {
			createOrIncrement(block, amt);
			return false;
		}
		pushUpdate();
		return true;
	}

	public int getBlock(Material mat, int damage) {
		try {
			return getBlock(new BlockStack(mat, damage));
		} catch (IllegalArgumentException e) {
			return 0;
		}
	}

	public int getBlock(BlockStack block) {
		return createOrIncrement(block, 0);
	}

	@Override
	public String toString() {
		String str = "";
		blockList.forEach((b, amt) -> str.concat(b.getMaterial() + ":" + b.getDamage() + " = " + amt + ", "));
		return str;
	}

	public Town getTown() {
		return TownyDataHandler.getTownById(townId);
	}

}
