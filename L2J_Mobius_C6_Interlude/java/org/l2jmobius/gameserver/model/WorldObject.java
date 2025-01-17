/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.model;

import java.util.logging.Logger;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.instancemanager.IdManager;
import org.l2jmobius.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2jmobius.gameserver.instancemanager.MercTicketManager;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.knownlist.WorldObjectKnownList;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import org.l2jmobius.gameserver.network.serverpackets.GetItem;

/**
 * Mother class of all objects in the world which ones is it possible to interact (PC, NPC, Item...)<br>
 * <br>
 * WorldObject:<br>
 * <li>Creature</li>
 * <li>Item</li>
 * <li>Potion</li>
 */
public abstract class WorldObject
{
	private static final Logger LOGGER = Logger.getLogger(WorldObject.class.getName());
	
	private boolean _isSpawned;
	private WorldObjectKnownList _knownList;
	private String _name = "";
	private int _objectId;
	private WorldRegion _worldRegion;
	private final Location _location = new Location(0, 0, -10000);
	
	public WorldObject(int objectId)
	{
		_objectId = objectId;
	}
	
	public void onAction(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public void onActionShift(GameClient client)
	{
		final Player player = client.getPlayer();
		onActionShift(player);
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public void onActionShift(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public void onForcedAttack(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	/**
	 * Do Nothing.<br>
	 * <br>
	 * <b><u>Overridden in</u>:</b><br>
	 * <li>GuardInstance : Set the home location of its GuardInstance</li>
	 * <li>Attackable : Reset the Spoiled flag</li>
	 */
	public void onSpawn()
	{
	}
	
	/**
	 * Checks if current object changed its region, if so, update references.
	 */
	public void updateWorldRegion()
	{
		if (!isSpawned())
		{
			return;
		}
		
		final WorldRegion newRegion = World.getInstance().getRegion(_location);
		if (newRegion != getWorldRegion())
		{
			getWorldRegion().removeVisibleObject(this);
			
			setWorldRegion(newRegion);
			
			// Add the WorldOject spawn to _visibleObjects and if necessary to _allplayers of its WorldRegion.
			getWorldRegion().addVisibleObject(this);
		}
	}
	
	/**
	 * Set the x,y,z position of the WorldObject and if necessary modify its _worldRegion.<br>
	 * <br>
	 * <b><u>Assert</u>:</b><br>
	 * <li>_worldRegion != null</li><br>
	 * <br>
	 * <b><u>Example of use</u>:</b><br>
	 * <li>Update position during and after movement, or after teleport</li><br>
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public void setXYZ(int x, int y, int z)
	{
		_location.setXYZ(x, y, z);
		
		try
		{
			if (World.getInstance().getRegion(_location) != getWorldRegion())
			{
				updateWorldRegion();
			}
		}
		catch (Exception e)
		{
			LOGGER.warning("Object Id at bad coords: (x: " + getX() + ", y: " + getY() + ", z: " + getZ() + ").");
			if (isPlayer())
			{
				((Player) this).teleToLocation(0, 0, 0);
				((Player) this).sendMessage("Error with your coords, Please ask a GM for help!");
			}
			else if (isCreature())
			{
				decayMe();
			}
		}
	}
	
	/**
	 * Set the x,y,z position of the WorldObject and make it invisible.<br>
	 * <br>
	 * <b><u>Concept</u>:</b><br>
	 * <br>
	 * A WorldObject is invisble if <b>_hidden</b>=true or <b>_worldregion</b>==null<br>
	 * <br>
	 * <b><u>Assert</u>:</b><br>
	 * <li>_worldregion==null <i>(WorldObject is invisible)</i></li><br>
	 * <br>
	 * <b><u>Example of use</u>:</b><br>
	 * <li>Create a Door</li>
	 * <li>Restore Player</li><br>
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public void setXYZInvisible(int x, int y, int z)
	{
		int correctX = x;
		if (correctX > World.WORLD_X_MAX)
		{
			correctX = World.WORLD_X_MAX - 5000;
		}
		if (correctX < World.WORLD_X_MIN)
		{
			correctX = World.WORLD_X_MIN + 5000;
		}
		
		int correctY = y;
		if (correctY > World.WORLD_Y_MAX)
		{
			correctY = World.WORLD_Y_MAX - 5000;
		}
		if (correctY < World.WORLD_Y_MIN)
		{
			correctY = World.WORLD_Y_MIN + 5000;
		}
		
		_location.setXYZ(correctX, correctY, z);
		setSpawned(false);
	}
	
	public int getX()
	{
		return _location.getX();
	}
	
	public int getY()
	{
		return _location.getY();
	}
	
	public int getZ()
	{
		return _location.getZ();
	}
	
	public int getHeading()
	{
		return _location.getHeading();
	}
	
	/**
	 * Remove a WorldObject from the world.<br>
	 * <br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Remove the WorldObject from the world</li><br>
	 * <font color=#FF0000><b><u>Caution</u>: This method DOESN'T REMOVE the object from _allObjects of World </b></font><br>
	 * <font color=#FF0000><b><u>Caution</u>: This method DOESN'T SEND Server->Client packets to players</b></font><br>
	 * <br>
	 * <b><u>Assert</u>:</b><br>
	 * <li>_worldRegion != null <i>(WorldObject is visible at the beginning)</i></li><br>
	 * <br>
	 * <b><u>Example of use</u>:</b><br>
	 * <li>Delete NPC/PC or Unsummon</li>
	 */
	public void decayMe()
	{
		// Remove the WorldObject from the world
		_isSpawned = false;
		World.getInstance().removeVisibleObject(this, getWorldRegion());
		World.getInstance().removeObject(this);
		setWorldRegion(null);
		
		if (Config.SAVE_DROPPED_ITEM)
		{
			ItemsOnGroundManager.getInstance().removeObject(this);
		}
	}
	
	/**
	 * Remove a Item from the world and send server->client GetItem packets.<br>
	 * <br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Send a Server->Client Packet GetItem to player that pick up and its _knowPlayers member</li>
	 * <li>Remove the WorldObject from the world</li><br>
	 * <font color=#FF0000><b><u>Caution</u>: This method DOESN'T REMOVE the object from _allObjects of World </b></font><br>
	 * <br>
	 * <b><u>Assert</u>:</b><br>
	 * <li>this instanceof Item</li>
	 * <li>_worldRegion != null <i>(WorldObject is visible at the beginning)</i></li><br>
	 * <br>
	 * <b><u>Example of use</u>:</b><br>
	 * <li>Do Pickup Item : Player and Pet</li><br>
	 * @param creature Player that pick up the item
	 */
	public void pickupMe(Creature creature) // NOTE: Should move this function into Item because it does not apply to Creature
	{
		final WorldRegion oldregion = getWorldRegion();
		
		// Create a server->client GetItem packet to pick up the Item
		creature.broadcastPacket(new GetItem((Item) this, creature.getObjectId()));
		
		synchronized (this)
		{
			_isSpawned = false;
			setWorldRegion(null);
		}
		
		// if this item is a mercenary ticket, remove the spawns!
		if (this instanceof Item)
		{
			final int itemId = ((Item) this).getItemId();
			if (MercTicketManager.getInstance().getTicketCastleId(itemId) > 0)
			{
				MercTicketManager.getInstance().removeTicket((Item) this);
				ItemsOnGroundManager.getInstance().removeObject(this);
			}
		}
		
		// this can synchronize on others instancies, so it's out of synchronized, to avoid deadlocks
		// Remove the Item from the world
		World.getInstance().removeVisibleObject(this, oldregion);
	}
	
	public void refreshId()
	{
		World.getInstance().removeObject(this);
		IdManager.getInstance().releaseId(getObjectId());
		_objectId = IdManager.getInstance().getNextId();
	}
	
	/**
	 * Init the position of a WorldObject spawn and add it in the world as a visible object.<br>
	 * <br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Set the x,y,z position of the WorldObject spawn and update its _worldregion</li>
	 * <li>Add the WorldObject spawn in the _allobjects of World</li>
	 * <li>Add the WorldObject spawn to _visibleObjects of its WorldRegion</li>
	 * <li>Add the WorldObject spawn in the world as a <b>visible</b> object</li><br>
	 * <br>
	 * <b><u>Assert</u>:</b><br>
	 * <li>_worldRegion == null <i>(WorldObject is invisible at the beginning)</i></li><br>
	 * <br>
	 * <b><u>Example of use</u>:</b><br>
	 * <li>Create Door</li>
	 * <li>Spawn : Monster, Minion, CTs, Summon...</li>
	 */
	public void spawnMe()
	{
		synchronized (this)
		{
			// Set the x,y,z position of the WorldObject spawn and update its _worldregion
			_isSpawned = true;
			setWorldRegion(World.getInstance().getRegion(getLocation()));
			
			// Add the WorldObject spawn in the _allobjects of World
			World.getInstance().storeObject(this);
			
			// Add the WorldObject spawn to _visibleObjects and if necessary to _allplayers of its WorldRegion
			getWorldRegion().addVisibleObject(this);
		}
		
		// this can synchronize on others instances, so it's out of synchronized, to avoid deadlocks
		// Add the WorldObject spawn in the world as a visible object
		World.getInstance().addVisibleObject(this, getWorldRegion(), null);
		onSpawn();
	}
	
	public void spawnMe(int x, int y, int z)
	{
		synchronized (this)
		{
			// Set the x,y,z position of the WorldObject spawn and update its _worldregion
			_isSpawned = true;
			
			int spawnX = x;
			if (spawnX > World.WORLD_X_MAX)
			{
				spawnX = World.WORLD_X_MAX - 5000;
			}
			if (spawnX < World.WORLD_X_MIN)
			{
				spawnX = World.WORLD_X_MIN + 5000;
			}
			
			int spawnY = y;
			if (spawnY > World.WORLD_Y_MAX)
			{
				spawnY = World.WORLD_Y_MAX - 5000;
			}
			if (spawnY < World.WORLD_Y_MIN)
			{
				spawnY = World.WORLD_Y_MIN + 5000;
			}
			
			_location.setXYZ(spawnX, spawnY, z);
			setWorldRegion(World.getInstance().getRegion(getLocation()));
		}
		
		// these can synchronize on others instances, so they're out of synchronized, to avoid deadlocks
		// Add the WorldObject spawn in the _allobjects of World
		World.getInstance().storeObject(this);
		
		// Add the WorldObject spawn to _visibleObjects and if necessary to _allplayers of its WorldRegion
		final WorldRegion region = getWorldRegion();
		if (region != null)
		{
			region.addVisibleObject(this);
		}
		else
		{
			LOGGER.info("ATTENTION: no region found for location " + x + "," + y + "," + z + ". It's not possible to spawn object " + _objectId + " here...");
			return;
		}
		// this can synchronize on others instances, so it's out of synchronized, to avoid deadlocks
		// Add the WorldObject spawn in the world as a visible object
		World.getInstance().addVisibleObject(this, region, null);
		onSpawn();
	}
	
	public void toggleVisible()
	{
		if (isSpawned())
		{
			decayMe();
		}
		else
		{
			spawnMe();
		}
	}
	
	public abstract boolean isAutoAttackable(Creature attacker);
	
	public boolean isSpawned()
	{
		return getWorldRegion() != null;
	}
	
	public void setSpawned(boolean value)
	{
		_isSpawned = value;
		if (!_isSpawned)
		{
			setWorldRegion(null);
		}
	}
	
	public WorldObjectKnownList getKnownList()
	{
		if (_knownList == null)
		{
			_knownList = new WorldObjectKnownList(this);
		}
		return _knownList;
	}
	
	public void setKnownList(WorldObjectKnownList value)
	{
		_knownList = value;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public void setName(String value)
	{
		_name = value;
	}
	
	public int getObjectId()
	{
		return _objectId;
	}
	
	public Location getLocation()
	{
		return _location;
	}
	
	/**
	 * Gets the world region.
	 * @return the world region
	 */
	public synchronized WorldRegion getWorldRegion()
	{
		return _worldRegion;
	}
	
	/**
	 * Sets the world region.
	 * @param value the new world region
	 */
	public synchronized void setWorldRegion(WorldRegion value)
	{
		_worldRegion = value;
	}
	
	/**
	 * @return The id of the instance zone the object is in - id 0 is global since everything like dropped items, mobs, players can be in a instantiated area, it must be in l2object
	 */
	public int getInstanceId()
	{
		return _location.getInstanceId();
	}
	
	/**
	 * @param instanceId The id of the instance zone the object is in - id 0 is global
	 */
	public void setInstanceId(int instanceId)
	{
		_location.setInstanceId(instanceId);
		
		// If we change it for visible objects, me must clear & revalidates knownlists
		if (_isSpawned && (_knownList != null))
		{
			if (this instanceof Player)
			{
				// We don't want some ugly looking disappear/appear effects, so don't update the knownlist here, but players usually enter instancezones through teleporting and the teleport will do the revalidation for us.
			}
			else
			{
				decayMe();
				spawnMe();
			}
		}
	}
	
	/**
	 * Verify if object can be attacked.
	 * @return {@code true} if object can be attacked, {@code false} otherwise
	 */
	public boolean canBeAttacked()
	{
		return false;
	}
	
	public Player getActingPlayer()
	{
		return null;
	}
	
	public boolean isPlayer()
	{
		return false;
	}
	
	public boolean isPlayable()
	{
		return false;
	}
	
	public boolean isSummon()
	{
		return false;
	}
	
	public boolean isPet()
	{
		return false;
	}
	
	public boolean isCreature()
	{
		return false;
	}
	
	public boolean isAttackable()
	{
		return false;
	}
	
	public boolean isNpc()
	{
		return false;
	}
	
	public boolean isMonster()
	{
		return false;
	}
	
	public boolean isRaid()
	{
		return false;
	}
	
	public boolean isMinion()
	{
		return false;
	}
	
	public boolean isArtefact()
	{
		return false;
	}
	
	public boolean isDoor()
	{
		return false;
	}
	
	public boolean isFence()
	{
		return false;
	}
	
	public boolean isBoat()
	{
		return false;
	}
	
	public boolean isItem()
	{
		return false;
	}
	
	public boolean isWalker()
	{
		return false;
	}
	
	/**
	 * Calculates 2D distance between this WorldObject and given x, y, z.
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @param z the Z coordinate
	 * @return distance between object and given x, y, z.
	 */
	public double calculateDistance2D(int x, int y, int z)
	{
		return Math.sqrt(Math.pow(x - getX(), 2) + Math.pow(y - getY(), 2));
	}
	
	/**
	 * Calculates the 2D distance between this WorldObject and given WorldObject.
	 * @param object the target object
	 * @return distance between object and given object.
	 */
	public double calculateDistance2D(WorldObject object)
	{
		return calculateDistance2D(object.getX(), object.getY(), object.getZ());
	}
	
	/**
	 * Calculates the 3D distance between this WorldObject and given x, y, z.
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @param z the Z coordinate
	 * @return distance between object and given x, y, z.
	 */
	public double calculateDistance3D(int x, int y, int z)
	{
		return Math.sqrt(Math.pow(x - getX(), 2) + Math.pow(y - getY(), 2) + Math.pow(z - getZ(), 2));
	}
	
	/**
	 * Calculates 3D distance between this WorldObject and given location.
	 * @param loc the location object
	 * @return distance between object and given location.
	 */
	public double calculateDistance3D(Location loc)
	{
		return calculateDistance3D(loc.getX(), loc.getY(), loc.getZ());
	}
	
	/**
	 * Calculates the non squared 2D distance between this WorldObject and given x, y, z.
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @param z the Z coordinate
	 * @return distance between object and given x, y, z.
	 */
	public double calculateDistanceSq2D(int x, int y, int z)
	{
		return Math.pow(x - getX(), 2) + Math.pow(y - getY(), 2);
	}
	
	/**
	 * Calculates the non squared 2D distance between this WorldObject and given WorldObject.
	 * @param object the target object
	 * @return distance between object and given object.
	 */
	public double calculateDistanceSq2D(WorldObject object)
	{
		return calculateDistanceSq2D(object.getX(), object.getY(), object.getZ());
	}
	
	/**
	 * Calculates the non squared 3D distance between this WorldObject and given x, y, z.
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @param z the Z coordinate
	 * @return distance between object and given x, y, z.
	 */
	public double calculateDistanceSq3D(int x, int y, int z)
	{
		return Math.pow(x - getX(), 2) + Math.pow(y - getY(), 2) + Math.pow(z - getZ(), 2);
	}
	
	/**
	 * Calculates the non squared 3D distance between this WorldObject and given WorldObject.
	 * @param object the target object
	 * @return distance between object and given object.
	 */
	public double calculateDistanceSq3D(WorldObject object)
	{
		return calculateDistanceSq3D(object.getX(), object.getY(), object.getZ());
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof WorldObject) && (((WorldObject) obj).getObjectId() == getObjectId());
	}
	
	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(":");
		sb.append(_name);
		sb.append("[");
		sb.append(_objectId);
		sb.append("]");
		return sb.toString();
	}
}
