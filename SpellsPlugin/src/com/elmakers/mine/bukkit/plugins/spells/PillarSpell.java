package com.elmakers.mine.bukkit.plugins.spells;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class PillarSpell extends Spell 
{
	int MAX_SEARCH_DISTANCE = 255;
	
	@Override
	public boolean onCast(String[] parameters) 
	{
		Block attachBlock = getTargetBlock();
		if (attachBlock == null)
		{
			player.sendMessage("No target");
			return false;
		}	

		Block targetBlock = attachBlock.getFace(BlockFace.UP);
		
		int distance = 0;
		while (targetBlock.getType() != Material.AIR && distance <= MAX_SEARCH_DISTANCE)
		{
			distance++;
			attachBlock = targetBlock;
			targetBlock = attachBlock.getFace(BlockFace.UP);
		}
		if (targetBlock.getType() != Material.AIR)
		{
			player.sendMessage("Can't pillar any further");
			return false;
		}
		setBlockAt(attachBlock.getTypeId(), targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
		player.sendMessage("You extend your target pillar");
		//player.sendMessage("Facing " + playerRot + " : " + direction.name() + ", " + distance + " spaces to " + attachBlock.getType().name());
		
		return true;
	}

	@Override
	public String getName() 
	{
		return "pillar";
	}

	@Override
	public String getDescription() 
	{
		return "Creates a pillar at your target";
	}

	@Override
	public String getCategory() 
	{
		return "build";
	}
}
