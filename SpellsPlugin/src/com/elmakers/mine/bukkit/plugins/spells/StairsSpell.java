package com.elmakers.mine.bukkit.plugins.spells;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.elmakers.mine.bukkit.utilities.PluginProperties;
import com.elmakers.mine.bukkit.utilities.UndoableBlock;

public class StairsSpell extends Spell
{
	static final String DEFAULT_DESTRUCTIBLES = "1,3,10,11,12,13";
	
	private List<Material> destructibleMaterials = new ArrayList<Material>();
	private int defaultDepth = 4;
	private int defaultWidth = 3;
	private int defaultHeight = 3;
	private int torchFrequency = 4;
	
	@Override
	public boolean onCast(String[] parameters)
	{
		Block targetBlock = getTargetBlock();
		if (targetBlock == null) 
		{
			castMessage(player, "No target");
			return false;
		}
		
		createStairs(targetBlock);
		
		return true;
	}
	
	protected void createStairs(Block targetBlock)
	{
		BlockFace vertDirection = BlockFace.UP;
		BlockFace horzDirection = getPlayerFacing();
		
		int depth = defaultDepth;
		int height = defaultHeight;
		int width = defaultWidth;
		
		BlockList tunneledBlocks = new BlockList();
		BlockList stairBlocks = new BlockList();
		Material fillMaterial = targetBlock.getType();
		
		BlockFace toTheLeft = goLeft(horzDirection);
		BlockFace toTheRight = goRight(horzDirection);
		Block bottomBlock = targetBlock;
		Block bottomLeftBlock = bottomBlock;
		for (int i = 0; i < width / 2; i ++)
		{
			bottomLeftBlock = bottomLeftBlock.getFace(toTheLeft);
		}
		
		targetBlock = bottomLeftBlock;
		Material stairsMaterial = Material.COBBLESTONE_STAIRS;
		
		for (int d = 0; d < depth; d++)
		{
			bottomBlock = bottomLeftBlock;
			for (int w = 0; w < width; w++)
			{
				targetBlock = bottomBlock;
				for (int h = 0; h < height; h++)
				{
					if (isDestructible(targetBlock))
					{
						// Check to see if the torch will stick to the wall
						// TODO: Check for glass, other non-sticky types.
						Block checkBlock = null;
						if (w == 0)
						{
							checkBlock = targetBlock.getFace(toTheLeft);
						}
						else
						{
							checkBlock = targetBlock.getFace(toTheRight);
						}
						// Put torches on the left and right wall 
						boolean useTorch = 
						(
								torchFrequency > 0 
						&& 		(w == 0 || w == width - 1) 
						&& 		(h == 1)
						&& 		(d % torchFrequency == 0)
						&&		checkBlock.getType() != Material.AIR
						);
						boolean useStairs = (h == 0);
						UndoableBlock undoBlock = null;
						if (useStairs)
						{
							undoBlock = stairBlocks.addBlock(targetBlock);
							targetBlock.setType(stairsMaterial);
						}
						else
						if (useTorch)
						{
							undoBlock = tunneledBlocks.addBlock(targetBlock);
							targetBlock.setType(Material.TORCH);
						}
						else
						{
							undoBlock = tunneledBlocks.addBlock(targetBlock);
							targetBlock.setType(Material.AIR);
						}
						undoBlock.update();
						Block standingBlock = targetBlock.getFace(BlockFace.DOWN);
						if (standingBlock.getType() == Material.AIR)
						{
							UndoableBlock standBlock = stairBlocks.addBlock(standingBlock);
							standingBlock.setType(fillMaterial);
							standBlock.update();
						}
					}
					targetBlock = targetBlock.getFace(BlockFace.UP);
				}
				bottomBlock = bottomBlock.getFace(toTheRight);
			}
			bottomLeftBlock = bottomLeftBlock.getFace(horzDirection);
			bottomLeftBlock = bottomLeftBlock.getFace(vertDirection);
		}

		plugin.addToUndoQueue(player, tunneledBlocks);
		plugin.addToUndoQueue(player, stairBlocks);
		castMessage(player, "Tunneled through " + tunneledBlocks.getCount() + "blocks and created " + stairBlocks.getCount() + " stairs");
	}	
	
	protected void createSpiralStairs(Block targetBlock)
	{
		// TODO
	}
	
	public boolean isDestructible(Block block)
	{
		if (block.getType() == Material.AIR) return true;
		
		for (Material material : destructibleMaterials)
		{
			if (material == block.getType())
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String getName()
	{
		return "stairs";
	}

	@Override
	public String getCategory()
	{
		return "construction";
	}

	@Override
	public String getDescription()
	{
		return "Construct some stairs";
	}
	
	@Override
	public void onLoad(PluginProperties properties)
	{
		destructibleMaterials = properties.getMaterials("spells-stairs-destructible", DEFAULT_DESTRUCTIBLES);
		defaultDepth = properties.getInteger("spells-stairs-depth", defaultDepth);
		defaultWidth = properties.getInteger("spells-stairs-width", defaultWidth);
		defaultHeight = properties.getInteger("spells-stairs-height", defaultHeight);
		torchFrequency = properties.getInteger("spells-stairs-torch-frequency", torchFrequency);
	}

}
