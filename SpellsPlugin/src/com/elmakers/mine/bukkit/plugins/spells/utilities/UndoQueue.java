package com.elmakers.mine.bukkit.plugins.spells.utilities;

import java.util.LinkedList;

import org.bukkit.block.Block;


public class UndoQueue
{
	private final LinkedList<BlockList> blockQueue = new LinkedList<BlockList>();
	private int maxSize = 0;
	
	public void add(BlockList blocks)
	{
		if (maxSize > 0 && blockQueue.size() > maxSize)
		{
			blockQueue.removeFirst();
		}
		blockQueue.add(blocks);
	}
	
	public boolean undo()
	{
		if (blockQueue.size() == 0) return false;
		
		BlockList blocks = blockQueue.removeLast();
		blocks.undo();
		return true;
	}
	
	public boolean undo(Block target)
	{
		if (blockQueue.size() == 0) return false;
		for (BlockList blocks : blockQueue)
		{
			if (blocks.contains(target))
			{
				blockQueue.remove(blocks);
				blocks.undo();
				return true;
			}
		}
		return false;
	}
	
	public void setMaxSize(int size)
	{
		maxSize = size;
	}
	
	public BlockList getLast()
	{
		if (blockQueue.isEmpty()) return null;
		return blockQueue.getLast();
	}
}
