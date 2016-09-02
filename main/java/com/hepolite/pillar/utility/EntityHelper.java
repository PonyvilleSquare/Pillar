package com.hepolite.pillar.utility;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class EntityHelper
{
	// ///////////////////////////////////////////////////////////////////////////////
	// ENTITY LOCATING // ENTITY LOCATING // ENTITY LOCATING // ENTITY LOCATING //
	// ///////////////////////////////////////////////////////////////////////////////

	/** Returns true if there was a clear path from the starting entity to the target entity */
	public static boolean isPathClear(Entity start, Entity end)
	{
		Vector direction = end.getLocation().subtract(start.getLocation()).toVector();
		BlockIterator it = new BlockIterator(start.getWorld(), start.getLocation().toVector(), direction.clone().normalize(), 0.0, (int) direction.length());
		while (it.hasNext())
		{
			Block block = it.next();
			if (block.getType().isSolid())
				return false;
		}
		return true;
	}

	/** Returns a list of entities within range */
	public static List<LivingEntity> getEntitiesInRange(Location location, float range)
	{
		// Find all nearby living entities and return them
		List<Entity> entitiesInWorld = location.getWorld().getEntities();
		List<LivingEntity> nearbyEntities = new LinkedList<LivingEntity>();
		for (Entity entity : entitiesInWorld)
		{
			if (entity instanceof LivingEntity)
			{
				if (location.distanceSquared(entity.getLocation()) < range * range)
					nearbyEntities.add((LivingEntity) entity);
			}
		}
		return nearbyEntities;
	}

	/** Returns a list of entities at the given location. Assumes a boundingbox 1m*1m*2m for all entities */
	public static List<LivingEntity> getEntitiesInLocation(Location location)
	{
		// Find all nearby players and return them
		List<Entity> entitiesInWorld = location.getWorld().getEntities();
		List<LivingEntity> entities = new LinkedList<LivingEntity>();
		for (Entity entity : entitiesInWorld)
		{
			if (entity instanceof LivingEntity)
			{
				Location position = entity.getLocation();
				if (location.getX() >= position.getX() - 0.5 && location.getX() <= position.getX() + 0.5)
					if (location.getZ() >= position.getZ() - 0.5 && location.getZ() <= position.getZ() + 0.5)
						if (location.getY() >= position.getY() - 0.1 && location.getY() <= position.getY() + 1.9)
							entities.add((LivingEntity) entity);
			}
		}
		return entities;
	}

	/** Returns a list of monsters within range */
	public static List<Monster> getMonstersInRange(Location location, float range)
	{
		// Find all nearby monsters and return them
		List<Entity> entitiesInWorld = location.getWorld().getEntities();
		List<Monster> nearbyEntities = new LinkedList<Monster>();
		for (Entity entity : entitiesInWorld)
		{
			if (entity instanceof Monster)
			{
				if (location.distanceSquared(entity.getLocation()) < range * range)
					nearbyEntities.add((Monster) entity);
			}
		}
		return nearbyEntities;
	}

	/** Returns a list of players within range */
	public static List<Player> getPlayersInRange(Location location, float range)
	{
		List<Player> nearbyPlayers = new LinkedList<Player>();
		for (Player player : location.getWorld().getPlayers())
		{
			if (location.distanceSquared(player.getLocation()) < range * range)
				nearbyPlayers.add(player);
		}
		return nearbyPlayers;
	}

	/** Returns a the nearest entity that intersect the given line, but aren't hiding behind solid blocks. Returns null if no entity was found */
	public static LivingEntity getEntityInSight(Player player, float distance, LivingEntity entityToIgnore)
	{
		Location start = player.getEyeLocation();
		Vector direction = start.getDirection().multiply(distance);
		return getEntityInSight(start, start.clone().add(direction), entityToIgnore);
	}

	/** Returns a the nearest entity that intersect the given line, but aren't hiding behind solid blocks. Returns null if no entity was found */
	public static LivingEntity getEntityInSight(Location start, Location end, LivingEntity entityToIgnore)
	{
		// Compute distance to the nearest solid block
		Location nearestBlock = end.clone();
		BlockIterator it = new BlockIterator(start, 0.0, (int) end.distance(start));
		while (it.hasNext())
		{
			Block block = it.next();
			if (block.getType().isSolid())
			{
				nearestBlock = block.getLocation();
				break;
			}
		}

		// Get all entities intersecting with the line, and figure out which of them are closer than the nearest block
		double nearestDistance = start.distanceSquared(nearestBlock);
		LivingEntity nearest = null;

		List<LivingEntity> entities = MathHelper.getEntitiesInLine(start, (float) end.distance(start), entityToIgnore);
		for (LivingEntity entity : entities)
		{
			double distance = entity.getLocation().distanceSquared(start);
			if (distance < nearestDistance)
			{
				nearestDistance = distance;
				nearest = entity;
			}
		}
		return nearest;
	}

	/** Returns a list of entities that intersect the given line, but aren't hiding behind solid blocks. Returns an empty list if no entities were found */
	public static List<LivingEntity> getEntitiesInSight(Location start, Location end)
	{
		List<LivingEntity> list = new LinkedList<LivingEntity>();

		// Compute distance to the nearest solid block
		Location nearestBlock = end.clone();
		BlockIterator it = new BlockIterator(start, 0.0, (int) end.distance(start));
		while (it.hasNext())
		{
			Block block = it.next();
			if (block.getType().isSolid())
			{
				nearestBlock = block.getLocation();
				break;
			}
		}

		// Get all entities intersecting with the line, and figure out which of them are closer than the nearest block
		double distance = start.distanceSquared(nearestBlock);
		List<LivingEntity> entities = MathHelper.getEntitiesInLine(start, (float) end.distance(start), null);
		for (LivingEntity entity : entities)
		{
			if (entity.getLocation().distanceSquared(start) < distance)
				list.add(entity);
		}
		return list;
	}

	/** Returns a the nearest player that intersect the given line, but aren't hiding behind solid blocks. Returns null if no player was found */
	public static Player getPlayerInSight(Player player, float distance, LivingEntity entityToIgnore)
	{
		Location start = player.getEyeLocation();
		Vector direction = start.getDirection().multiply(distance);
		return getPlayerInSight(start, start.clone().add(direction), entityToIgnore);
	}

	/** Returns a the nearest player that intersect the given line, but aren't hiding behind solid blocks. Returns null if no player was found */
	public static Player getPlayerInSight(Location start, Location end, LivingEntity entityToIgnore)
	{
		// Compute distance to the nearest solid block
		Location nearestBlock = end.clone();
		BlockIterator it = new BlockIterator(start, 0.0, (int) end.distance(start));
		while (it.hasNext())
		{
			Block block = it.next();
			if (block.getType().isSolid())
			{
				nearestBlock = block.getLocation();
				break;
			}
		}

		// Get all entities intersecting with the line, and figure out which of them are closer than the nearest block
		double nearestDistance = start.distanceSquared(nearestBlock);
		Player nearest = null;

		List<LivingEntity> entities = MathHelper.getEntitiesInLine(start, (float) end.distance(start), entityToIgnore);
		for (LivingEntity entity : entities)
		{
			double distance = entity.getLocation().distanceSquared(start);
			if (entity instanceof Player && distance < nearestDistance)
			{
				nearestDistance = distance;
				nearest = (Player) entity;
			}
		}
		return nearest;
	}

	/** Returns a list of players that intersect the given line, but aren't hiding behind solid blocks. Returns an empty list if no players were found */
	public static List<Player> getPlayersInSight(Location start, Location end)
	{
		List<Player> list = new LinkedList<Player>();

		// Compute distance to the nearest solid block
		Location nearestBlock = end.clone();
		BlockIterator it = new BlockIterator(start, 0.0, (int) end.distance(start));
		while (it.hasNext())
		{
			Block block = it.next();
			if (block.getType().isSolid())
			{
				nearestBlock = block.getLocation();
				break;
			}
		}

		// Get all entities intersecting with the line, and figure out which of them are closer than the nearest block
		double distance = start.distanceSquared(nearestBlock);
		List<LivingEntity> entities = MathHelper.getEntitiesInLine(start, (float) end.distance(start), null);
		for (LivingEntity entity : entities)
		{
			if (entity instanceof Player && entity.getLocation().distanceSquared(start) < distance)
				list.add((Player) entity);
		}
		return list;
	}

	/** Returns a list of items that intersect the given line, but aren't hiding behind solid blocks. Returns an empty list if no entities were found */
	public static List<Item> getItemsInSight(Location start, Location end)
	{
		List<Item> list = new LinkedList<Item>();

		// Compute distance to the nearest solid block
		Location nearestBlock = end.clone();
		BlockIterator it = new BlockIterator(start, 0.0, (int) end.distance(start));
		while (it.hasNext())
		{
			Block block = it.next();
			if (block.getType().isSolid())
			{
				nearestBlock = block.getLocation();
				break;
			}
		}

		// Get all items intersecting with the line, and figure out which of them are closer than the nearest block
		double distance = start.distanceSquared(nearestBlock);
		List<Item> items = MathHelper.getItemsInLine(start, (float) end.distance(start));
		for (Item item : items)
		{
			if (item.getLocation().distanceSquared(start) < distance)
				list.add(item);
		}
		return list;
	}

	// ///////////////////////////////////////////////////////////////////////////////

	/** Returns the level of the potion effect on the given entity. Level 1 is given as 1, NOT as 0 as it would be in Minecraft. Returns 0 if there is no effect applied */
	public static int getPotionLevel(LivingEntity entity, PotionEffectType type)
	{
		if (entity == null || type == null)
			return 0;
		for (PotionEffect effect : entity.getActivePotionEffects())
		{
			if (effect.getType().equals(type))
				return 1 + effect.getAmplifier();
		}
		return 0;
	}
}
