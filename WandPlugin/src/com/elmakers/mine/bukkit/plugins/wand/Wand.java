package com.elmakers.mine.bukkit.plugins.wand;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class Wand 
{
	private String name;
	private String description;
	private final List<WandCommand> commands = new ArrayList<WandCommand>();
	private WandCommand currentCommand;
	
	public final List<WandCommand> getCommands()
	{
		return commands;
	}
	
	public WandCommand getCurrentCommand()
	{
		return currentCommand;
	}
	
	public void addCommand(String command)
	{
		WandCommand newCommand = new WandCommand();
		newCommand.setCommand(command);
		commands.add(newCommand);
		currentCommand = newCommand;
	}
	
	public void selectCommand(String command)
	{
		for (WandCommand lookCommand : commands)
		{
			if (lookCommand.getCommand().equalsIgnoreCase(command))
			{
				currentCommand = lookCommand;
				break;
			}
		}
	}
	
	public void use(WandPlugin plugin, Player player)
	{
		if (currentCommand != null)
		{
			currentCommand.use(plugin, player);
		}
	}
	
	public void nextCommand()
	{
		if (currentCommand != null)
		{
			int index = commands.indexOf(currentCommand);
			index = (index + 1) % commands.size();
			currentCommand = commands.get(index);
		}
	}
	
	public boolean removeCommand(String command)
	{
		WandCommand foundCommand = null;
		for (WandCommand lookCommand : commands)
		{
			if (lookCommand.getCommand().equalsIgnoreCase(command))
			{
				foundCommand = lookCommand;
				commands.remove(foundCommand);
				break;
			}
		}
		return (foundCommand != null);
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getDescription()
	{
		return description;
	}

	public String getName()
	{
		return name;
	}
}
