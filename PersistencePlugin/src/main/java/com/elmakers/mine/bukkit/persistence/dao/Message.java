package com.elmakers.mine.bukkit.persistence.dao;

import org.bukkit.command.CommandSender;

import com.elmakers.mine.bukkit.persistence.annotation.PersistClass;
import com.elmakers.mine.bukkit.persistence.annotation.PersistField;

/**
 * A class to encapsulate and store in-game text.
 * 
 * This class will store in-game messages in the data store, separated by plugin.
 * 
 * Each message can be parameterized, printf-style. The character '\r' will cause a newline,
 * allowing for multiline output.
 * 
 * @author NathanWolf
 *
 */

@PersistClass(schema="global", name="message")
public class Message extends Persisted
{	
	
	/**
	 * The default constructor, used by Persistence to create new instances.
	 */
	public Message()
	{
	}
	
	/**
	 * Create a new Message with an id and message.
	 * 
	 * It is easier to use Messaging.getMessage, rather than create your own instances.
	 * 
	 * @param id The id of this message
	 * @param message The message to display
	 */
	public Message(PluginData plugin, String id, String message)
	{
		this.plugin = plugin;
		this.enabled = true;
		this.messageId = id;
		this.message = message;
	}
	
	/**
	 * Send this message to the specfieid player
	 * 
	 * The message can optionally be parameterized- but the parameters will need to match
	 * the string signature, For instance:
	 * 
	 * sendTo(player, player.getName());
	 * 
	 * Would work for the message "Hello, %s!".
	 * 
	 * The "\r" character will create new lines in the output.
	 * 
	 * @param receiver The CommandSender to send the message to
	 * @param parameters A list of optional parameters to parameterize the string with
	 */
	public void sendTo(CommandSender receiver, Object ... parameters)
	{
		if (!enabled) return;
		String[] lines = getLines(parameters);
		for (String line : lines)
		{
			receiver.sendMessage(line);
		}
	}
	
	/**
	 * Parameterize this message and break it up into multiple lines.
	 *
	 * @param parameters A list of optional parameters to parameterize the string with.
	 * @return A list of strings representing the lines this message would send
	 */
	public String[] getLines(Object ... parameters)
	{
		String baseMessage;
		try
		{
			baseMessage = String.format(message, parameters);
		}
		catch(Throwable e)
		{
			baseMessage = message;
		}
		return baseMessage.split("\r");
	}
	
	/* Return the basic message
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return message;
	}
	
	@PersistField(id=true, auto=true)
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	@PersistField
	public String getMessage()
	{
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	@PersistField
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	@PersistField
	public void setMessageId(String messageId)
	{
		this.messageId = messageId;
	}

	public String getMessageId()
	{
		return messageId;
	}

	@PersistField
	public void setLevel(MessageLevel level)
	{
		this.level = level;
	}

	public MessageLevel getLevel()
	{
		return level;
	}

	@PersistField
	public void setPlugin(PluginData plugin)
	{
		this.plugin = plugin;
	}

	public PluginData getPlugin()
	{
		return plugin;
	}

	private int				id;
	private MessageLevel	level;
	private PluginData		plugin;
	private String			messageId;
	private String			message;
	private boolean			enabled;
}
