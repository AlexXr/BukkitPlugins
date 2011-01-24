package com.elmakers.mine.bukkit.plugins.wand;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.bukkit.craftbukkit.entity.CraftPlayer;

import com.elmakers.mine.bukkit.plugins.spells.SpellVariant;
import com.elmakers.mine.bukkit.plugins.spells.SpellsPlugin;


public class WandPlayerListener extends PlayerListener 
{
	private WandPlugin plugin;
	
	public void setPlugin(WandPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	 /**
     * Called when a player plays an animation, such as an arm swing
     * 
     * @param event Relevant event details
     */
	@Override
    public void onPlayerAnimation(PlayerAnimationEvent event) 
	{
		Player player = event.getPlayer();
		if (event.getAnimationType() == PlayerAnimationType.ARM_SWING)
		{
			if (event.getPlayer().getInventory().getItemInHand().getTypeId() == plugin.getWandTypeId())
			{
				WandPermissions permissions = plugin.getPermissions(player.getName());	
				if (!permissions.canUse())
				{
					return;
				}
				
				Inventory inventory = player.getInventory();
				ItemStack[] contents = inventory.getContents();
				
				if (contents[0].getType() != Material.AIR)
				{
					SpellVariant spell = plugin.getSpells().getSpell(contents[0].getType(), player.getName());
					if (spell != null)
					{
						plugin.getSpells().castSpell(spell, player);
					}
				}
			}
		}
    }
  
    /**
     * Called when a player uses an item
     * 
     * @param event Relevant event details
     */
	@Override
    public void onPlayerItem(PlayerItemEvent event) 
	{
		int materialId = event.getPlayer().getInventory().getItemInHand().getTypeId();
		if (materialId == plugin.getWandTypeId())
		{
			Player player = event.getPlayer();
			WandPermissions permissions = plugin.getPermissions(player.getName());	
			if (!permissions.canUse())
			{
				return;
			}
			
			Inventory inventory = player.getInventory();
			ItemStack[] contents = inventory.getContents();
			ItemStack[] active = new ItemStack[9];
			SpellsPlugin spells = plugin.getSpells();
			
			for (int i = 0; i < 9; i++) { active[i] = contents[i]; }
			
			int maxSpellSlot = 0;
			for (int i = 0; i < 9; i++)
			{
				if (active[i].getType() == Material.AIR) break;
				if (active[i].getTypeId() != plugin.getWandTypeId()) 
				{
					SpellVariant spell = spells.getSpell(active[i].getType(), player.getName());
					if (spell == null) break;
					maxSpellSlot++;
				}
			}
			
			if (maxSpellSlot < 2)
			{
				return;
			}
			
			maxSpellSlot++;
			
			for (int i = 0; i < maxSpellSlot; i++)
			{
				if (contents[i].getTypeId() != plugin.getWandTypeId())
				{
					for (int di = 1; di < maxSpellSlot - 1; di++)
					{
						int ni = (i + di) % maxSpellSlot;
						if (active[ni].getTypeId() != plugin.getWandTypeId())
						{
							contents[i] = active[ni];
							break;
						}
					}
				}
			}
			
			inventory.setContents(contents);
			CraftPlayer cPlayer = ((CraftPlayer)event.getPlayer());
			cPlayer.getHandle().l();
		}
		else
		{
			// Check for magic item
			Player player = event.getPlayer();
			SpellVariant spell = plugin.getSpells().getSpell(Material.getMaterial(materialId), player.getName());
			if (spell != null)
			{
				player.sendMessage(spell.getName() + " : " + spell.getDescription());
			}
		}
    }
	
	private void showHelp(Player player)
	{
		WandPermissions permissions = plugin.getPermissions(player.getName());
			
		player.sendMessage("How to use your wand:");
		player.sendMessage(" Type /spells to see what spells you know");
		player.sendMessage(" Place a spell item in your first inventory slot");
		player.sendMessage(" Left-click your wand to cast!");
		player.sendMessage(" Right-click to cycle spells in your inventory");

		if (permissions.canModify())
		{
			player.sendMessage("/wand <spellname> : Give the item necessary to cast a spell");
		}
		if (permissions.canAdminister())
		{
			player.sendMessage("/wand reload : Reload the configuration");
		}
		
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
    	Player player = event.getPlayer();
    	WandPermissions permissions = plugin.getPermissions(player.getName());

		if (!permissions.canUse())
		{
			return;
		}
		
    	String[] split = event.getMessage().split(" ");
    	String commandString = split[0];
    	
    	if (!commandString.equalsIgnoreCase("/wand"))
    	{
    		return;
    	}
    	
    	if (split.length <= 1 || !permissions.canModify())
    	{
    		showHelp(player);
    		return;
    	}
    	
    	SpellVariant spell = plugin.getSpells().getSpell(split[1], player.getName());
    	if (spell == null)
    	{
    		showHelp(player);
    		return;
    	}
    	
		ItemStack itemStack = new ItemStack(spell.getMaterial(), 1);
		player.getWorld().dropItem(player.getLocation(), itemStack);
    	
    }

}
