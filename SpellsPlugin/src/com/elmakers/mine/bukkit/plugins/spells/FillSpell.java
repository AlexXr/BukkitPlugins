package com.elmakers.mine.bukkit.plugins.spells;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.elmakers.mine.bukkit.utilities.PluginProperties;
import com.elmakers.mine.bukkit.utilities.UndoableBlock;

public class FillSpell extends Spell 
{
	int maxDimension = 128;
	int maxVolume = 512;
	Block target = null;
	
	@Override
	public boolean onCast(String[] parameters) 
	{
		Block targetBlock = getTargetBlock();
		Material material = plugin.finishMaterialUse(player);
		if (targetBlock == null) 
		{
			plugin.castMessage(player, "No target");
			return false;
		}
		
		if (target != null)
		{			
			int deltax = targetBlock.getX() - target.getX();
			int deltay = targetBlock.getY() - target.getY();
			int deltaz = targetBlock.getZ() - target.getZ();
			
			int absx = Math.abs(deltax);
			int absy = Math.abs(deltay);
			int absz = Math.abs(deltaz);
		
			if (maxDimension > 0 && (absx > maxDimension || absy > maxDimension || absz > maxDimension))
			{
				player.sendMessage("Dimension is too big!");
				return false;
			}

			if (maxVolume > 0 && absx * absy * absz > maxVolume)
			{
				player.sendMessage("Volume is too big!");
				return false;
			}
			
			int dx = (int)Math.signum(deltax);
			int dy = (int)Math.signum(deltay);
			int dz = (int)Math.signum(deltaz);
			
			absx++;
			absy++;
			absz++;
			
			BlockList filledBlocks = new BlockList();
			plugin.castMessage(player, "Filling " + absx + "x" + absy + "x" + absz + " area with " + material.name().toLowerCase());
			int x = target.getX();
			int y = target.getY();
			int z = target.getZ();
			for (int ix = 0; ix < absx; ix++)
			{
				for (int iy = 0; iy < absy; iy++)
				{
					for (int iz = 0; iz < absz; iz++)
					{
						Block block = getBlockAt(x + ix * dx, y + iy * dy, z + iz * dz);
						UndoableBlock undoBlock = filledBlocks.addBlock(block);
						block.setType(material);
						undoBlock.update();
					}
				}
			}
			plugin.addToUndoQueue(player, filledBlocks);
			
			target = null;
			return true;
		}
		else
		{
			target = targetBlock;
			plugin.startMaterialUse(player, target.getType());
			player.sendMessage("Cast again to fill with " + target.getType().name().toLowerCase());
			return true;
		}
	}
	
	@Override
	public void onCancel()
	{
		if (target != null)
		{
			player.sendMessage("Cancelled fill");
			target = null;
		}
	}

	@Override
	public String getName() 
	{
		return "fill";
	}

	@Override
	public String getDescription() 
	{
		return "Fills a selected area (2 clicks)";
	}

	@Override
	public String getCategory() 
	{
		return "construction";
	}

	@Override
	public void onLoad(PluginProperties properties)
	{
		maxDimension = properties.getInteger("spells-fill-max-dimension", maxDimension);
		maxVolume = properties.getInteger("spells-fill-max-volume", maxVolume);
	}
}
