package com.elmakers.mine.bukkit.plugins.persistence.dao;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;

import com.elmakers.mine.bukkit.plugins.persistence.annotation.PersistField;
import com.elmakers.mine.bukkit.plugins.persistence.annotation.PersistClass;

@PersistClass(schema="global", name="area", contained=true)
public class BoundingBox
{
	public BoundingBox()
	{
		
	}
	
	public BoundingBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
	{
		min = new BlockVector(Math.min(minX, maxX), Math.min(minY, maxY), Math.min(minZ, maxZ));
		max = new BlockVector(Math.max(minX, maxX), Math.max(minY, maxY), Math.max(minZ, maxZ));
	}
	
	public BoundingBox(BlockVector min, BlockVector max)
	{
		this.min = min;
		this.max = max;
	}
	
	public boolean contains(BlockVector p)
	{
		return
		(
			(p.getBlockX() >= min.getBlockX() && p.getBlockX() <= max.getBlockX())
		&&	(p.getBlockY() >= min.getBlockY() && p.getBlockY() <= max.getBlockY())
		&&	(p.getBlockZ() >= min.getBlockZ() && p.getBlockZ() <= max.getBlockZ())
		);
	}
	
	public BlockVector getCenter()
	{
		return new BlockVector
		(
			(int)Math.floor((min.getBlockX() + max.getBlockX()) / 2),
			(int)Math.floor((min.getBlockY() + max.getBlockY()) / 2),
			(int)Math.floor((min.getBlockZ() + max.getBlockZ()) / 2)
		);
	}
	
	public int getSizeX()
	{
		return max.getBlockX() - min.getBlockX();
	}
	
	public int getSizeY()
	{
		return max.getBlockY() - min.getBlockY();
	}
	
	public int getSizeZ()
	{
		return max.getBlockZ() - min.getBlockZ();
	}
	
	public BoundingBox getFace(BlockFace face)
	{
		return getFace(face, 1, 0);
	}
	
	public BoundingBox getFace(BlockFace face, int thickness, int offset)
	{
		// Brute-force this for now. There's probably a Matrix-y way to do this!
		switch(face)
		{
			case UP: return new BoundingBox(min.getBlockX(), max.getBlockY() + offset, min.getBlockZ(), max.getBlockX(), max.getBlockY() + offset + thickness, max.getBlockZ());
			case DOWN: return new BoundingBox(min.getBlockX(), min.getBlockY() - offset - thickness, min.getBlockZ(), max.getBlockX(), min.getBlockY() - offset, max.getBlockZ());
			case WEST: return new BoundingBox(min.getBlockX(), min.getBlockY(), max.getBlockZ() + offset, max.getBlockX(), max.getBlockY(), max.getBlockZ() + offset + thickness);
			case EAST: return new BoundingBox(min.getBlockX(), min.getBlockY(), min.getBlockZ() - offset - thickness, max.getBlockX(), max.getBlockY(), min.getBlockZ() - offset);
			case SOUTH: return new BoundingBox(max.getBlockX() + offset, min.getBlockY(), min.getBlockZ(), max.getBlockX() + offset + thickness, max.getBlockY(), max.getBlockZ());
			case NORTH: return new BoundingBox(min.getBlockX() - offset - thickness, min.getBlockY(), min.getBlockZ(), min.getBlockX() - offset, max.getBlockY(), max.getBlockZ());
		}
		
		return null;
	}
	
	public void fill(World world, Material material)
	{
		fill(world, material, null);
	}
	
	public void fill(World world, Material material, HashMap<Material, ? extends Object> destructable)
	{
		for (int x = min.getBlockX(); x < max.getBlockX(); x++)
		{
			for (int y = min.getBlockY(); y < max.getBlockY(); y++)
			{
				for (int z = min.getBlockZ(); z < max.getBlockZ(); z++)
				{
					Block block = world.getBlockAt(x, y, z);
					
					if (destructable == null)
					{
						block.setType(material);
					}
					else
					{
						Material blockType = block.getType();
						if (destructable.get(blockType) != null)
						{
							block.setType(material);
						}
					}
				}
			}
		}
	}
	
	@PersistField(contained=true)
	public BlockVector getMin()
	{
		return min;
	}
	
	public void setMin(BlockVector min)
	{
		this.min = min;
	}
	
	@PersistField(contained=true)
	public BlockVector getMax()
	{
		return max;
	}
	
	public void setMax(BlockVector max)
	{
		this.max = max;
	}
	
	protected BlockVector min;
	protected BlockVector max;
	
}
