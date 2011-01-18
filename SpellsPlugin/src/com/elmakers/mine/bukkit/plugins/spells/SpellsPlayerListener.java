package com.elmakers.mine.bukkit.plugins.spells;

import org.bukkit.Material;
/*
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
*/
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

public class SpellsPlayerListener extends PlayerListener 
{
	private SpellsPlugin plugin;
	
	public void setPlugin(SpellsPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	/**
     * Commands sent from in game to us.
     *
     * @param player The player who sent the command.
     * @param split The input line split by spaces.
     * @return <code>boolean</code> - True denotes that the command existed, false the command doesn't.
     */
    @Override
    public void onPlayerCommand(PlayerChatEvent event) 
    {
    	String[] split = event.getMessage().split(" ");
    	String commandString = split[0];
    	
    	if (!commandString.equalsIgnoreCase("/cast"))
    	{
    		return;
    	}
    	
    	if (split.length < 2)
    	{
    		plugin.listSpells(event.getPlayer());
    		return;
    	}

    	// No params
   
    	String spellName = split[1];
    	Spell spell = plugin.getSpell(spellName);
    	if (spell == null || spellName.equalsIgnoreCase("help") || spellName.equalsIgnoreCase("list"))
    	{
    		plugin.listSpells(event.getPlayer());
    		return;
    	}
    	
    	String[] parameters = new String[split.length - 1];
    	for (int i = 1; i < split.length; i++)
    	{
    		parameters[i - 1] = split[i];
    	}
    	
    	spell.cast(parameters, plugin, event.getPlayer());
    }
    

    /**
     * Called when a player performs an animation, such as the arm swing
     * 
     * @param event Relevant event details
     */
    /*
	@Override
    public void onPlayerAnimation(PlayerAnimationEvent event) 
	{
		if (event.getAnimationType() != PlayerAnimationType.ARM_SWING)
		{
			return;
		}
		
		// Kind of a hack for Wand compatibility, ignore the stick.
		// What we really need is a way to tell what are blocks, or a whitelist.
		ItemStack item = event.getPlayer().getInventory().getItemInHand();
		Material material = Material.AIR;
		if (item != null)
		{
			material = item.getType();
		}
		if (material != Material.STICK)
		{
			plugin.setCurrentMaterialType(event.getPlayer(), material);
		}
    }
    */
    
    /**
     * Called when a player uses (right-clicks with) an item
     * 
     * @param event Relevant event details
     */
    
	@Override
    public void onPlayerItem(PlayerItemEvent event)
	{		
		// Kind of a hack for Wand compatibility, ignore the stick.
		// What we really need is a way to tell what are blocks, or a whitelist.
		ItemStack item = event.getPlayer().getInventory().getItemInHand();
		Material material = Material.AIR;
		if (item != null)
		{
			material = item.getType();
		}
		if (material != Material.STICK)
		{
			plugin.setCurrentMaterialType(event.getPlayer(), material);
		}
    }
 
}
