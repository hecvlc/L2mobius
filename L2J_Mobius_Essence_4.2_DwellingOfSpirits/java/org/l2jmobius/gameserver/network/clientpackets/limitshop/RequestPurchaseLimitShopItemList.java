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
package org.l2jmobius.gameserver.network.clientpackets.limitshop;

import java.util.List;

import org.l2jmobius.commons.network.ReadablePacket;
import org.l2jmobius.gameserver.data.xml.LimitShopCraftData;
import org.l2jmobius.gameserver.data.xml.LimitShopData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.LimitShopProductHolder;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.limitshop.ExPurchaseLimitShopItemListNew;

/**
 * @author Mobius
 */
public class RequestPurchaseLimitShopItemList implements ClientPacket
{
	private int _shopType;
	
	@Override
	public void read(ReadablePacket packet)
	{
		_shopType = packet.readByte();
	}
	
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		final List<LimitShopProductHolder> products;
		switch (_shopType)
		{
			case 3: // Normal Lcoin Shop
			{
				products = LimitShopData.getInstance().getProducts();
				break;
			}
			case 4: // Lcoin Special Craft
			{
				products = LimitShopCraftData.getInstance().getProducts();
				break;
			}
			default:
			{
				return;
			}
		}
		
		// Send the packet.
		player.sendPacket(new ExPurchaseLimitShopItemListNew(player, _shopType, products));
	}
}
