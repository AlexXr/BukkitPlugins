package com.elmakers.mine.bukkit.plugins.persistence.messages;

import org.bukkit.plugin.Plugin;

import com.elmakers.mine.bukkit.plugins.persistence.Persistence;
import com.elmakers.mine.bukkit.plugins.persistence.messages.dao.Command;
import com.elmakers.mine.bukkit.plugins.persistence.messages.dao.Message;

public class Messaging
{
	public void initialize(Persistence persistence)
	{
		this.persistence = persistence;
	}
	
	public Message getMessage(String id, String defaultString)
	{
		Message message = persistence.get(id, Message.class);
		if (message == null)
		{
			message = new Message(id, defaultString);
			persistence.put(message);
		}
		return message;
	}
	
	public Command getCommand(Plugin plugin, String id)
	{
		Command command = persistence.get(id, Command.class);
		if (command == null)
		{
			command = new Command(plugin, id, id);
			persistence.put(command);
		}
		return command;
	}
	
	private Persistence persistence;
}
