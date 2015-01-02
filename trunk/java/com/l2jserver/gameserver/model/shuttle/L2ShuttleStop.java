/*
 * Copyright (C) 2004-2015 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.model.shuttle;

import java.util.ArrayList;
import java.util.List;

import com.l2jserver.gameserver.model.Location;

/**
 * @author UnAfraid
 */
public class L2ShuttleStop
{
	private final int _id;
	private boolean _isOpen = true;
	private final List<Location> _dimensions = new ArrayList<>(3);
	private long _lastDoorStatusChanges = System.currentTimeMillis();
	
	public L2ShuttleStop(int id)
	{
		_id = id;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public boolean isDoorOpen()
	{
		return _isOpen;
	}
	
	public void addDimension(Location loc)
	{
		_dimensions.add(loc);
	}
	
	public List<Location> getDimensions()
	{
		return _dimensions;
	}
	
	public void openDoor()
	{
		if (_isOpen)
		{
			return;
		}
		
		_isOpen = true;
		_lastDoorStatusChanges = System.currentTimeMillis();
	}
	
	public void closeDoor()
	{
		if (!_isOpen)
		{
			return;
		}
		
		_isOpen = false;
		_lastDoorStatusChanges = System.currentTimeMillis();
	}
	
	public boolean hasDoorChanged()
	{
		return (System.currentTimeMillis() - _lastDoorStatusChanges) <= 1000;
	}
}
