package com.hepolite.pillar.protocollib;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import com.hepolite.pillar.events.PotionEffectEvent;
import com.hepolite.pillar.listener.ListenerManager;
import com.hepolite.pillar.protocollib.wrappers.WrapperPlayServerEntityEffect;

public class PacketListenerEntityEffect extends PacketListener
{
	public PacketListenerEntityEffect()
	{
		super(ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_EFFECT);
	}

	@Override
	protected void onPacket(PacketEvent event)
	{
		// http://wiki.vg/Protocol#Entity_Effect
		WrapperPlayServerEntityEffect wrapper = new WrapperPlayServerEntityEffect(event.getPacket());

		Entity entity = wrapper.getEntity(event);
		if (!(entity instanceof LivingEntity))
			return;

		PotionEffect effect = new PotionEffect(wrapper.getEffect(), wrapper.getDuration(), wrapper.getAmplifier(), wrapper.isAmbient(), wrapper.showParticles());
		PotionEffectEvent potionEvent = new PotionEffectEvent((LivingEntity) entity, effect);
		ListenerManager.post(potionEvent);
		effect = potionEvent.getEffect();
		wrapper.setEffect(effect.getType());
		wrapper.setDuration(effect.getDuration());
		wrapper.setAmplifier((byte) effect.getAmplifier());
		wrapper.setAmbient(effect.isAmbient());
		wrapper.setShowParticles(effect.hasParticles());
		event.setCancelled(potionEvent.isCancelled());
	}
}
