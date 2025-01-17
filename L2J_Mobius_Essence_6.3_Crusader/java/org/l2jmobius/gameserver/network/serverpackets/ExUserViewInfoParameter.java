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
import org.l2jmobius.gameserver.enums.ItemGrade;
import org.l2jmobius.gameserver.enums.ShotType;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.type.WeaponType;
import org.l2jmobius.gameserver.model.stats.Stat;
import org.l2jmobius.gameserver.model.stats.TraitType;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Mobius
 */
public class ExUserViewInfoParameter extends ServerPacket
{
	private final Player _player;
	
	public ExUserViewInfoParameter(Player player)
	{
		_player = player;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_USER_VIEW_INFO_PARAMETER.writeId(this);
		
		int index = 0;
		
		// Number of parameters.
		writeInt(185);
		
		// XXX Attack Section
		// P. Atk. (%)
		writeShort(index++);
		writeInt((_player.getPAtk() / Config.MAX_PATK) * 100);
		
		// P. Atk. (num.)
		writeShort(index++);
		writeInt(_player.getPAtk());
		
		// M. Atk. (%)
		writeShort(index++);
		writeInt((_player.getMAtk() / Config.MAX_MATK) * 100);
		
		// M. Atk. (num)
		writeShort(index++);
		writeInt(_player.getMAtk());
		
		// Soulshot Damage - Activation
		writeShort(index++);
		writeInt((_player.isChargedShot(ShotType.BLESSED_SOULSHOTS) || _player.isChargedShot(ShotType.SOULSHOTS)) ? (10000 + (_player.getActiveRubyJewel() != null ? (int) _player.getActiveRubyJewel().getBonus() * 1000 : 0)) : 0);
		
		// Spiritshot Damage - Activation
		writeShort(index++);
		writeInt((_player.isChargedShot(ShotType.BLESSED_SPIRITSHOTS) || _player.isChargedShot(ShotType.SPIRITSHOTS)) ? (10000 + (_player.getActiveShappireJewel() != null ? (int) _player.getActiveShappireJewel().getBonus() * 1000 : 0)) : 0);
		
		// Soulshot Damage - Enchanted Weapons
		writeShort(index++);
		writeInt((((_player.getActiveWeaponInstance() != null) && _player.getActiveWeaponInstance().isEnchanted()) ? (int) (_player.getActiveWeaponInstance().getEnchantLevel() * (_player.getActiveWeaponItem().getItemGrade() == ItemGrade.S ? 1.6 : _player.getActiveWeaponItem().getItemGrade() == ItemGrade.A ? 1.4 : _player.getActiveWeaponItem().getItemGrade() == ItemGrade.B ? 0.7 : _player.getActiveWeaponItem().getItemGrade().equals(ItemGrade.C) ? 0.4 : _player.getActiveWeaponItem().getItemGrade().equals(ItemGrade.D) ? 0.4 : 0) * 100) : 0));
		
		// Spiritshot Damage - Enchanted Weapons
		writeShort(index++);
		writeInt((((_player.getActiveWeaponInstance() != null) && _player.getActiveWeaponInstance().isEnchanted()) ? (int) (_player.getActiveWeaponInstance().getEnchantLevel() * (_player.getActiveWeaponItem().getItemGrade() == ItemGrade.S ? 1.6 : _player.getActiveWeaponItem().getItemGrade() == ItemGrade.A ? 1.4 : _player.getActiveWeaponItem().getItemGrade() == ItemGrade.B ? 0.7 : _player.getActiveWeaponItem().getItemGrade().equals(ItemGrade.C) ? 0.4 : _player.getActiveWeaponItem().getItemGrade().equals(ItemGrade.D) ? 0.4 : 0) * 100) : 0));
		
		// Soulshot Damage - Misc.
		writeShort(index++);
		writeInt(_player.getActiveRubyJewel() != null ? (int) _player.getActiveRubyJewel().getBonus() * 1000 : 0);
		
		// Spiritshot Damage - Misc.
		writeShort(index++);
		writeInt(_player.getActiveShappireJewel() != null ? (int) _player.getActiveShappireJewel().getBonus() * 1000 : 0);
		
		// Basic PvP Damage
		writeShort(index++);
		writeInt((int) _player.getStat().getValue(Stat.PVP_PHYSICAL_ATTACK_DAMAGE) * 100);
		
		// P. Skill Damage in PvP
		writeShort(index++);
		writeInt((int) _player.getStat().getValue(Stat.PVP_PHYSICAL_SKILL_DAMAGE) * 100);
		
		// M. Skill Damage in PvP
		writeShort(index++);
		writeInt((int) _player.getStat().getValue(Stat.PVP_MAGICAL_SKILL_DAMAGE) * 100);
		
		// Inflicted PvP Damage
		writeShort(index++);
		writeInt(0);
		
		// PvP Damage Decrease Ignore
		writeShort(index++);
		writeInt(0);
		
		// Basic PvE Damage
		writeShort(index++);
		writeInt((int) _player.getStat().getValue(Stat.PVE_PHYSICAL_ATTACK_DAMAGE) * 100);
		
		// P. Skill Damage in PvE
		writeShort(index++);
		writeInt((int) _player.getStat().getValue(Stat.PVE_PHYSICAL_SKILL_DAMAGE) * 100);
		
		// M. Skill Damage in PvE
		writeShort(index++);
		writeInt((int) _player.getStat().getValue(Stat.PVE_MAGICAL_SKILL_DAMAGE) * 100);
		
		// PvE Damage
		writeShort(index++);
		writeInt(0);
		
		// PvE Damage Decrease Ignore
		writeShort(index++);
		writeInt(0);
		
		// Basic Power
		writeShort(index++);
		writeInt(0);
		
		// P. Skill Power
		writeShort(index++);
		writeInt((int) _player.getStat().getValue(Stat.PHYSICAL_SKILL_POWER) * 100);
		
		// M. Skill Power
		writeShort(index++);
		writeInt((int) _player.getStat().getValue(Stat.MAGICAL_SKILL_POWER) * 100);
		
		// AoE Skill Damage
		writeShort(index++);
		writeInt(0);
		
		// Damage Bonus - Sword
		writeShort(index++);
		writeInt(((_player.getActiveWeaponInstance() != null) && (_player.getActiveWeaponInstance().getItemType() == WeaponType.SWORD)) ? _player.getStat().getWeaponBonusPAtk() : 0);
		
		// Damage Bonus - Ancient Sword
		writeShort(index++);
		writeInt((_player.getActiveWeaponInstance() != null) && (_player.getActiveWeaponInstance().getItemType() == WeaponType.ANCIENTSWORD) ? _player.getStat().getWeaponBonusPAtk() : 0);
		
		// Damage Bonus - Dagger
		writeShort(index++);
		writeInt((_player.getActiveWeaponInstance() != null) && (_player.getActiveWeaponInstance().getItemType() == WeaponType.DAGGER) ? _player.getStat().getWeaponBonusPAtk() : 0);
		
		// Damage Bonus - Rapier
		writeShort(index++);
		writeInt((_player.getActiveWeaponInstance() != null) && (_player.getActiveWeaponInstance().getItemType() == WeaponType.RAPIER) ? _player.getStat().getWeaponBonusPAtk() : 0);
		
		// Damage Bonus - Blunt Weapon
		writeShort(index++);
		writeInt((_player.getActiveWeaponInstance() != null) && ((_player.getActiveWeaponInstance().getItemType() == WeaponType.ETC) || (_player.getActiveWeaponInstance().getItemType() == WeaponType.BLUNT) || (_player.getActiveWeaponInstance().getItemType() == WeaponType.DUALBLUNT)) ? _player.getStat().getWeaponBonusPAtk() : 0);
		
		// Damage Bonus - Spear
		writeShort(index++);
		writeInt((_player.getActiveWeaponInstance() != null) && (_player.getActiveWeaponInstance().getItemType() == WeaponType.POLE) ? _player.getStat().getWeaponBonusPAtk() : 0);
		
		// Damage Bonus - Fists
		writeShort(index++);
		writeInt((_player.getActiveWeaponInstance() != null) && ((_player.getActiveWeaponInstance().getItemType() == WeaponType.FIST) || (_player.getActiveWeaponInstance().getItemType() == WeaponType.DUALFIST)) ? _player.getStat().getWeaponBonusPAtk() : 0);
		
		// Damage Bonus - Dual Swords
		writeShort(index++);
		writeInt((_player.getActiveWeaponInstance() != null) && (_player.getActiveWeaponInstance().getItemType() == WeaponType.DUAL) ? _player.getStat().getWeaponBonusPAtk() : 0);
		
		// Damage Bonus - Bow
		writeShort(index++);
		writeInt((_player.getActiveWeaponInstance() != null) && ((_player.getActiveWeaponInstance().getItemType() == WeaponType.BOW) || (_player.getActiveWeaponInstance().getItemType() == WeaponType.CROSSBOW) || (_player.getActiveWeaponInstance().getItemType() == WeaponType.TWOHANDCROSSBOW)) ? _player.getStat().getWeaponBonusPAtk() : 0);
		
		// Damage Bonus - Firearms
		writeShort(index++);
		writeInt((_player.getActiveWeaponInstance() != null) && (_player.getActiveWeaponInstance().getItemType() == WeaponType.PISTOLS) ? _player.getStat().getWeaponBonusPAtk() : 0);
		
		// XXX Defense Section
		// P. Def. (%)
		writeShort(index++);
		writeInt((_player.getTemplate().getBasePDef() / _player.getPDef()) * 100);
		
		// P. Def. (num.)
		writeShort(index++);
		writeInt(_player.getPDef());
		
		// M. Def. (%)
		writeShort(index++);
		writeInt((_player.getTemplate().getBaseMDef() / _player.getMDef()) * 100);
		
		// M. Def. (num.)
		writeShort(index++);
		writeInt(_player.getMDef());
		
		// Soulshot Damage Resistance
		writeShort(index++);
		writeInt((int) (100 - (_player.getStat().getValue(Stat.SOULSHOT_RESISTANCE, 1) * 100)));
		
		// Spiritshot Damage Resistance
		writeShort(index++);
		writeInt((int) (100 - (_player.getStat().getValue(Stat.SPIRITSHOT_RESISTANCE, 1) * 100)));
		
		// Received basic PvP Damage
		writeShort(index++);
		writeInt(0);
		
		// Received P. Skill Damage in PvP
		writeShort(index++);
		writeInt(0);
		
		// Received M. Skill Damage in PvP
		writeShort(index++);
		writeInt(0);
		
		// Received PvP Damage
		writeShort(index++);
		writeInt((int) _player.getStat().getValue(Stat.PVP_DAMAGE_TAKEN));
		
		// PvP Damage Decrease
		writeShort(index++);
		writeInt(0);
		
		// Received basic PvE Damage
		writeShort(index++);
		writeInt(0);
		
		// Received P. Skill Damage in PvE
		writeShort(index++);
		writeInt(0);
		
		// Received M. Skill Damage in PvE
		writeShort(index++);
		writeInt(0);
		
		// Received PvE Damage
		writeShort(index++);
		writeInt((int) _player.getStat().getValue(Stat.PVE_DAMAGE_TAKEN));
		
		// PvE Damage Decrease
		writeShort(index++);
		writeInt(0);
		
		// Received basic damage power
		writeShort(index++);
		writeInt(0);
		
		// P. Skill Power when hit
		writeShort(index++);
		writeInt(0);
		
		// M. Skill Power when hit
		writeShort(index++);
		writeInt(0);
		
		// Received AoE Skill Damage
		writeShort(index++);
		writeInt(0);
		
		// Damage Resistance Bonus - Sword
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.SWORD) * 100);
		
		// Damage Resistance Bonus - Ancient Sword
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.ANCIENTSWORD) * 100);
		
		// Damage Resistance Bonus - Dagger
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.DAGGER) * 100);
		
		// Damage Resistance Bonus - Rapier
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.RAPIER) * 100);
		
		// Damage Resistance Bonus - Blunt Weapon
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.BLUNT) * 100);
		
		// Damage Resistance Bonus - Spear
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.POLE) * 100);
		
		// Damage Resistance Bonus - Fists
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.FIST) * 100);
		
		// Damage Resistance Bonus - Dual Swords
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.DUAL) * 100);
		
		// Damage Resistance Bonus - Bow
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.BOW) * 100);
		
		// Damage Resistance Bonus - Firearms
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.PISTOLS) * 100);
		
		// Shield Defense (%)
		writeShort(index++);
		// writeInt((_player.getStat().getShldDef() - _player.getShldDef()) / _player.getShldDef());
		writeInt(_player.getStat().getShldDef());
		
		// Shield Defence (num.)
		writeShort(index++);
		writeInt(_player.getShldDef());
		
		// Shield Defence Rate
		writeShort(index++);
		writeInt(_player.getStat().getShldDef());
		
		// M. Damage Resistance (%)
		writeShort(index++);
		writeInt(0);
		
		// M. Damage Resistance (num.)
		writeShort(index++);
		writeInt(0);
		
		// M. Damage Reflection (%)
		writeShort(index++);
		writeInt(0);
		
		// M. Damage Reflection Resistance
		writeShort(index++);
		writeInt(0);
		
		// Received Fixed Damage (%)
		writeShort(index++);
		writeInt(0);
		
		// Casting Interruption Rate (%)
		writeShort(index++);
		writeInt(0);
		
		// Casting Interruption Rate (num.)
		writeShort(index++);
		writeInt(0);
		
		// XXX Accuracy Section
		// P. Accuracy (%)
		writeShort(index++);
		writeInt(0);
		
		// P. Accuracy (num.)
		writeShort(index++);
		writeInt(_player.getAccuracy());
		
		// M. Accuracy (%)
		writeShort(index++);
		writeInt(0);
		
		// M. Accuracy (num.)
		writeShort(index++);
		writeInt(_player.getMagicAccuracy());
		
		// Vital Point Attack Rate (%)
		writeShort(index++);
		writeInt(0);
		
		// Vital Point Attack Rate (num.)
		writeShort(index++);
		writeInt(0);
		
		// XXX Evasion Section
		// P. Evasion (%)
		writeShort(index++);
		writeInt(((_player.getEvasionRate() * 100) / Config.MAX_EVASION));
		
		// P. Evasion (num.)
		writeShort(index++);
		writeInt(_player.getEvasionRate());
		
		// M. Evasion (%)
		writeShort(index++);
		writeInt(((_player.getMagicEvasionRate() * 100) / Config.MAX_EVASION));
		
		// M. Evasion (num.)
		writeShort(index++);
		writeInt(_player.getMagicEvasionRate());
		
		// Received Vital Point Attack Rate (%)
		writeShort(index++);
		writeInt(0);
		
		// Received Vital Point Attack Rate (num.)
		writeShort(index++);
		writeInt(0);
		
		// P. Skill Evasion (%)
		writeShort(index++);
		writeInt(0);
		
		// M. Skill Evasion (%)
		writeShort(index++);
		writeInt(0);
		
		// XXX Speed Section
		// Atk. Spd. (%)
		writeShort(index++);
		writeInt(((_player.getPAtkSpd() * 100) / Config.MAX_PATK_SPEED));
		
		// Atk. Spd. (num.)
		writeShort(index++);
		writeInt(_player.getStat().getPAtkSpd());
		
		// Casting Spd. (%)
		writeShort(index++);
		writeInt((_player.getMAtkSpd() * 100) / Config.MAX_MATK_SPEED);
		
		// Casting Spd. (num.)
		writeShort(index++);
		writeInt(_player.getStat().getMAtkSpd());
		
		// Speed (%)
		writeShort(index++);
		writeInt((int) ((_player.getMoveSpeed() * 100) / Config.MAX_RUN_SPEED));
		
		// Speed (num.)
		writeShort(index++);
		writeInt((int) _player.getStat().getMoveSpeed());
		
		// XXX Critical Rate Section
		// Basic Critical Rate (%)
		writeShort(index++);
		writeInt(_player.getStat().getCriticalHit());
		
		// Basic Critical Rate (num.)
		writeShort(index++);
		writeInt(_player.getStat().getCriticalHit());
		
		// P. Skill Critical Rate (%)
		writeShort(index++);
		writeInt(0);
		
		// P. Skill Critical Rate (num.)
		writeShort(index++);
		writeInt(0);
		
		// M. Skill Critical Rate (%)
		writeShort(index++);
		writeInt(0);
		
		// M. Skill Critical Rate (num.)
		writeShort(index++);
		writeInt(0);
		
		// Received basic Critical Rate (%)
		writeShort(index++);
		writeInt(0);
		
		// Received basic Critical Rate (num.)
		writeShort(index++);
		writeInt(0);
		
		// Received P. Skill Critical Rate (%)
		writeShort(index++);
		writeInt(0);
		
		// Received P. Skill Critical Rate (num.)
		writeShort(index++);
		writeInt(0);
		
		// Received M. Skill Critical Rate (%)
		writeShort(index++);
		writeInt(0);
		
		// Received M. Skill Critical Rate (num.)
		writeShort(index++);
		writeInt(0);
		
		// XXX Critical Damage Section
		// Basic Critical Damage (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getValue(Stat.CRITICAL_DAMAGE) * 100);
		
		// Basic Critical Damage (num.)
		writeShort(index++);
		writeInt((int) _player.getStat().getCriticalDmg(1) * 100);
		
		// P. Skill Critical Damage (%)
		writeShort(index++);
		writeInt(0);
		
		// P. Skill Critical Damage (num.)
		writeShort(index++);
		writeInt(0);
		
		// M. Skill Critical Damage (%)
		writeShort(index++);
		writeInt(0);
		
		// M. Skill Critical Damage (num.)
		writeShort(index++);
		writeInt(0);
		
		// Received Basic Critical Damage (%)
		writeShort(index++);
		writeInt(0);
		
		// Received Basic Critical Damage (num.)
		writeShort(index++);
		writeInt(0);
		
		// Received P. Skill Critical Damage (%)
		writeShort(index++);
		writeInt(0);
		
		// Received P. Skill Critical Damage (num.)
		writeShort(index++);
		writeInt(0);
		
		// Received M. Skill Critical Damage (%)
		writeShort(index++);
		writeInt(0);
		
		// Received M. Skill Critical Damage (num.)
		writeShort(index++);
		writeInt(0);
		
		// XXX Recovery Section
		// HP ReCovery Potions' Effect (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getValue(Stat.ADDITIONAL_POTION_HP) * 100);
		
		// HP Recovery Potions' Effect (num.)
		writeShort(index++);
		writeInt((int) _player.getStat().getValue(Stat.ADDITIONAL_POTION_HP) * 100);
		
		// MP Recovery Potions' Effect (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getValue(Stat.ADDITIONAL_POTION_MP) * 100);
		
		// MP Recovery Potions' Effect (num.)
		writeShort(index++);
		writeInt((int) _player.getStat().getValue(Stat.ADDITIONAL_POTION_MP) * 100);
		
		// HP Recovery Rate (%)
		writeShort(index++);
		writeInt(_player.getStat().getHpRegen());
		
		// HP Recovery Rate (num.)
		writeShort(index++);
		writeInt(_player.getStat().getHpRegen());
		
		// HP Recovery Rate while standing (%)
		writeShort(index++);
		writeInt(!_player.isMoving() ? _player.getStat().getHpRegen() : 0);
		
		// HP Recovery Rate while standing (num.)
		writeShort(index++);
		writeInt(!_player.isMoving() ? _player.getStat().getHpRegen() : 0);
		
		// HP Recovery Rate while sitting (%)
		writeShort(index++);
		writeInt(_player.isSitting() ? _player.getStat().getHpRegen() : 0);
		
		// HP Recovery Rate while sitting (num.)
		writeShort(index++);
		writeInt(_player.isSitting() ? _player.getStat().getHpRegen() : 0);
		
		// HP Recovery Rate while walking (%)
		writeShort(index++);
		writeInt((_player.isMoving() && !_player.isRunning()) ? _player.getStat().getHpRegen() : 0);
		
		// HP Recovery Rate while walking (num.)
		writeShort(index++);
		writeInt((_player.isMoving() && !_player.isRunning()) ? _player.getStat().getHpRegen() : 0);
		
		// HP Recovery Rate while running (%)
		writeShort(index++);
		writeInt(_player.isRunning() ? _player.getStat().getHpRegen() : 0);
		
		// HP Recovery Rate while running (num.)
		writeShort(index++);
		writeInt(_player.isRunning() ? _player.getStat().getHpRegen() : 0);
		
		// MP Recovery Rate (%)
		writeShort(index++);
		writeInt(_player.getStat().getMpRegen());
		
		// MP Recovery Rate (num.)
		writeShort(index++);
		writeInt(_player.getStat().getMpRegen());
		
		// MP Recovery Rate while standing (%)
		writeShort(index++);
		writeInt(!_player.isMoving() ? _player.getStat().getMpRegen() : 0);
		
		// MP Recovery Rate while standing (num.)
		writeShort(index++);
		writeInt(!_player.isMoving() ? _player.getStat().getMpRegen() : 0);
		
		// MP Recovery Rate while sitting (%)
		writeShort(index++);
		writeInt(_player.isSitting() ? _player.getStat().getMpRegen() : 0);
		
		// MP Recovery Rate while sitting (num.)
		writeShort(index++);
		writeInt(_player.isSitting() ? _player.getStat().getMpRegen() : 0);
		
		// MP Recovery Rate while walking (%)
		writeShort(index++);
		writeInt((_player.isMoving() && !_player.isRunning()) ? _player.getStat().getMpRegen() : 0);
		
		// MP Recovery Rate while walking (num.)
		writeShort(index++);
		writeInt((_player.isMoving() && !_player.isRunning()) ? _player.getStat().getMpRegen() : 0);
		
		// MP Recovery Rate while running (%)
		writeShort(index++);
		writeInt(_player.isRunning() ? _player.getStat().getMpRegen() : 0);
		
		// MP Recovery Rate while running (num.)
		writeShort(index++);
		writeInt(_player.isRunning() ? _player.getStat().getMpRegen() : 0);
		
		// CP Recovery Rate (%)
		writeShort(index++);
		writeInt(_player.getStat().getCpRegen());
		
		// CP Recovery Rate (num.)
		writeShort(index++);
		writeInt(_player.getStat().getCpRegen());
		
		// CP Recovery Rate while standing (%)
		writeShort(index++);
		writeInt(!_player.isMoving() ? _player.getStat().getCpRegen() : 0);
		
		// CP Recovery Rate while standing (num.)
		writeShort(index++);
		writeInt(!_player.isMoving() ? _player.getStat().getCpRegen() : 0);
		
		// CP Recovery Rate while sitting (%)
		writeShort(index++);
		writeInt(_player.isSitting() ? _player.getStat().getCpRegen() : 0);
		
		// CP Recovery Rate while sitting (num.)
		writeShort(index++);
		writeInt(_player.isSitting() ? _player.getStat().getCpRegen() : 0);
		
		// CP Recovery Rate while walking (%)
		writeShort(index++);
		writeInt((_player.isMoving() && !_player.isRunning()) ? _player.getStat().getCpRegen() : 0);
		
		// CP Recovery Rate while walking (num.)
		writeShort(index++);
		writeInt((_player.isMoving() && !_player.isRunning()) ? _player.getStat().getCpRegen() : 0);
		
		// CP Recovery Rate while running (%)
		writeShort(index++);
		writeInt(_player.isRunning() ? _player.getStat().getCpRegen() : 0);
		
		// CP Recovery Rate while running (num.)
		writeShort(index++);
		writeInt(_player.isRunning() ? _player.getStat().getCpRegen() : 0);
		
		// XXX Skill Cooldown Section
		// P. Skill Cooldown (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getReuseTypeValue(1) * 100);
		
		// M. Skill Cooldown (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getReuseTypeValue(2) * 100);
		
		// Song/ Dance Cooldown (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getReuseTypeValue(3) * 100);
		
		// XXX MP Consumption Section
		// P. Skill MP Consumption Decrease (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getMpConsumeTypeValue(1) * 100);
		
		// M. Skill MP Consumption Decrease (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getMpConsumeTypeValue(2) * 100);
		
		// Song/ Dance MP Consumption Decrease (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getMpConsumeTypeValue(3) * 100);
		
		// P. Skill MP Consumption Decrease (num.)
		writeShort(index++);
		writeInt((int) _player.getStat().getMpConsumeTypeValue(1) * 100);
		
		// M. Skill MP Consumption Decrease (num.)
		writeShort(index++);
		writeInt((int) _player.getStat().getMpConsumeTypeValue(2) * 100);
		
		// Song/ Dance MP Consumption Decrease (num.)
		writeShort(index++);
		writeInt((int) _player.getStat().getMpConsumeTypeValue(3) * 100);
		
		// XXX Anomalies Section
		// Buff Cancel Resistance Bonus (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getValue(Stat.RESIST_DISPEL_BUFF) * 100);
		
		// Debuff/ Anomaly Resistance Bonus (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getValue(Stat.ABNORMAL_RESIST_MAGICAL) * 100);
		
		// Paralysis Atk. Rate (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getAttackTrait(TraitType.PARALYZE) * 100);
		
		// Shock Atk. Rate (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getAttackTrait(TraitType.SHOCK) * 100);
		
		// Knockback Atk. Rate (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getAttackTrait(TraitType.KNOCKBACK) * 100);
		
		// Sleep Atk. Rate (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getAttackTrait(TraitType.SLEEP) * 100);
		
		// Imprisonment Atk. Rate (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getAttackTrait(TraitType.IMPRISON) * 100);
		
		// Pull Atk. Rate (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getAttackTrait(TraitType.PULL) * 100);
		
		// Fear Atk. Rate (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getAttackTrait(TraitType.FEAR) * 100);
		
		// Silence Atk. Rate (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getAttackTrait(TraitType.SILENCE) * 100);
		
		// Hold Atk. Rate (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getAttackTrait(TraitType.HOLD) * 100);
		
		// Suppression Atk. Rate (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getAttackTrait(TraitType.SUPPRESSION) * 100);
		
		// Infection Atk. Rate (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getAttackTrait(TraitType.INFECTION) * 100);
		
		// Paralysis Resistance (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.PARALYZE) * 100);
		
		// Shock Resistance (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.SHOCK) * 100);
		
		// Knockback Resistance (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.KNOCKBACK) * 100);
		
		// Sleep Resistance (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.SLEEP) * 100);
		
		// Imprisonment Resistance (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.IMPRISON) * 100);
		
		// Pull Resistance (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.PULL) * 100);
		
		// Fear Resistance (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.FEAR) * 100);
		
		// Silence Resistance (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.SLEEP) * 100);
		
		// Hold Resistance (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.HOLD) * 100);
		
		// Suppresion Resistance (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.SUPPRESSION) * 100);
		
		// Infection Resistance (%)
		writeShort(index++);
		writeInt((int) _player.getStat().getDefenceTrait(TraitType.INFECTION) * 100);
	}
}