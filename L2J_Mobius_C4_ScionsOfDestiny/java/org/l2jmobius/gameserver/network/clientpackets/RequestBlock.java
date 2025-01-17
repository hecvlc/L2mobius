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
import org.l2jmobius.gameserver.data.sql.CharInfoTable;
import org.l2jmobius.gameserver.model.BlockList;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.SystemMessageId;

public class RequestBlock implements ClientPacket
{
	private static final int BLOCK = 0;
	private static final int UNBLOCK = 1;
	private static final int BLOCKLIST = 2;
	private static final int ALLBLOCK = 3;
	private static final int ALLUNBLOCK = 4;
	
	private String _name;
	private int _type;
	
	@Override
	public void read(ReadablePacket packet)
	{
		_type = packet.readInt(); // 0x00 - block, 0x01 - unblock, 0x03 - allblock, 0x04 - allunblock
		if ((_type == BLOCK) || (_type == UNBLOCK))
		{
			_name = packet.readString();
		}
	}
	
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		switch (_type)
		{
			case BLOCK:
			case UNBLOCK:
			{
				// Can't block/unblock inexisting or self.
				final int targetId = CharInfoTable.getInstance().getIdByName(_name);
				if ((targetId <= 0) || (player.getObjectId() == targetId))
				{
					player.sendPacket(SystemMessageId.YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST);
					return;
				}
				// Can't block a GM character.
				if (CharInfoTable.getInstance().getAccessLevelById(targetId) > 0)
				{
					player.sendPacket(SystemMessageId.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM);
					return;
				}
				if (_type == BLOCK)
				{
					BlockList.addToBlockList(player, targetId);
				}
				else
				{
					BlockList.removeFromBlockList(player, targetId);
				}
				break;
			}
			case BLOCKLIST:
			{
				BlockList.sendListToOwner(player);
				break;
			}
			case ALLBLOCK:
			{
				player.sendPacket(SystemMessageId.MESSAGE_REFUSAL_MODE); // Update by rocknow
				BlockList.setBlockAll(player, true);
				break;
			}
			case ALLUNBLOCK:
			{
				player.sendPacket(SystemMessageId.MESSAGE_ACCEPTANCE_MODE); // Update by rocknow
				BlockList.setBlockAll(player, false);
				break;
			}
			default:
			{
				PacketLogger.info("Unknown 0x0a block type: " + _type);
			}
		}
	}
}