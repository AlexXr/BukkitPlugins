package com.elmakers.mine.bukkit.plugins.spells.builtin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.elmakers.mine.bukkit.plugins.spells.Spell;
import com.elmakers.mine.bukkit.plugins.spells.utilities.BlockList;
import com.elmakers.mine.bukkit.plugins.spells.utilities.UndoableBlock;

public class TransmuteSpell extends Spell
{

	@Override
	public boolean onCast(String[] parameters)
	{	
		BlockList transmuteAction = null;

		/*
		 * Use target if targeting
		 */
		boolean usedTarget = false;
		targetThrough(Material.GLASS);
		Block target = getTargetBlock();
		
		if (target != null)
		{
			transmuteAction = spells.getLastBlockList(player.getName(), target);
			usedTarget = transmuteAction != null;
		}

		if (transmuteAction == null)
		{
			transmuteAction = spells.getLastBlockList(player.getName());
		}
		
		if (transmuteAction == null)
		{
			sendMessage(player, "Nothing to transmute");
			return false;
		}
		
		ItemStack targetItem = getBuildingMaterial(true);
		if (targetItem == null)
		{
			sendMessage(player, "Nothing to transmute with");
			return false;
		}
		
		Material material = targetItem.getType();
		byte data = 0;
		MaterialData targetData = targetItem.getData();
		if (targetData != null)
		{
			data = targetData.getData();
		}
				
		for (UndoableBlock undoBlock : transmuteAction.getBlocks())
		{
			Block block = undoBlock.getBlock();
			block.setType(material);
			block.setData(data);
		}
		
		if (usedTarget)
		{
			castMessage(player, "You transmute your target structure to " + material.name().toLowerCase());
		}
		else
		{
			castMessage(player, "You transmute your last structure to " + material.name().toLowerCase());
		}
		
		return true;
	}

	@Override
	public String getName()
	{
		return "transmute";
	}

	@Override
	public String getCategory()
	{
		return "construction";
	}

	@Override
	public String getDescription()
	{
		return "Modify your last construction";
	}

	@Override
	public Material getMaterial()
	{
		return Material.GOLD_INGOT;
	}

}
