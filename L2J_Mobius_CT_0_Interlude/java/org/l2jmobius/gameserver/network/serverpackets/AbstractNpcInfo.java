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
package org.l2jmobius.gameserver.network.serverpackets;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.data.sql.ClanTable;
import org.l2jmobius.gameserver.data.xml.NpcNameLocalisationData;
import org.l2jmobius.gameserver.enums.PlayerCondOverride;
import org.l2jmobius.gameserver.instancemanager.TownManager;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.Summon;
import org.l2jmobius.gameserver.model.actor.instance.Monster;
import org.l2jmobius.gameserver.model.actor.instance.Trap;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.skill.AbnormalVisualEffect;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.model.zone.type.TownZone;
import org.l2jmobius.gameserver.network.ServerPackets;

public abstract class AbstractNpcInfo extends ServerPacket
{
	protected int _x;
	protected int _y;
	protected int _z;
	protected int _heading;
	protected int _displayId;
	protected boolean _isAttackable;
	protected boolean _isSummoned;
	protected int _mAtkSpd;
	protected int _pAtkSpd;
	protected final int _runSpd;
	protected final int _walkSpd;
	protected final int _swimRunSpd;
	protected final int _swimWalkSpd;
	protected final int _flyRunSpd;
	protected final int _flyWalkSpd;
	protected double _moveMultiplier;
	protected int _rhand;
	protected int _lhand;
	protected int _chest;
	protected int _enchantEffect;
	protected float _collisionHeight;
	protected float _collisionRadius;
	protected String _name = "";
	protected String _title = "";
	protected final boolean _gmSeeInvis;
	
	public AbstractNpcInfo(Creature creature, boolean gmSeeInvis)
	{
		_isSummoned = creature.isShowSummonAnimation();
		_x = creature.getX();
		_y = creature.getY();
		_z = creature.getZ();
		_heading = creature.getHeading();
		_mAtkSpd = creature.getMAtkSpd();
		_pAtkSpd = (int) creature.getPAtkSpd();
		_moveMultiplier = creature.getMovementSpeedMultiplier();
		_runSpd = (int) Math.round(creature.getRunSpeed() / _moveMultiplier);
		_walkSpd = (int) Math.round(creature.getWalkSpeed() / _moveMultiplier);
		_swimRunSpd = (int) Math.round(creature.getSwimRunSpeed() / _moveMultiplier);
		_swimWalkSpd = (int) Math.round(creature.getSwimWalkSpeed() / _moveMultiplier);
		_flyRunSpd = creature.isFlying() ? _runSpd : 0;
		_flyWalkSpd = creature.isFlying() ? _walkSpd : 0;
		_gmSeeInvis = gmSeeInvis;
	}
	
	/**
	 * Packet for Npcs
	 */
	public static class NpcInfo extends AbstractNpcInfo
	{
		private final Npc _npc;
		private int _clanCrest = 0;
		private int _allyCrest = 0;
		private int _allyId = 0;
		private int _clanId = 0;
		
		public NpcInfo(Npc cha, Creature attacker)
		{
			super(cha, attacker.canOverrideCond(PlayerCondOverride.SEE_ALL_PLAYERS));
			_npc = cha;
			_displayId = cha.getTemplate().getDisplayId(); // On every subclass
			_rhand = cha.getRightHandItem(); // On every subclass
			_lhand = cha.getLeftHandItem(); // On every subclass
			_enchantEffect = cha.getEnchantEffect();
			_collisionHeight = cha.getTemplate().getFCollisionHeight(); // On every subclass
			_collisionRadius = cha.getTemplate().getFCollisionRadius(); // On every subclass
			_isAttackable = cha.isAutoAttackable(attacker);
			// npc crest of owning clan/ally of castle
			if (cha.isNpc() && cha.isInsideZone(ZoneId.TOWN) && (Config.SHOW_CREST_WITHOUT_QUEST || cha.getCastle().getShowNpcCrest()) && (cha.getCastle().getOwnerId() != 0))
			{
				final TownZone town = TownManager.getTown(_x, _y, _z);
				if (town != null)
				{
					final int townId = town.getTownId();
					if ((townId != 33) && (townId != 22))
					{
						final Clan clan = ClanTable.getInstance().getClan(cha.getCastle().getOwnerId());
						_clanCrest = clan.getCrestId();
						_clanId = clan.getId();
						_allyCrest = clan.getAllyCrestId();
						_allyId = clan.getAllyId();
					}
				}
			}
		}
		
		@Override
		public void write()
		{
			// Localisation related.
			String[] localisation = null;
			if (Config.MULTILANG_ENABLE)
			{
				final Player player = getPlayer();
				if (player != null)
				{
					final String lang = player.getLang();
					if ((lang != null) && !lang.equals("en"))
					{
						localisation = NpcNameLocalisationData.getInstance().getLocalisation(lang, _npc.getId());
						if (localisation != null)
						{
							_name = localisation[0];
							_title = localisation[1];
						}
					}
				}
			}
			
			ServerPackets.NPC_INFO.writeId(this);
			writeInt(_npc.getObjectId());
			writeInt(_displayId + 1000000); // npctype id
			writeInt(_isAttackable);
			writeInt(_x);
			writeInt(_y);
			writeInt(_z);
			writeInt(_heading);
			writeInt(0);
			writeInt(_mAtkSpd);
			writeInt(_pAtkSpd);
			writeInt(_runSpd);
			writeInt(_walkSpd);
			writeInt(_swimRunSpd);
			writeInt(_swimWalkSpd);
			writeInt(_flyRunSpd);
			writeInt(_flyWalkSpd);
			writeInt(_flyRunSpd);
			writeInt(_flyWalkSpd);
			writeDouble(_moveMultiplier);
			writeDouble(_npc.getAttackSpeedMultiplier());
			writeDouble(_collisionRadius);
			writeDouble(_collisionHeight);
			writeInt(_rhand); // right hand weapon
			writeInt(_chest);
			writeInt(_lhand); // left hand weapon
			writeByte(1); // name above char 1=true ... ??
			writeByte(_npc.isRunning());
			writeByte(_npc.isInCombat());
			writeByte(_npc.isAlikeDead());
			writeByte(_isSummoned ? 2 : 0); // invisible ?? 0=false 1=true 2=summoned (only works if model has a summon animation)
			if ((localisation == null) && _npc.getTemplate().isUsingServerSideName())
			{
				_name = _npc.getName(); // On every subclass
			}
			writeString(_name);
			if (_npc.isInvisible())
			{
				_title = "Invisible";
			}
			else if (localisation == null)
			{
				if (_npc.getTemplate().isUsingServerSideTitle())
				{
					_title = _npc.getTemplate().getTitle(); // On every subclass
				}
				else
				{
					_title = _npc.getTitle(); // On every subclass
				}
			}
			// Custom level titles
			if (_npc.isMonster() && (Config.SHOW_NPC_LEVEL || Config.SHOW_NPC_AGGRESSION))
			{
				String t1 = "";
				if (Config.SHOW_NPC_LEVEL)
				{
					t1 += "Lv " + _npc.getLevel();
				}
				String t2 = "";
				if (Config.SHOW_NPC_AGGRESSION)
				{
					if (!t1.isEmpty())
					{
						t2 += " ";
					}
					final Monster monster = (Monster) _npc;
					if (monster.isAggressive())
					{
						t2 += "[A]"; // Aggressive.
					}
					if ((monster.getTemplate().getClans() != null) && (monster.getTemplate().getClanHelpRange() > 0))
					{
						t2 += "[G]"; // Group.
					}
				}
				t1 += t2;
				if ((_title != null) && !_title.isEmpty())
				{
					t1 += " " + _title;
				}
				_title = _npc.isChampion() ? Config.CHAMP_TITLE + " " + t1 : t1;
			}
			else if (Config.CHAMPION_ENABLE && _npc.isChampion())
			{
				_title = (Config.CHAMP_TITLE); // On every subclass
			}
			writeString(_title);
			writeInt(0); // Title color 0=client default
			writeInt(0); // pvp flag
			writeInt(0); // karma
			writeInt(_npc.isInvisible() ? _npc.getAbnormalVisualEffects() | AbnormalVisualEffect.STEALTH.getMask() : _npc.getAbnormalVisualEffects());
			writeInt(_clanId); // clan id
			writeInt(_clanCrest); // crest id
			writeInt(_allyId); // ally id
			writeInt(_allyCrest); // all crest
			writeByte(_npc.isInsideZone(ZoneId.WATER) ? 1 : _npc.isFlying() ? 2 : 0); // C2
			writeByte(_npc.getTeam().getId());
			writeDouble(_collisionRadius);
			writeDouble(_collisionHeight);
			writeInt(_enchantEffect); // C4
			writeInt(_npc.isFlying()); // C6
		}
	}
	
	public static class TrapInfo extends AbstractNpcInfo
	{
		private final Trap _trap;
		
		public TrapInfo(Trap cha, Creature attacker)
		{
			super(cha, (attacker != null) && attacker.canOverrideCond(PlayerCondOverride.SEE_ALL_PLAYERS));
			_trap = cha;
			_displayId = cha.getTemplate().getDisplayId();
			_isAttackable = cha.isAutoAttackable(attacker);
			_rhand = 0;
			_lhand = 0;
			_collisionHeight = _trap.getTemplate().getFCollisionHeight();
			_collisionRadius = _trap.getTemplate().getFCollisionRadius();
			if (cha.getTemplate().isUsingServerSideName())
			{
				_name = cha.getName();
			}
			_title = cha.getOwner() != null ? cha.getOwner().getName() : "";
		}
		
		@Override
		public void write()
		{
			ServerPackets.NPC_INFO.writeId(this);
			writeInt(_trap.getObjectId());
			writeInt(_displayId + 1000000); // npctype id
			writeInt(_isAttackable);
			writeInt(_x);
			writeInt(_y);
			writeInt(_z);
			writeInt(_heading);
			writeInt(0);
			writeInt(_mAtkSpd);
			writeInt(_pAtkSpd);
			writeInt(_runSpd);
			writeInt(_walkSpd);
			writeInt(_swimRunSpd);
			writeInt(_swimWalkSpd);
			writeInt(_flyRunSpd);
			writeInt(_flyWalkSpd);
			writeInt(_flyRunSpd);
			writeInt(_flyWalkSpd);
			writeDouble(_moveMultiplier);
			writeDouble(_trap.getAttackSpeedMultiplier());
			writeDouble(_collisionRadius);
			writeDouble(_collisionHeight);
			writeInt(_rhand); // right hand weapon
			writeInt(_chest);
			writeInt(_lhand); // left hand weapon
			writeByte(1); // name above char 1=true ... ??
			writeByte(1);
			writeByte(_trap.isInCombat());
			writeByte(_trap.isAlikeDead());
			writeByte(_isSummoned ? 2 : 0); // invisible ?? 0=false 1=true 2=summoned (only works if model has a summon animation)
			writeString(_name);
			writeString(_title);
			writeInt(0); // title color 0 = client default
			writeInt(_trap.getPvpFlag());
			writeInt(_trap.getKarma());
			writeInt(_trap.isInvisible() ? _trap.getAbnormalVisualEffects() | AbnormalVisualEffect.STEALTH.getMask() : _trap.getAbnormalVisualEffects());
			writeInt(0); // clan id
			writeInt(0); // crest id
			writeInt(0); // C2
			writeInt(0); // C2
			writeByte(0); // C2
			writeByte(_trap.getTeam().getId());
			writeDouble(_collisionRadius);
			writeDouble(_collisionHeight);
			writeInt(0); // C4
			writeInt(0); // C6
		}
	}
	
	/**
	 * Packet for summons.
	 */
	public static class SummonInfo extends AbstractNpcInfo
	{
		private final Summon _summon;
		private final int _value;
		
		public SummonInfo(Summon cha, Creature attacker, int value)
		{
			super(cha, attacker.canOverrideCond(PlayerCondOverride.SEE_ALL_PLAYERS));
			_summon = cha;
			_value = value;
			_isAttackable = cha.isAutoAttackable(attacker);
			_rhand = cha.getWeapon();
			_lhand = 0;
			_chest = cha.getArmor();
			_enchantEffect = cha.getTemplate().getWeaponEnchant();
			_name = cha.getName();
			_title = (cha.getOwner() != null) && cha.getOwner().isOnline() ? cha.getOwner().getName() : "";
			_displayId = cha.getTemplate().getDisplayId();
			_collisionHeight = cha.getTemplate().getFCollisionHeight();
			_collisionRadius = cha.getTemplate().getFCollisionRadius();
		}
		
		@Override
		public void write()
		{
			ServerPackets.NPC_INFO.writeId(this);
			writeInt(_summon.getObjectId());
			writeInt(_displayId + 1000000); // npctype id
			writeInt(_isAttackable);
			writeInt(_x);
			writeInt(_y);
			writeInt(_z);
			writeInt(_heading);
			writeInt(0);
			writeInt(_mAtkSpd);
			writeInt(_pAtkSpd);
			writeInt(_runSpd);
			writeInt(_walkSpd);
			writeInt(_swimRunSpd);
			writeInt(_swimWalkSpd);
			writeInt(_flyRunSpd);
			writeInt(_flyWalkSpd);
			writeInt(_flyRunSpd);
			writeInt(_flyWalkSpd);
			writeDouble(_moveMultiplier);
			writeDouble(_summon.getAttackSpeedMultiplier());
			writeDouble(_collisionRadius);
			writeDouble(_collisionHeight);
			writeInt(_rhand); // right hand weapon
			writeInt(_chest);
			writeInt(_lhand); // left hand weapon
			writeByte(1); // name above char 1=true ... ??
			writeByte(1); // always running 1=running 0=walking
			writeByte(_summon.isInCombat());
			writeByte(_summon.isAlikeDead());
			writeByte(_isSummoned ? 2 : _value); // invisible ?? 0=false 1=true 2=summoned (only works if model has a summon animation)
			writeString(_name);
			writeString(_title);
			writeInt(1); // Title color 0=client default
			writeInt(_summon.getPvpFlag());
			writeInt(_summon.getKarma());
			writeInt(_gmSeeInvis && _summon.isInvisible() ? _summon.getAbnormalVisualEffects() | AbnormalVisualEffect.STEALTH.getMask() : _summon.getAbnormalVisualEffects());
			writeInt(0); // clan id
			writeInt(0); // crest id
			writeInt(0); // C2
			writeInt(0); // C2
			writeByte(_summon.isInsideZone(ZoneId.WATER) ? 1 : _summon.isFlying() ? 2 : 0); // C2
			writeByte(_summon.getTeam().getId());
			writeDouble(_collisionRadius);
			writeDouble(_collisionHeight);
			writeInt(_enchantEffect); // C4
			writeInt(0); // C6
		}
	}
}
