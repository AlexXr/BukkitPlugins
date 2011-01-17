package com.elmakers.mine.bukkit.plugins.spells;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class TowerSpell extends Spell {

	@Override
	public boolean onCast(String[] parameters) 
	{
		Block target = getTargetBlock();
		if (target == null) 
		{
			player.sendMessage("No target");
			return false;
		}
		int MAX_HEIGHT = 255;
		int height = 16;
		int maxHeight = 127;
		int material = 20;
		int midX = target.getX();
		int midY = target.getY();
		int midZ = target.getZ();
		
		// Check for roof
		for (int i = height; i < maxHeight; i++)
		{
			int y = midY + i;
			if (y > MAX_HEIGHT)
			{
				maxHeight = MAX_HEIGHT - midY;
				height = height > maxHeight ? maxHeight : height;
				break;
			}
			Block block = getBlockAt(midX, y, midZ);
			if (block.getType() != Material.AIR)
			{
				player.sendMessage("Found ceiling of " + block.getType().name().toLowerCase());
				height = i;
				break;
			}
		}
		
		int blocksCreated = 0;
		for (int i = 0; i < height; i++)
		{
			midY++;
			for (int dx = -1; dx <= 1; dx++)
			{
				for (int dz = -1; dz <= 1; dz++)
				{
					int x = midX + dx;
					int y = midY;
					int z = midZ + dz;
					// Leave the middle empty
					if (dx != 0 || dz != 0)
					{
						blocksCreated++;
						setBlockAt(material, x, y, z);
					}					
				}
			}
		}
		player.sendMessage("Made tower " + height + " high with " + blocksCreated + " blocks");
		return true;
	}

	@Override
	public String getName() 
	{
		return "tower";
	}

	@Override
	public String getDescription() 
	{
		return "Create a tower out of the specified material";
	}

}
