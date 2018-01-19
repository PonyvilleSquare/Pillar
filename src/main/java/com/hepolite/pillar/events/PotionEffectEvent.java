package com.hepolite.pillar.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;

public class PotionEffectEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private boolean isCancelled = false;

	private final LivingEntity entity;
	private PotionEffect effect;

	public PotionEffectEvent(LivingEntity entity, PotionEffect effect)
	{
		this.entity = entity;
		this.effect = effect;
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	/** Returns the entity that is to be affected by the potion effect */
	public final LivingEntity getEntity()
	{
		return entity;
	}

	/** Returns the potion effect that is to be applied */
	public final PotionEffect getEffect()
	{
		return effect;
	}

	/** Sets the potion effect that is to be applied */
	public final void setEffect(PotionEffect effect)
	{
		this.effect = effect;
	}

	/** Returns whether the event has been cancelled or not */
	@Override
	public boolean isCancelled()
	{
		return isCancelled;
	}

	/** Assigns the canceling state of the event */
	@Override
	public void setCancelled(boolean cancelled)
	{
		isCancelled = cancelled;
	}
}
