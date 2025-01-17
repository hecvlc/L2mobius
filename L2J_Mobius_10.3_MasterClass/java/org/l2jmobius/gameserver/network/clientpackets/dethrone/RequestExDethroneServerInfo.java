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
package org.l2jmobius.gameserver.network.clientpackets.dethrone;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.instancemanager.GlobalVariablesManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.dethrone.ExDethroneServerInfo;

/**
 * @author CostyKiller
 */
public class RequestExDethroneServerInfo implements ClientPacket
{
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		final long serverPoints = GlobalVariablesManager.getInstance().getLong("CONQUEST_SERVER_POINTS", 0);
		final long soulOrbScore = serverPoints * Config.CONQUEST_RATE_SERVER_SOUL_ORBS; // Custom value.
		final boolean adenOwner = (player.getClan() != null) && (player.getObjectId() == player.getClan().getLeaderId()) && (player.getClan().getCastleId() == 5); // Aden Castle Id is 5
		final boolean connected = GlobalVariablesManager.getInstance().getBoolean("CONQUEST_CONNECTED", false);
		player.sendPacket(new ExDethroneServerInfo(serverPoints, soulOrbScore, adenOwner, connected));
	}
}
