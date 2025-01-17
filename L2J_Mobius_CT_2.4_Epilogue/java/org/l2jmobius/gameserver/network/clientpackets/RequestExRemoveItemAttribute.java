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
package org.l2jmobius.gameserver.network.clientpackets;

import org.l2jmobius.commons.network.ReadablePacket;
import org.l2jmobius.gameserver.model.Elementals;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.Weapon;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowBaseAttributeCancelWindow;
import org.l2jmobius.gameserver.network.serverpackets.InventoryUpdate;

public class RequestExRemoveItemAttribute implements ClientPacket
{
	private int _objectId;
	private long _price;
	
	@Override
	public void read(ReadablePacket packet)
	{
		_objectId = packet.readInt();
	}
	
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Item targetItem = player.getInventory().getItemByObjectId(_objectId);
		if (targetItem == null)
		{
			return;
		}
		
		if (targetItem.getElementals() == null)
		{
			return;
		}
		
		if (player.reduceAdena("RemoveElement", getPrice(targetItem), player, true))
		{
			if (targetItem.isEquipped())
			{
				targetItem.removeElementAttrBonus(player);
			}
			for (Elementals element : targetItem.getElementals())
			{
				targetItem.clearElementAttr(element.getElement());
			}
			player.updateUserInfo();
			
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(targetItem);
			player.sendPacket(iu);
			
			// Retail message.
			player.sendMessage(targetItem.getName() + "'s elemental power was removed.");
			player.sendPacket(new ExShowBaseAttributeCancelWindow(player));
		}
		else
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		}
	}
	
	private long getPrice(Item item)
	{
		switch (item.getTemplate().getCrystalType())
		{
			case S:
			{
				if (item.getTemplate() instanceof Weapon)
				{
					_price = 50000;
				}
				else
				{
					_price = 40000;
				}
				break;
			}
			case S80:
			{
				if (item.getTemplate() instanceof Weapon)
				{
					_price = 100000;
				}
				else
				{
					_price = 80000;
				}
				break;
			}
			case S84:
			{
				if (item.getTemplate() instanceof Weapon)
				{
					_price = 200000;
				}
				else
				{
					_price = 160000;
				}
				break;
			}
		}
		return _price;
	}
}