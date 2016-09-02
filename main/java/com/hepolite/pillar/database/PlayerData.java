package com.hepolite.pillar.database;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerData
{
	// Control variables
	private final HashMap<String, DataField> fields = new HashMap<String, DataField>();
	private final UUID uuid;

	public PlayerData(UUID uuid)
	{
		this.uuid = uuid;
	}

	/** Performs on tick on the player data, updating fields if needed */
	public final void onTick()
	{
		for (Iterator<Entry<String, DataField>> it = fields.entrySet().iterator(); it.hasNext();)
		{
			Entry<String, DataField> entry = it.next();
			if (entry.getValue().tick())
				it.remove();
		}
	}

	// ////////////////////////////////////////////////////////////////////////

	/** Stores some value in the field map */
	public final void set(String field, Object value)
	{
		set(field, value, -1);
	}

	/** Stores some value in the field map, that should only exist for a certain amount of time, measured in ticks. Use -1 for an infinite lifetime */
	public final void set(String field, Object value, int lifetime)
	{
		fields.put(field, new DataField(value, lifetime));
	}

	/** Returns the true if the field exists */
	public final boolean has(String field)
	{
		return fields.containsKey(field);
	}

	/** Removes the given field in the data, if it exists */
	public final void remove(String field)
	{
		fields.remove(field);
	}

	/** Returns the value in the field map, if it exists */
	public final Object get(String field)
	{
		DataField dataField = fields.get(field);
		if (dataField == null)
			return null;
		return dataField.getValue();
	}
	
	/** Returns the lifetime of the given field, if it exists */
	public final int getLifetime(String field)
	{
		DataField dataField = fields.get(field);
		if (dataField == null)
			return 0;
		return dataField.getLifetime();
	}

	/** Returns true if there are no fields stored in the player data */
	public final boolean isEmpty()
	{
		return fields.isEmpty();
	}

	/** Returns the player associated with the data */
	public final Player getPlayer()
	{
		return Bukkit.getPlayer(uuid);
	}
}
