package com.hepolite.pillar.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class Log
{
	// Control variables
	private static Logger logger = null;

	/** Initialize the log */
	public static void initialize(JavaPlugin plugin)
	{
		Log.logger = plugin.getLogger();
	}

	/** Logs a message to the console */
	public static void log(String message)
	{
		if (Log.logger != null)
			Log.logger.info(message);
	}

	/** Logs a message to the console */
	public static void log(String message, Level level)
	{
		if (Log.logger != null)
			Log.logger.log(level, message);
	}
}
