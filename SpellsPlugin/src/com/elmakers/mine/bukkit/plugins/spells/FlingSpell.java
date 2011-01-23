package com.elmakers.mine.bukkit.plugins.spells;

import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.util.Vector;

public class FlingSpell extends Spell
{
	private final int magnitude = 20;

	@Override
	public boolean onCast(String[] parameters)
	{
		Vector velocity = getAimVector();
		velocity.multiply(magnitude);
		CraftPlayer craftPlayer = (CraftPlayer)player;
		craftPlayer.setVelocity(velocity);
		castMessage(player, "Whee!");
		return true;
	}

	@Override
	public String getName()
	{
		return "fling";
	}

	@Override
	public String getCategory()
	{
		return "WIP";
	}

	@Override
	public String getDescription()
	{
		return "Sends you flying in the target direction";
	}

	@Override
	public Material getMaterial()
	{
		return Material.LEATHER_BOOTS;
	}

}
