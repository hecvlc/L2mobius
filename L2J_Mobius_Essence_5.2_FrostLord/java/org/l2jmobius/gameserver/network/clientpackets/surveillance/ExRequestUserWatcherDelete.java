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
package org.l2jmobius.gameserver.network.clientpackets.surveillance;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.commons.network.ReadablePacket;
import org.l2jmobius.gameserver.data.sql.CharInfoTable;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.RelationChanged;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.network.serverpackets.surveillance.ExUserWatcherTargetList;

/**
 * @author MacuK
 */
public class ExRequestUserWatcherDelete implements ClientPacket
{
	private String _name;
	
	@Override
	public void read(ReadablePacket packet)
	{
		_name = packet.readSizedString();
		packet.readInt(); // World Id
	}
	
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		final int targetId = CharInfoTable.getInstance().getIdByName(_name);
		if (targetId == -1)
		{
			player.sendPacket(SystemMessageId.THAT_CHARACTER_DOES_NOT_EXIST);
			return;
		}
		
		if (!player.getSurveillanceList().contains(targetId))
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.C1_IS_NOT_ON_YOUR_FRIEND_LIST);
			sm.addString(_name);
			player.sendPacket(sm);
			return;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("DELETE FROM character_surveillances WHERE (charId=? AND targetId=?)"))
		{
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, targetId);
			statement.execute();
		}
		catch (Exception e)
		{
			PacketLogger.warning("ExRequestUserWatcherDelete: Could not add surveillance objectid: " + e.getMessage());
		}
		
		final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_DELETED_C1_FROM_YOUR_SURVEILLANCE_LIST);
		sm.addString(_name);
		player.sendPacket(sm);
		
		player.getSurveillanceList().remove(Integer.valueOf(targetId));
		player.sendPacket(new ExUserWatcherTargetList(player));
		
		final Player target = World.getInstance().getPlayer(targetId);
		if ((target != null) && target.isVisibleFor(player))
		{
			player.sendPacket(new RelationChanged());
		}
	}
}
