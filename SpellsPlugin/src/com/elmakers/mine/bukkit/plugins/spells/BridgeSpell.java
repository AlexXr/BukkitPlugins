package com.elmakers.mine.bukkit.plugins.spells;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BridgeSpell extends Spell 
{
	int MAX_SEARCH_DISTANCE = 16;
	
	@Override
	public boolean onCast(String[] parameters) 
	{
		Block playerBlock = getPlayerBlock();
		if (playerBlock == null) 
		{
			// no spot found to bridge
			player.sendMessage("You need to be standing on something");
			return false;
		}
		
		BlockFace direction = getPlayerFacing();
		Block attachBlock = playerBlock;
		Block targetBlock = attachBlock.getFace(direction);
		int distance = 0;
		while (targetBlock.getType() != Material.AIR && distance <= MAX_SEARCH_DISTANCE)
		{
			distance++;
			attachBlock = targetBlock;
			targetBlock = attachBlock.getFace(direction);
		}
		if (targetBlock.getType() != Material.AIR)
		{
			player.sendMessage("Can't bridge any further");
			return false;
		}
		setBlockAt(attachBlock.getTypeId(), targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
		player.sendMessage("A bridge extends!");
		//player.sendMessage("Facing " + playerRot + " : " + direction.name() + ", " + distance + " spaces to " + attachBlock.getType().name());
		
		return true;
	}

	@Override
	public String getName() 
	{
		return "bridge";
	}

	@Override
	public String getDescription() 
	{
		return "Extends the ground underneath you";
	}

	@Override
	public String getCategory() 
	{
		return "build";
	}
}
