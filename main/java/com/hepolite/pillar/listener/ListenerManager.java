package com.hepolite.pillar.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import com.hepolite.pillar.Pillar;

public class ListenerManager
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
}
