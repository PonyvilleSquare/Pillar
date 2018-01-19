package com.hepolite.pillar.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.hepolite.pillar.Pillar;

public abstract class PacketListener extends PacketAdapter
{
	private final PacketType type;

	public PacketListener(ListenerPriority priority, PacketType type)
	{
		super(Pillar.getInstance(), priority, type);
		this.type = type;
	}

	@Override
	public void onPacketSending(PacketEvent event)
	{
		if (event.getPacketType() == type)
		{
			try
			{
				onPacket(event);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/** Provides the functionality for the specific packet */
	protected abstract void onPacket(PacketEvent event);
}
