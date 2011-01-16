package com.elmakers.mine.bukkit.plugins.spells;

import org.bukkit.entity.Player;

public class HealSpell extends Spell 
{

	@Override
	public boolean onCast(String[] parameters) 
	{
		player.sendMessage("You heal yourself");
		player.setHealth(20);
		return true;
	}

	@Override
	public String getName() 
	{
		return "heal";
	}

	@Override
	public String getDescription() 
	{
		return "Heal yourself";
	}

}
