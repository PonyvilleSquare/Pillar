package com.hepolite.pillar.database;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.entity.Player;

public class Database
{
	// Control variables
	private final static HashMap<UUID, PlayerData> playerDataMap = new HashMap<UUID, PlayerData>();

	/** Performs one tick on the database, processing everything that needs processing */
	public static void onTick()
	{
		// Trim out empty data bits
		for (Iterator<PlayerData> it = playerDataMap.values().iterator(); it.hasNext();)
		{
			PlayerData data = it.next();
			if (data.isEmpty())
				it.remove();
		}

		// Update all player data bits
		for (PlayerData playerData : playerDataMap.values())
			playerData.onTick();
	}

	/** Returns the player data associated with the given player */
	public static PlayerData getPlayerData(UUID uuid)
	{
		if (uuid == null)
			return null;
		if (!playerDataMap.containsKey(uuid))
			playerDataMap.put(uuid, new PlayerData(uuid));
		return playerDataMap.get(uuid);
	}

	/** Returns the player data associated with the given player */
	public static PlayerData getPlayerData(Player player)
	{
		if (player == null)
			return null;
		return getPlayerData(player.getUniqueId());
	}

	/** Returns a collection of all the player data bits that exists in the database */
	public static Collection<PlayerData> getPlayerData()
	{
		return playerDataMap.values();
	}
}
