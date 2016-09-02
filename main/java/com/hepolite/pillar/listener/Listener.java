package com.hepolite.pillar.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import com.hepolite.pillar.Pillar;

public abstract class Listener implements org.bukkit.event.Listener
{
	public Listener()
	{
		Pillar.getInstance().getListenerManager().registerListener(this);
	}

	/** On tick handling, called every tick */
	public void onTick()
	{
	}

	/** Posts an even to the event bus */
	protected void post(Event event)
	{
		Bukkit.getServer().getPluginManager().callEvent(event);
	}
}
