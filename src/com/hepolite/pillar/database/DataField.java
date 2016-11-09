package com.hepolite.pillar.database;

public class DataField
{
	// Control variables
	private int lifetime = -1;		// Set to -1 for infinite lifetime
	private Object value = null;	// Value stored in the field

	/** Initialize a data field */
	public DataField(Object value, int lifetime)
	{
		this.lifetime = lifetime;
		this.value = value;
	}

	/** Returns the value of the data field */
	public final Object getValue()
	{
		return value;
	}
	
	/** Returns the lifetime that is left of the field */
	public final int getLifetime()
	{
		return lifetime;
	}

	/** Reduces the lifetime of the field by one, returns true if it is 0 */
	public final boolean tick()
	{
		return (lifetime != -1 && lifetime-- == 0);
	}
}
