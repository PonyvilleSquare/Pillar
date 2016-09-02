package com.hepolite.pillar.chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Chat
{
	/** Sends the given message to the given player; will use & as the alternative chat color code */
	public final static void message(Player player, String message)
	{
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
}
