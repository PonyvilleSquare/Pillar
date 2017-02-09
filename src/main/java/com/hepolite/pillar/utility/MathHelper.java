package com.hepolite.pillar.utility;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MathHelper
{
	public static Vector gravity = new Vector(0.0, -0.05, 0.0);

	// ////////////////////////////////////////////////////////////////////////////////////////////

	/** Returns the nearest living entity that's inside the given line. If no entity was found, null is returned */
	public static LivingEntity getEntityInLine(Player player, float visionRange)
	{
		return getEntityInLine(player.getEyeLocation(), visionRange, player);
	}

	/** Returns all the entities that's inside the given line. If no entities were found, an empty list is returned */
	public static List<LivingEntity> getEntitiesInLine(Player player, float visionRange)
	{
		return getEntitiesInLine(player.getEyeLocation(), visionRange, player);
	}

	/** Returns the nearest living entity that's inside the given line. If no entity was found, null is returned */
	public static LivingEntity getEntityInLine(Location location, float visionRange, LivingEntity ignoredEntity)
	{
		Vector3D observerDir = new Vector3D(location.getDirection());
		Vector3D observerStart = new Vector3D(location);
		Vector3D observerEnd = observerStart.add(observerDir.multiply(visionRange));

		return getEntityInLine(location.getWorld(), observerStart, observerEnd, ignoredEntity);
	}

	/** Returns all the entities that's inside the given line. If no entities were found, an empty list is returned */
	public static List<LivingEntity> getEntitiesInLine(Location location, float visionRange, LivingEntity ignoredEntity)
	{
		Vector3D observerDir = new Vector3D(location.getDirection());
		Vector3D observerStart = new Vector3D(location);
		Vector3D observerEnd = observerStart.add(observerDir.multiply(visionRange));

		return getEntitiesInLine(location.getWorld(), observerStart, observerEnd, ignoredEntity);
	}

	/** Returns the nearest living entity that's inside the given line. If no entities was found, null is returned */
	public static LivingEntity getEntityInLine(World world, Vector3D start, Vector3D end, LivingEntity ignoredEntity)
	{
		LivingEntity nearestEntity = null;
		float nearestDistanceSquared = -1.0f;

		List<Entity> entities = world.getEntities();
		for (Entity entity : entities)
		{
			if (entity instanceof LivingEntity)
			{
				// Get the entity bounding box, slightly expanded
				Vector3D targetPos = new Vector3D(((LivingEntity) entity).getLocation());
				Vector3D minimum = targetPos.add(-0.5, 0, -0.5);
				Vector3D maximum = targetPos.add(0.5, 2.0, 0.5);	// Treat all mobs as two meters tall for now

				if (entity != ignoredEntity && MathHelper.hasIntersection(start, end, minimum, maximum))
				{
					float distanceSquared = (float) ((LivingEntity) entity).getEyeLocation().toVector().distanceSquared(start.toVector());
					if (distanceSquared < nearestDistanceSquared || nearestDistanceSquared == -1.0f)
					{
						nearestDistanceSquared = distanceSquared;
						nearestEntity = (LivingEntity) entity;
					}
				}
			}
		}
		return nearestEntity;
	}

	/** Returns all living entities that's inside the given line. If no entities were found, an empty list is returned */
	public static List<LivingEntity> getEntitiesInLine(World world, Vector3D start, Vector3D end, LivingEntity ignoredEntity)
	{
		List<LivingEntity> resultEntities = new LinkedList<LivingEntity>();

		List<Entity> entities = world.getEntities();
		for (Entity entity : entities)
		{
			if (entity instanceof LivingEntity)
			{
				// Get the entity bounding box, slightly expanded
				Vector3D targetPos = new Vector3D(((LivingEntity) entity).getLocation());
				Vector3D minimum = targetPos.add(-0.75, 0, -0.75);
				Vector3D maximum = targetPos.add(0.75, 2.0, 0.75);	// Treat all mobs as two meters tall for now

				if (entity != ignoredEntity && MathHelper.hasIntersection(start, end, minimum, maximum))
					resultEntities.add((LivingEntity) entity);
			}
		}
		return resultEntities;
	}

	/** Returns all the entities that's inside the given line. If no entities were found, an empty list is returned */
	public static List<Item> getItemsInLine(Player player, float visionRange)
	{
		return getItemsInLine(player.getEyeLocation(), visionRange);
	}

	/** Returns all the entities that's inside the given line. If no entities were found, an empty list is returned */
	public static List<Item> getItemsInLine(Location location, float visionRange)
	{
		Vector3D observerDir = new Vector3D(location.getDirection());
		Vector3D observerStart = new Vector3D(location);
		Vector3D observerEnd = observerStart.add(observerDir.multiply(visionRange));

		return getItemsInLine(location.getWorld(), observerStart, observerEnd);
	}

	/** Returns all item entities that's inside the given line. If no entities were found, an empty list is returned */
	public static List<Item> getItemsInLine(World world, Vector3D start, Vector3D end)
	{
		List<Item> resultEntities = new LinkedList<Item>();

		List<Entity> entities = world.getEntities();
		for (Entity entity : entities)
		{
			if (entity instanceof Item)
			{
				// Get the entity bounding box, slightly expanded
				Vector3D targetPos = new Vector3D(entity.getLocation());
				Vector3D minimum = targetPos.add(-0.5, -0.5, -0.5);
				Vector3D maximum = targetPos.add(0.5, 0.5, 0.5);	// Treat all items as a cubic meter for now

				if (MathHelper.hasIntersection(start, end, minimum, maximum))
					resultEntities.add((Item) entity);
			}
		}
		return resultEntities;
	}

	/* Intersection code from https://gist.github.com/aadnk/5550498 */
	/** Returns true if the line formed between the points p1 and p2 are intersecting the bounding box spanned out by the vector min and max */
	public static boolean hasIntersection(Vector3D p1, Vector3D p2, Vector3D min, Vector3D max)
	{
		final double epsilon = 0.0001f;
		Vector3D d = p2.subtract(p1).multiply(0.5);
		Vector3D e = max.subtract(min).multiply(0.5);
		Vector3D c = p1.add(d).subtract(min.add(max).multiply(0.5));
		Vector3D ad = d.abs();
		if (Math.abs(c.x) > e.x + ad.x)
			return false;
		if (Math.abs(c.y) > e.y + ad.y)
			return false;
		if (Math.abs(c.z) > e.z + ad.z)
			return false;
		if (Math.abs(d.y * c.z - d.z * c.y) > e.y * ad.z + e.z * ad.y + epsilon)
			return false;
		if (Math.abs(d.z * c.x - d.x * c.z) > e.z * ad.x + e.x * ad.z + epsilon)
			return false;
		if (Math.abs(d.x * c.y - d.y * c.x) > e.x * ad.y + e.y * ad.x + epsilon)
			return false;
		return true;
	}

	/* Vector code from https://gist.github.com/aadnk/5550498 */
	/** A vector class to make vector math easier */
	public static final class Vector3D
	{
		/**
		 * Represents the null (0, 0, 0) origin.
		 */
		public static final Vector3D ORIGIN = new Vector3D(0, 0, 0);

		// Use protected members, like Bukkit
		public final double x;
		public final double y;
		public final double z;

		/**
		 * Construct an immutable 3D vector.
		 */
		public Vector3D(double x, double y, double z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}

		/**
		 * Construct an immutable floating point 3D vector from a location object.
		 * 
		 * @param location
		 *            - the location to copy.
		 */
		public Vector3D(Location location)
		{
			this(location.toVector());
		}

		/**
		 * Construct an immutable floating point 3D vector from a mutable Bukkit vector.
		 * 
		 * @param vector
		 *            - the mutable real Bukkit vector to copy.
		 */
		public Vector3D(Vector vector)
		{
			if (vector == null)
				throw new IllegalArgumentException("Vector cannot be NULL.");
			this.x = vector.getX();
			this.y = vector.getY();
			this.z = vector.getZ();
		}

		/**
		 * Convert this instance to an equivalent real 3D vector.
		 * 
		 * @return Real 3D vector.
		 */
		public Vector toVector()
		{
			return new Vector(x, y, z);
		}

		/**
		 * Adds the current vector and a given position vector, producing a result vector.
		 * 
		 * @param other
		 *            - the other vector.
		 * @return The new result vector.
		 */
		public Vector3D add(Vector3D other)
		{
			if (other == null)
				throw new IllegalArgumentException("other cannot be NULL");
			return new Vector3D(x + other.x, y + other.y, z + other.z);
		}

		/**
		 * Adds the current vector and a given vector together, producing a result vector.
		 * 
		 * @param other
		 *            - the other vector.
		 * @return The new result vector.
		 */
		public Vector3D add(double x, double y, double z)
		{
			return new Vector3D(this.x + x, this.y + y, this.z + z);
		}

		/**
		 * Substracts the current vector and a given vector, producing a result position.
		 * 
		 * @param other
		 *            - the other position.
		 * @return The new result position.
		 */
		public Vector3D subtract(Vector3D other)
		{
			if (other == null)
				throw new IllegalArgumentException("other cannot be NULL");
			return new Vector3D(x - other.x, y - other.y, z - other.z);
		}

		/**
		 * Substracts the current vector and a given vector together, producing a result vector.
		 * 
		 * @param other
		 *            - the other vector.
		 * @return The new result vector.
		 */
		public Vector3D subtract(double x, double y, double z)
		{
			return new Vector3D(this.x - x, this.y - y, this.z - z);
		}

		/**
		 * Multiply each dimension in the current vector by the given factor.
		 * 
		 * @param factor
		 *            - multiplier.
		 * @return The new result.
		 */
		public Vector3D multiply(int factor)
		{
			return new Vector3D(x * factor, y * factor, z * factor);
		}

		/**
		 * Multiply each dimension in the current vector by the given factor.
		 * 
		 * @param factor
		 *            - multiplier.
		 * @return The new result.
		 */
		public Vector3D multiply(double factor)
		{
			return new Vector3D(x * factor, y * factor, z * factor);
		}

		/**
		 * Divide each dimension in the current vector by the given divisor.
		 * 
		 * @param divisor
		 *            - the divisor.
		 * @return The new result.
		 */
		public Vector3D divide(int divisor)
		{
			if (divisor == 0)
				throw new IllegalArgumentException("Cannot divide by null.");
			return new Vector3D(x / divisor, y / divisor, z / divisor);
		}

		/**
		 * Divide each dimension in the current vector by the given divisor.
		 * 
		 * @param divisor
		 *            - the divisor.
		 * @return The new result.
		 */
		public Vector3D divide(double divisor)
		{
			if (divisor == 0)
				throw new IllegalArgumentException("Cannot divide by null.");
			return new Vector3D(x / divisor, y / divisor, z / divisor);
		}

		/**
		 * Retrieve the absolute value of this vector.
		 * 
		 * @return The new result.
		 */
		public Vector3D abs()
		{
			return new Vector3D(Math.abs(x), Math.abs(y), Math.abs(z));
		}

		@Override
		public String toString()
		{
			return String.format("[x: %s, y: %s, z: %s]", x, y, z);
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////

	/** Computes a vector that will allow a projectile to hit a given entity if the entity moves at a constant rate */
	public static Vector computePredictionVector(Location start, Location end, float projectileSpeed, boolean factorInGravity)
	{
		// Compute the delta in position
		Vector delta = end.clone().subtract(start).toVector();

		// Obtain the gravity vector
		Vector gravity = MathHelper.gravity.clone();
		if (!factorInGravity)
			gravity.setX(0.0).setY(0.0).setZ(0.0);

		// Compute the second-degree polynomial
		double a = 0.25 * gravity.dot(gravity);
		double b = delta.dot(gravity) - projectileSpeed * projectileSpeed;
		double c = delta.dot(delta);

		// Compute the square root value, b^2-4ac
		double sqrtValue = b * b - 4.0 * a * c;
		if (sqrtValue < 0.0)
			return delta.multiply(projectileSpeed / delta.length()); // Do some rather weird guess; can't hit the target anyways

		// Compute the square root
		double t1 = (-b - Math.sqrt(sqrtValue)) / (2.0 * a);
		double t2 = (-b + Math.sqrt(sqrtValue)) / (2.0 * a);

		// Find the smallest allowed time
		double tMin = 1000000.0;
		if (t1 >= 0.0)
			tMin = Math.min(tMin, Math.sqrt(t1));
		if (t2 >= 0.0)
			tMin = Math.min(tMin, Math.sqrt(t2));
		if (tMin > 100.0) // If the time to get anywhere is ridiculous, return the best guess
			return delta.multiply(projectileSpeed / delta.length()); // Do some rather weird guess

		// Compute the position to aim at and scale it properly
		// Aim at the position P + G/2 t^2
		Vector direction = delta.subtract(gravity.clone().multiply(0.5 * tMin * tMin));
		return direction.normalize().multiply(projectileSpeed);
	}

	/** Computes a much more advanced form for prediction vector */
	public static Vector computeAdvancedPredictionVector(Location start, Location end, Vector deltaVelocity, float projectileSpeed, boolean factorInGravity)
	{
		// Compute the delta in position
		Vector deltaPosition = end.clone().subtract(start).toVector();

		// Obtain the gravity vector
		Vector gravity = MathHelper.gravity.clone();
		if (!factorInGravity)
			gravity.setX(0.0).setY(0.0).setZ(0.0);

		// Compute the fourth-degree polynomial
		double a = 0.25 * gravity.dot(gravity);
		double b = deltaVelocity.dot(gravity);
		double c = (-deltaPosition.dot(gravity) + deltaVelocity.dot(deltaVelocity) - projectileSpeed * projectileSpeed);
		double d = 2.0 * deltaPosition.dot(deltaVelocity);
		double e = deltaPosition.dot(deltaPosition);

		// Find the time of the intersection
		double time = computeFirstQuarticEquationRoot(a, b, c, d, e);
		if (time < 0.0)
			return deltaPosition.multiply(projectileSpeed / deltaPosition.length()); // Do some rather weird guess

		// Compute the position to aim at and scale it properly
		// Aim at the position P + Vt + G/2 t^2
		Vector direction = (deltaPosition.subtract(gravity.clone().multiply(0.5 * time * time)).add(deltaVelocity.clone().multiply(time))).normalize();
		return direction.multiply(projectileSpeed);
	}

	/** Computes the solutions for a quartic equation, if they exists. ax^4 + bx^3 + cx^2 + dx + e = 0 */
	public static double computeFirstQuarticEquationRoot(double a, double b, double c, double d, double e)
	{
		// Control variables
		double t = 0.0;
		double value = e;
		double oldValue = 0.0;

		double root = -1.0; // The first root found

		double smallestTime = -1.0; // Smallest polynomial value found
		double smallestValue = -1.0;

		// Do some steps until a root can be located
		while (t < 100.0) // Care only about solutions that can be found within 100 ticks
		{
			t += 0.5;	// Take a somewhat big step at a time

			// Compute the values of the polynomial
			oldValue = value;
			value = e + t * (d + t * (c + t * (b + t * a)));

			smallestTime = (value < smallestValue) ? t : smallestTime;
			smallestValue = (value < smallestValue || smallestValue == -1.0) ? value : smallestValue;

			if (oldValue * value < 0.0)
			{
				// Apply some steps of the bisection method
				double lt = t - 0.1, rt = t;
				double mt = t, middleValue;
				for (int i = 0; i < 15; i++)
				{
					mt = 0.5 * (lt + rt);
					middleValue = e + mt * (d + mt * (c + mt * (b + mt * a)));
					if (oldValue * middleValue < 0)
						rt = mt;
					else if (oldValue * middleValue > 0)
						lt = mt;
					else
						break;
				}
				root = mt;
				break;
			}
			else if (oldValue * value == 0.0) // If the value is exactly 0, the new value is a root
			{
				root = t;
				break;
			}
		}

		// Return the end value
		return root;
	}

	/** Computes the angle between two vectors in terms of cosine, use arccos(angle) to convert to radians */
	public static double getAngleBetweenVectors(Vector v1, Vector v2)
	{
		return v1.dot(v2) / (v1.length() * v2.length());
	}
}
