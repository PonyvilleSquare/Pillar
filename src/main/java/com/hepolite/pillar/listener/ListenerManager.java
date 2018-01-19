package com.hepolite.pillar.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.hepolite.pillar.Pillar;
import com.hepolite.pillar.utility.Damager;

public class ListenerManager implements org.bukkit.event.Listener
{
	// Control variables
	private final List<Listener> listeners = new ArrayList<Listener>();

	/** Registers a listener to the manager */
	public final void registerListener(Listener listener)
	{
		if (listener != null)
		{
			listeners.add(listener);
			Pillar.getInstance().getServer().getPluginManager().registerEvents(listener, Pillar.getInstance());
		}
	}

	/** Called each tick */
	public void onTick()
	{
		for (Listener listener : listeners)
			listener.onTick();
	}

	/** Posts an even to the event bus */
	public final static void post(Event event)
	{
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	/** Handles custom death messages when a player dies, if relevant */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDie(PlayerDeathEvent event)
	{
		String message = Damager.getNextDeathMessage();
		if (message == null)
			return;

		message = message.replaceAll("<player>", event.getEntity().getDisplayName() + ChatColor.RESET);
		event.setDeathMessage(message);
		Damager.setNextDeathMessage(null);
	}
}
