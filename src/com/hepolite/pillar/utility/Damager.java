package com.hepolite.pillar.utility;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.hepolite.pillar.listener.ListenerManager;

public class Damager
{
	private static final Function<? super Double, Double> ZERO = Functions.constant(-0.0);

	private static String nextDeathMessage = null;

	/** If the next attack kills a player, this method sets the death message that will be displayed */
	public static void setNextDeathMessage(String message)
	{
		nextDeathMessage = message;
	}

	/** Returns the death message if any, or null if there was no death message stored */
	public static String getNextDeathMessage()
	{
		return nextDeathMessage;
	}

	/** Applies some damage to the target */
	public static void doDamage(double damage, LivingEntity target, DamageCause cause)
	{
		doDamage(damage, target, null, cause);
	}

	/** Applies some damage to the target, applied from the attacker */
	public static void doDamage(double damage, LivingEntity target, LivingEntity attacker, DamageCause cause)
	{
		doDamage(target, attacker, cause, new EnumMap<DamageModifier, Double>(ImmutableMap.of(DamageModifier.BASE, Math.max(0.0, damage))), new EnumMap<DamageModifier, Function<? super Double, Double>>(ImmutableMap.of(DamageModifier.BASE, ZERO)));
	}

	/** Applies some damage to the target, applied from the attacker */
	private static void doDamage(LivingEntity target, LivingEntity attacker, DamageCause cause, final Map<DamageModifier, Double> modifiers, final Map<DamageModifier, ? extends Function<? super Double, Double>> functions)
	{
		if (target == null || target.isDead() || !target.isValid())
			return;
		if (target instanceof Player)
		{
			Player player = (Player) target;
			if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
				return;
		}

		EntityDamageEvent event = null;
		if (attacker == null)
			event = new EntityDamageEvent(target, cause, modifiers, functions);
		else
			event = new EntityDamageByEntityEvent(attacker, target, cause, modifiers, functions);
		ListenerManager.post(event);
		if (!event.isCancelled())
		{
			double finalDamage = event.getFinalDamage();
			target.damage(0.0);
			target.setLastDamage(finalDamage);
			target.setLastDamageCause(event);
			target.setHealth(Math.max(0.0, target.getHealth() - finalDamage));
		}
		setNextDeathMessage(null);
	}

	/** Applies some healing to the given target */
	public static void doHeal(double heal, LivingEntity target, RegainReason reason)
	{
		if (target.isDead() || !target.isValid())
			return;
		EntityRegainHealthEvent event = new EntityRegainHealthEvent(target, Math.max(0.0, heal), reason);
		ListenerManager.post(event);
		if (!event.isCancelled())
			target.setHealth(Math.min(target.getMaxHealth(), target.getHealth() + event.getAmount()));
	}

	// ///////////////////////////////////////////////////////////////////////////

	/** Creates a lightning strike at the given location */
	public static void createLightningStrike(Location location, float strength, float radius, boolean affectPlayersOnly)
	{
		createLightningStrike(location, strength, radius, affectPlayersOnly, null);
	}

	/** Creates a lightning strike at the given location, from the given attacker */
	public static void createLightningStrike(Location location, float strength, float radius, boolean affectPlayersOnly, LivingEntity attacker)
	{
		// Locate the highest point and strike the lightning there
		double posX = location.getX();
		double posZ = location.getZ();
		double posY = location.getWorld().getHighestBlockYAt((int) (posX + 0.5), (int) (posZ + 0.5));

		List<LivingEntity> entitiesInWorld = location.getWorld().getLivingEntities();
		for (LivingEntity entity : entitiesInWorld)
		{
			double deltaX = entity.getLocation().getX() - location.getX();
			double deltaZ = entity.getLocation().getZ() - location.getZ();
			if (deltaX * deltaX + deltaZ * deltaZ < radius * radius)
			{
				// Only interested in the entity with the greatest altitude that's within range
				if (entity.getLocation().getY() > posY)
				{
					posX = entity.getLocation().getX();
					posY = entity.getLocation().getY();
					posZ = entity.getLocation().getZ();
				}
			}
		}
		location.setX(posX);
		location.setY(posY);
		location.setZ(posZ);
		location.getWorld().strikeLightningEffect(location);

		// Find all nearby entities and damage them if applicable
		List<LivingEntity> entities = EntityHelper.getEntitiesInRange(location, radius);
		for (LivingEntity entity : entities)
		{
			if (!affectPlayersOnly || entity instanceof Player)
			{
				if (attacker != null)
				{
					if (attacker instanceof Player)
						Damager.setNextDeathMessage("<player> was killed by " + attacker.getName() + ", using lightning");
					else if (attacker.getCustomName() != null)
						Damager.setNextDeathMessage("<player> was killed by " + attacker.getCustomName() + ", using lightning");
				}
				else
					Damager.setNextDeathMessage("<player> was killed by lightning");

				doDamage(strength, entity, attacker, DamageCause.LIGHTNING);
				entity.setFireTicks((int) (15.0f * strength));
			}
		}
	}
}
