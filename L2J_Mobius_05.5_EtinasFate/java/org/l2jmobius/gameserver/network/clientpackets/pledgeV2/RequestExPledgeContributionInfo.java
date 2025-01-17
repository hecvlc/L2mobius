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
package org.l2jmobius.gameserver.network.clientpackets.pledgeV2;

import org.l2jmobius.commons.network.ReadablePacket;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.pledgeV2.ExPledgeContributionInfo;

/**
 * @author Mobius
 */
public class RequestExPledgeContributionInfo implements ClientPacket
{
	private boolean _cycle;
	
	@Override
	public void read(ReadablePacket packet)
	{
		_cycle = packet.readBoolean();
	}
	
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if ((player == null) || (player.getClan() == null))
		{
			return;
		}
		
		client.sendPacket(new ExPledgeContributionInfo(player, _cycle));
	}
}