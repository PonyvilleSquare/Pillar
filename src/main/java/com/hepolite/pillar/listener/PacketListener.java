package com.hepolite.pillar.listener;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.hepolite.pillar.protocollib.PacketListenerEntityEffect;

public class PacketListener
{
	public PacketListener()
	{
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

		protocolManager.addPacketListener(new PacketListenerEntityEffect());
	}
}
