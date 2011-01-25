package com.elmakers.mine.bukkit.plugins.spells.builtin;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.elmakers.mine.bukkit.plugins.spells.Spell;
import com.elmakers.mine.bukkit.plugins.spells.utilities.BlockList;
import com.elmakers.mine.bukkit.plugins.spells.utilities.PluginProperties;

public class DisintigrateSpell extends Spell
{
	private int				defaultSearchDistance	= 32;

	@Override
	public boolean onCast(String[] parameters)
	{
		Block target = getTargetBlock();
		if (target == null)
		{
			castMessage(player, "No target");
			return false;
		}
		if (defaultSearchDistance > 0 && getDistance(player, target) > defaultSearchDistance)
		{
			castMessage(player, "Can't blast that far away");
			return false;
		}
		
		BlockList disintigrated = new BlockList();
		disintigrated.addBlock(target);
		
		if (isUnderwater())
		{
			target.setType(Material.STATIONARY_WATER);
		}
		else
		{
			target.setType(Material.AIR);
		}
		
		plugin.addToUndoQueue(player, disintigrated);
		castMessage(player, "ZAP!");
		
		return true;
	}

	@Override
	protected String getName()
	{
		return "disintigrate";
	}

	@Override
	public String getCategory()
	{
		return "mining";
	}

	@Override
	public String getDescription()
	{
		return "Destroy the target block";
	}

	@Override
	public Material getMaterial()
	{
		return Material.GOLD_SWORD;
	}

	@Override
	public void onLoad(PluginProperties properties)
	{
		defaultSearchDistance = properties.getInteger("spells-disintigrate-search-distance", defaultSearchDistance);
	}
}
