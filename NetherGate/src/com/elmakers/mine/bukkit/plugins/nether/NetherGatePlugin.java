package com.elmakers.mine.bukkit.plugins.nether;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.elmakers.mine.bukkit.plugins.nether.dao.Nether;
import com.elmakers.mine.bukkit.plugins.persistence.Messaging;
import com.elmakers.mine.bukkit.plugins.persistence.Persistence;
import com.elmakers.mine.bukkit.plugins.persistence.PersistencePlugin;
import com.elmakers.mine.bukkit.plugins.persistence.dao.Message;
import com.elmakers.mine.bukkit.plugins.persistence.dao.PluginCommand;
import com.elmakers.mine.bukkit.plugins.persistence.dao.Position;

public class NetherGatePlugin extends JavaPlugin
{

	public NetherGatePlugin(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder,
			File plugin, ClassLoader cLoader)
	{
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
	}

	@Override
	public void onDisable()
	{
		
	}

	@Override
	public void onEnable()
	{
		try
		{
			initialize();
			PluginDescriptionFile pdfFile = this.getDescription();
	        log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled");
		}
		catch(Throwable e)
		{
			PluginDescriptionFile pdfFile = this.getDescription();
	        log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " failed to initialize");	
	        e.printStackTrace();
		}
		
		// Hook up event listeners
		PluginManager pm = getServer().getPluginManager();
		
        pm.registerEvent(Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        pm.registerEvent(Type.CHUNK_LOADED, worldListener, Priority.Normal, this);
  }
	
	public void initialize()
	{
		Plugin checkForPersistence = this.getServer().getPluginManager().getPlugin("Persistence");
	    if(checkForPersistence != null) 
	    {
	    	PersistencePlugin plugin = (PersistencePlugin)checkForPersistence;
	    	persistence = plugin.getPersistence();
	    } 
	    else 
	    {
	    	log.warning("The NetherGate plugin depends on Persistence");
	    	this.getServer().getPluginManager().disablePlugin(this);
	    	return;
	    }
	    
	    messaging = persistence.getMessaging(this);
	    manager.initialize(persistence, messaging);
	    
		netherCommand = messaging.getPlayerCommand("nether", "Manage Nether areas", "nether <command>");
		createCommand = netherCommand.getSubCommand("create", "Create a new Nether underground", "create");
		kitCommand = netherCommand.getSubCommand("kit", "Give yourself a portal kit", "kit");
		
		createCommand.bind("onCreate");
		netherCommand.bind("onNether");
		kitCommand.bind("onKit");
		
		creationFailedMessage = messaging.getMessage("creationFailed", "Nether creation failed- is there enough room below you?");
		creationSuccessMessage = messaging.getMessage("creationSuccess", "Created new Nether area");
		netherExistsMessage = messaging.getMessage("netherExist", "A Nether area already exists here");
		giveKitMessage = messaging.getMessage("giveKit", "Happy portaling!");
	}
	
	public boolean onNether(Player player, String[] parameters)
	{
		// Currently only ops can use nether
		// TODO: Implement permissions
		if (!player.isOp()) return false;
		
		netherCommand.sendHelp(player, "Use : ", true, true);
		
		return true;
	}
	
	public boolean onKit(Player player, String[] parameters)
	{
		PlayerInventory inventory = player.getInventory();
		
		// Give a bit of obsidian
		ItemStack itemStack = new ItemStack(Material.OBSIDIAN, 32);
		inventory.addItem(itemStack);
		
		// And a flint and steel, if they don't have one
		if (!inventory.contains(Material.FLINT_AND_STEEL))
		{
			itemStack = new ItemStack(Material.FLINT_AND_STEEL, 1);
			player.getInventory().addItem(itemStack);
		}
		
		// And a diamond pickaxe (for destroying), if they don't have one
		if (!inventory.contains(Material.DIAMOND_PICKAXE))
		{
			itemStack = new ItemStack(Material.DIAMOND_PICKAXE, 1);
			player.getInventory().addItem(itemStack);
		}
		
		return true;
	}
	
	public boolean onCreate(Player player, String[] parameters)
	{
		// Currently only ops can use nether
		// TODO: Implement permissions
		if (!player.isOp()) return false;
		
		// Check for an existing Nether area
		Nether nether = manager.getNether(new Position(player.getLocation()));
		if (nether != null)
		{
			netherExistsMessage.sendTo(player);
			return true;
		}
		
		if (!manager.create(player))
		{
			creationFailedMessage.sendTo(player);
		}
		else
		{
			creationSuccessMessage.sendTo(player);
		}
		
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		return messaging.dispatch(this, sender, cmd.getName(), args);
	}

	protected PluginCommand netherCommand;
	protected PluginCommand createCommand;
	protected PluginCommand kitCommand;
	
	protected Message creationFailedMessage;
	protected Message creationSuccessMessage;
	protected Message netherExistsMessage;
	protected Message giveKitMessage;
	
	protected NetherManager manager = new NetherManager();
	protected NetherPlayerListener playerListener = new NetherPlayerListener(manager);
	protected NetherWorldListener worldListener = new NetherWorldListener(manager);
	
	protected Persistence persistence = null;
	protected Messaging messaging = null;

	protected static final Logger log = Logger.getLogger("Minecraft");
}
