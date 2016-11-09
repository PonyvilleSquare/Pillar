package com.hepolite.pillar;

import org.bukkit.plugin.java.JavaPlugin;

import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.listener.ListenerManager;
import com.hepolite.pillar.logging.Log;
import com.hepolite.pillar.utility.NBTAPI;

public class Pillar extends JavaPlugin
{
	// Control variables
	private static Pillar instance = null;

	private ListenerManager listenerManager;

	int scheduledTask = -1;

	@Override
	public void onEnable()
	{
		instance = this;
		Log.initialize(this);
		NBTAPI.initialize();

		listenerManager = new ListenerManager();
		getServer().getPluginManager().registerEvents(listenerManager, instance);

		// Set up a task that runs once every tick
		Runnable task = new Runnable()
		{
			@Override
			public void run()
			{
				Database.onTick();
				listenerManager.onTick();
			}
		};
		scheduledTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, task, 5, 1);
	}

	@Override
	public void onDisable()
	{
		// Disable the task thread
		if (scheduledTask != -1)
			getServer().getScheduler().cancelTask(scheduledTask);
		scheduledTask = -1;
	}

	/** Returns the Pillar plugin instance */
	public final static Pillar getInstance()
	{
		return instance;
	}

	/** Returns the ListenerManager for the plugin */
	public final ListenerManager getListenerManager()
	{
		return listenerManager;
	}
}
