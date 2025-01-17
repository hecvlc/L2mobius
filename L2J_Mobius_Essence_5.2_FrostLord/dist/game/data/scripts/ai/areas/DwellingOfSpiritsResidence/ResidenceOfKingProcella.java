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
package ai.areas.DwellingOfSpiritsResidence;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.skill.SkillCaster;

import ai.AbstractNpcAI;

/**
 * @author RobikBobik, Mobius
 */
public class ResidenceOfKingProcella extends AbstractNpcAI
{
	// NPCs
	private static final int PROCELLA = 29107;
	private static final int PROCELLA_GUARDIAN_1 = 29112;
	private static final int PROCELLA_GUARDIAN_2 = 29113;
	private static final int PROCELLA_GUARDIAN_3 = 29114;
	private static final int PROCELLA_STORM = 29115;
	// Skills
	private static final SkillHolder HURRICANE_SUMMON = new SkillHolder(50042, 1);
	private static final int HURRICANE_BOLT = 50043;
	private static final SkillHolder HURRICANE_BOLT_LV_1 = new SkillHolder(50043, 1);
	// Misc
	private static final int STORM_MAX_COUNT = 16;
	
	public ResidenceOfKingProcella()
	{
		addKillId(PROCELLA, PROCELLA_GUARDIAN_1, PROCELLA_GUARDIAN_2, PROCELLA_GUARDIAN_3);
		addSpawnId(PROCELLA);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			startQuestTimer("SPAWN_MINION", 300000 + getRandom(-15000, 15000), npc, null);
			startQuestTimer("SPAWN_STORM", 5000, npc, null);
			world.setParameter("stormCount", 0);
		}
		return null;
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "SPAWN_MINION":
			{
				final Instance world = npc.getInstanceWorld();
				if ((world != null) && (npc.getId() == PROCELLA))
				{
					world.setParameter("minion1", addSpawn(PROCELLA_GUARDIAN_1, 212663, 179421, -15486, 31011, true, 0, true, npc.getInstanceId()));
					world.setParameter("minion2", addSpawn(PROCELLA_GUARDIAN_2, 213258, 179822, -15486, 12001, true, 0, true, npc.getInstanceId()));
					world.setParameter("minion3", addSpawn(PROCELLA_GUARDIAN_3, 212558, 179974, -15486, 12311, true, 0, true, npc.getInstanceId()));
					startQuestTimer("HIDE_PROCELLA", 1000, world.getNpc(PROCELLA), null);
				}
				break;
			}
			case "SPAWN_STORM":
			{
				final Instance world = npc.getInstanceWorld();
				if ((world != null) && (world.getParameters().getInt("stormCount", 0) < STORM_MAX_COUNT))
				{
					world.getNpc(PROCELLA).doCast(HURRICANE_SUMMON.getSkill());
					final Npc procellaStorm = addSpawn(PROCELLA_STORM, world.getNpc(PROCELLA).getX() + getRandom(-500, 500), world.getNpc(PROCELLA).getY() + getRandom(-500, 500), world.getNpc(PROCELLA).getZ(), 31011, true, 0, true, npc.getInstanceId());
					procellaStorm.setRandomWalking(true);
					world.getParameters().increaseInt("stormCount", 1);
					startQuestTimer("SPAWN_STORM", 60000, world.getNpc(PROCELLA), null);
					startQuestTimer("CHECK_CHAR_INSIDE_RADIUS_NPC", 100, procellaStorm, player);
				}
				break;
			}
			case "HIDE_PROCELLA":
			{
				final Instance world = npc.getInstanceWorld();
				if (world != null)
				{
					if (world.getNpc(PROCELLA).isInvisible())
					{
						world.getNpc(PROCELLA).setInvisible(false);
					}
					else
					{
						world.getNpc(PROCELLA).setInvisible(true);
						startQuestTimer("SPAWN_MINION", 300000 + getRandom(-15000, 15000), world.getNpc(PROCELLA), player);
					}
				}
				break;
			}
			case "CHECK_CHAR_INSIDE_RADIUS_NPC":
			{
				final Instance world = npc.getInstanceWorld();
				if (world != null)
				{
					final Player plr = world.getPlayers().stream().findAny().orElse(null); // Usamos orElse(null) para evitar el Optional vacΞ�β€�Ξ’Β­o
					if ((plr != null) && (plr.isInsideRadius3D(npc, 100)))
					{
						npc.abortAttack();
						npc.abortCast();
						npc.setTarget(plr);
						
						if (plr.getKnownSkill(HURRICANE_BOLT) != null) // Verificamos si el jugador tiene la habilidad
						{
							if (plr.getAffectedSkillLevel(HURRICANE_BOLT) == 1)
							{
								npc.abortCast();
								startQuestTimer("CHECK_CHAR_INSIDE_RADIUS_NPC", 100, npc, player);
							}
							else
							{
								if (SkillCaster.checkUseConditions(npc, HURRICANE_BOLT_LV_1.getSkill()))
								{
									npc.doCast(HURRICANE_BOLT_LV_1.getSkill());
								}
							}
						}
						startQuestTimer("CHECK_CHAR_INSIDE_RADIUS_NPC", 100, npc, player);
					}
					else
					{
						startQuestTimer("CHECK_CHAR_INSIDE_RADIUS_NPC", 100, npc, player);
					}
				}
				break;
			}
		}
		return null;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (world == null)
		{
			return null;
		}
		
		if (npc.getId() == PROCELLA)
		{
			cancelQuestTimer("SPAWN_MINION", npc, player.getActingPlayer());
			cancelQuestTimer("SPAWN_STORM", npc, player.getActingPlayer());
			cancelQuestTimer("CHECK_CHAR_INSIDE_RADIUS_NPC", npc, player.getActingPlayer());
		}
		else if ((world.getParameters().getObject("minion1", Npc.class).isDead()) && (world.getParameters().getObject("minion2", Npc.class).isDead()) && (world.getParameters().getObject("minion3", Npc.class).isDead()))
		{
			startQuestTimer("HIDE_PROCELLA", 1000, world.getNpc(PROCELLA), null);
		}
		
		return super.onKill(npc, player, isSummon);
	}
	
	public static void main(String[] args)
	{
		new ResidenceOfKingProcella();
	}
}
