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

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.Config;
import org.l2jmobius.commons.network.ReadablePacket;
import org.l2jmobius.gameserver.ai.CtrlIntention;
import org.l2jmobius.gameserver.data.CrownTable;
import org.l2jmobius.gameserver.data.SkillTable;
import org.l2jmobius.gameserver.data.sql.ClanHallTable;
import org.l2jmobius.gameserver.enums.IllegalActionPunishmentType;
import org.l2jmobius.gameserver.handler.IItemHandler;
import org.l2jmobius.gameserver.handler.ItemHandler;
import org.l2jmobius.gameserver.instancemanager.CastleManager;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.item.Weapon;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.item.type.WeaponType;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import org.l2jmobius.gameserver.network.serverpackets.EtcStatusUpdate;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jmobius.gameserver.network.serverpackets.ItemList;
import org.l2jmobius.gameserver.network.serverpackets.ShowCalculator;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.network.serverpackets.UserInfo;
import org.l2jmobius.gameserver.util.Util;

public class UseItem implements ClientPacket
{
	private int _objectId;
	
	private static final List<Integer> SHOT_IDS = new ArrayList<>();
	static
	{
		SHOT_IDS.add(5789);
		SHOT_IDS.add(1835);
		SHOT_IDS.add(1463);
		SHOT_IDS.add(1464);
		SHOT_IDS.add(1465);
		SHOT_IDS.add(1466);
		SHOT_IDS.add(1467);
		SHOT_IDS.add(5790);
		SHOT_IDS.add(2509);
		SHOT_IDS.add(2510);
		SHOT_IDS.add(2511);
		SHOT_IDS.add(2512);
		SHOT_IDS.add(2513);
		SHOT_IDS.add(2514);
		SHOT_IDS.add(3947);
		SHOT_IDS.add(3948);
		SHOT_IDS.add(3949);
		SHOT_IDS.add(3950);
		SHOT_IDS.add(3951);
		SHOT_IDS.add(3952);
	}
	
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
		
		final Item item = player.getInventory().getItemByObjectId(_objectId);
		if (item == null)
		{
			return;
		}
		
		// Like L2OFF you can't use soulshots while sitting.
		if (player.isSitting() && SHOT_IDS.contains(item.getItemId()))
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.DUE_TO_INSUFFICIENT_S1_THE_AUTOMATIC_USE_FUNCTION_CANNOT_BE_ACTIVATED);
			sm.addItemName(item.getItemId());
			player.sendPacket(sm);
			return;
		}
		
		// Flood protect UseItem
		if (!client.getFloodProtectors().canUseItem())
		{
			return;
		}
		
		if (player.isInsideZone(ZoneId.JAIL))
		{
			player.sendMessage("You cannot use items while jailed.");
			return;
		}
		
		if (player.isStunned() || player.isConfused() || player.isParalyzed() || player.isSleeping())
		{
			player.sendMessage("You cannot use items right now.");
			return;
		}
		
		if (player.getPrivateStoreType() != 0)
		{
			player.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.getActiveTradeList() != null)
		{
			player.cancelActiveTrade();
		}
		
		// No unequipping wear-items.
		if (item.isWear())
		{
			return;
		}
		
		if (item.getTemplate().getType2() == ItemTemplate.TYPE2_QUEST)
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_QUEST_ITEMS);
			return;
		}
		
		/*
		 * Alt game - Karma punishment // SOE 736 Scroll of Escape 1538 Blessed Scroll of Escape 1829 Scroll of Escape: Clan Hall 1830 Scroll of Escape: Castle 3958 L2Day - Blessed Scroll of Escape 5858 Blessed Scroll of Escape: Clan Hall 5859 Blessed Scroll of Escape: Castle 6663 Scroll of Escape:
		 * Orc Village 6664 Scroll of Escape: Silenos Village 7117 Scroll of Escape to Talking Island 7118 Scroll of Escape to Elven Village 7119 Scroll of Escape to Dark Elf Village 7120 Scroll of Escape to Orc Village 7121 Scroll of Escape to Dwarven Village 7122 Scroll of Escape to Gludin Village
		 * 7123 Scroll of Escape to the Town of Gludio 7124 Scroll of Escape to the Town of Dion 7125 Scroll of Escape to Floran 7126 Scroll of Escape to Giran Castle Town 7127 Scroll of Escape to Hardin's Private Academy 7128 Scroll of Escape to Heine 7129 Scroll of Escape to the Town of Oren 7130
		 * Scroll of Escape to Ivory Tower 7131 Scroll of Escape to Hunters Village 7132 Scroll of Escape to Aden Castle Town 7133 Scroll of Escape to the Town of Goddard 7134 Scroll of Escape to the Rune Township. 7554 Scroll of Escape to Talking Island 7555 Scroll of Escape to Elven Village 7556
		 * Scroll of Escape to Dark Elf Village 7557 Scroll of Escape to Orc Village 7558 Scroll of Escape to Dwarven Village 7559 Scroll of Escape to Giran Castle Town 7618 Scroll of Escape - Ketra Orc Village 7619 Scroll of Escape - Varka Silenos Village 10129 Scroll of Escape : Fortress 10130
		 * Blessed Scroll of Escape : Fortress
		 */
		final int itemId = item.getItemId();
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TELEPORT && (player.getKarma() > 0) && ((itemId == 736) || (itemId == 1538) || (itemId == 1829) || (itemId == 1830) || (itemId == 3958) || (itemId == 5858) || (itemId == 5859) || (itemId == 6663) || (itemId == 6664) || (itemId >= 7117) || ((itemId >= 7554) && (itemId <= 7559)) || (itemId == 7618) || (itemId == 7619) || (itemId == 10129) || (itemId == 10130)))
		{
			return;
		}
		
		// Items that cannot be used.
		if (itemId == 57)
		{
			return;
		}
		
		// You cannot do anything else while fishing.
		if (player.isFishing() && ((itemId < 6535) || (itemId > 6540)))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_FISHING_3);
			return;
		}
		
		if ((player.getPkKills() > 0) && ((itemId >= 7816) && (itemId <= 7831)))
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
			player.sendPacket(SystemMessageId.YOU_ARE_UNABLE_TO_EQUIP_THIS_ITEM_WHEN_YOUR_PK_COUNT_IS_GREATER_THAN_OR_EQUAL_TO_ONE);
			return;
		}
		
		// Scroll of resurrection like L2OFF if you are casting you can't use them.
		if (((itemId == 737) || (itemId == 3936) || (itemId == 3959) || (itemId == 6387)) && player.isCastingNow())
		{
			return;
		}
		
		final Clan clan = player.getClan();
		if ((itemId == 5858) && ((clan == null) || (ClanHallTable.getInstance().getClanHallByOwner(clan) == null)))
		{
			player.sendPacket(new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(5858));
			return;
		}
		
		if ((itemId == 5859) && ((clan == null) || (CastleManager.getInstance().getCastleByOwner(clan) == null)))
		{
			player.sendPacket(new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(5859));
			return;
		}
		
		// A shield that can only be used by the members of a clan that owns a castle.
		if (((clan == null) || (clan.getCastleId() == 0)) && (itemId == 7015) && Config.CASTLE_SHIELD && !player.isGM())
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
			return;
		}
		
		// A shield that can only be used by the members of a clan that owns a clan hall.
		if (((clan == null) || (clan.getHideoutId() == 0)) && (itemId == 6902) && Config.CLANHALL_SHIELD && !player.isGM())
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
			return;
		}
		
		// Apella armor used by clan members may be worn by a Baron or a higher level Aristocrat.
		if ((itemId >= 7860) && (itemId <= 7879) && Config.APELLA_ARMORS && ((clan == null) || (player.getPledgeClass() < 5)) && !player.isGM())
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
			return;
		}
		
		// Clan Oath armor used by all clan members.
		if ((itemId >= 7850) && (itemId <= 7859) && Config.OATH_ARMORS && (clan == null) && !player.isGM())
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
			return;
		}
		
		// The Lord's Crown used by castle lords only.
		if ((itemId == CrownTable.CROWN_OF_THE_LORD) && Config.CASTLE_CROWN && ((clan == null) || (clan.getCastleId() == 0) || !player.isClanLeader()) && !player.isGM())
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
			return;
		}
		
		// Castle circlets used by the members of a clan that owns a castle, academy members are excluded.
		if (Config.CASTLE_CIRCLETS && (((itemId >= 6834) && (itemId <= 6840)) || (itemId == 8182) || (itemId == 8183)))
		{
			if (clan == null)
			{
				player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
				return;
			}
			
			final int circletId = CastleManager.getInstance().getCircletByCastleId(clan.getCastleId());
			if ((player.getPledgeType() == -1) || (circletId != itemId))
			{
				player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
				return;
			}
		}
		
		// Char cannot use item when dead
		if (player.isDead())
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
			sm.addItemName(itemId);
			player.sendPacket(sm);
			return;
		}
		
		// Char cannot use pet items
		if (item.getTemplate().isForWolf() || item.getTemplate().isForHatchling() || item.getTemplate().isForStrider() || item.getTemplate().isForBabyPet())
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_MAY_NOT_EQUIP_A_PET_ITEM); // You cannot equip a pet item.
			sm.addItemName(itemId);
			player.sendPacket(sm);
			return;
		}
		
		if (item.isEquipable())
		{
			// No unequipping/equipping while the player is in special conditions
			if (player.isFishing() || player.isStunned() || player.isSleeping() || player.isParalyzed() || player.isAlikeDead())
			{
				player.sendMessage("Your status does not allow you to do that.");
				return;
			}
			
			// Over-enchant protection.
			if (Config.OVER_ENCHANT_PROTECTION && !player.isGM() //
				&& (((item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON) && (item.getEnchantLevel() > Config.ENCHANT_WEAPON_MAX)) //
					|| ((item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY) && (item.getEnchantLevel() > Config.ENCHANT_JEWELRY_MAX)) //
					|| ((item.getTemplate().getType2() != ItemTemplate.TYPE2_WEAPON) && (item.getTemplate().getType2() != ItemTemplate.TYPE2_ACCESSORY) && (item.getEnchantLevel() > Config.ENCHANT_ARMOR_MAX))))
			{
				PacketLogger.info("Over-enchanted (+" + item.getEnchantLevel() + ") " + item + " has been removed from " + player);
				player.getInventory().destroyItem("Over-enchant protection", item, player, null);
				if (Config.OVER_ENCHANT_PUNISHMENT != IllegalActionPunishmentType.NONE)
				{
					player.sendMessage("[Server]: You have over-enchanted items!");
					player.sendMessage("[Server]: Respect our server rules.");
					player.sendPacket(new ExShowScreenMessage("You have over-enchanted items!", 6000));
					Util.handleIllegalPlayerAction(player, player.getName() + " has over-enchanted items.", Config.OVER_ENCHANT_PUNISHMENT);
				}
				return;
			}
			
			// Like L2OFF you can't use equips while you are casting
			if ((player.isCastingNow() || player.isCastingPotionNow() || player.isMounted()))
			{
				player.sendPacket(SystemMessageId.YOU_MAY_NOT_EQUIP_ITEMS_WHILE_CASTING_OR_PERFORMING_A_SKILL);
				return;
			}
			
			// Over enchant protection
			int bodyPart = item.getTemplate().getBodyPart();
			if (Config.PROTECTED_ENCHANT)
			{
				switch (bodyPart)
				{
					case ItemTemplate.SLOT_LR_HAND:
					case ItemTemplate.SLOT_L_HAND:
					case ItemTemplate.SLOT_R_HAND:
					{
						if (((item.getEnchantLevel() > Config.NORMAL_WEAPON_ENCHANT_LEVEL.size()) || (item.getEnchantLevel() > Config.BLESS_WEAPON_ENCHANT_LEVEL.size()) || (item.getEnchantLevel() > Config.CRYSTAL_WEAPON_ENCHANT_LEVEL.size())) && !player.isGM())
						{
							// player.setAccountAccesslevel(-1); //ban
							player.sendMessage("You have been banned for using an item wich is over enchanted!"); // message
							Util.handleIllegalPlayerAction(player, player + " has item Overenchanted! ", Config.DEFAULT_PUNISH);
							// player.closeNetConnection(); //kick
							return;
						}
						break;
					}
					case ItemTemplate.SLOT_CHEST:
					case ItemTemplate.SLOT_BACK:
					case ItemTemplate.SLOT_GLOVES:
					case ItemTemplate.SLOT_FEET:
					case ItemTemplate.SLOT_HEAD:
					case ItemTemplate.SLOT_FULL_ARMOR:
					case ItemTemplate.SLOT_LEGS:
					{
						if (((item.getEnchantLevel() > Config.NORMAL_ARMOR_ENCHANT_LEVEL.size()) || (item.getEnchantLevel() > Config.BLESS_ARMOR_ENCHANT_LEVEL.size()) || (item.getEnchantLevel() > Config.CRYSTAL_ARMOR_ENCHANT_LEVEL.size())) && !player.isGM())
						{
							// player.setAccountAccesslevel(-1); //ban
							player.sendMessage("You have been banned for using an item wich is over enchanted!"); // message
							Util.handleIllegalPlayerAction(player, player + " has item Overenchanted! ", Config.DEFAULT_PUNISH);
							// player.closeNetConnection(); //kick
							return;
						}
						break;
					}
					case ItemTemplate.SLOT_R_EAR:
					case ItemTemplate.SLOT_L_EAR:
					case ItemTemplate.SLOT_NECK:
					case ItemTemplate.SLOT_R_FINGER:
					case ItemTemplate.SLOT_L_FINGER:
					{
						if (((item.getEnchantLevel() > Config.NORMAL_JEWELRY_ENCHANT_LEVEL.size()) || (item.getEnchantLevel() > Config.BLESS_JEWELRY_ENCHANT_LEVEL.size()) || (item.getEnchantLevel() > Config.CRYSTAL_JEWELRY_ENCHANT_LEVEL.size())) && !player.isGM())
						{
							// player.setAccountAccesslevel(-1); //ban
							player.sendMessage("You have been banned for using an item wich is over enchanted!"); // message
							Util.handleIllegalPlayerAction(player, player + " has item Overenchanted! ", Config.DEFAULT_PUNISH);
							// player.closeNetConnection(); //kick
							return;
						}
						break;
					}
				}
			}
			
			// Don't allow weapon/shield hero equipment during Olimpia
			if (player.isInOlympiadMode() && ((((bodyPart == ItemTemplate.SLOT_LR_HAND) || (bodyPart == ItemTemplate.SLOT_L_HAND) || (bodyPart == ItemTemplate.SLOT_R_HAND)) && (((item.getItemId() >= 6611) && (item.getItemId() <= 6621)) || (item.getItemId() == 6842))) || Config.LIST_OLY_RESTRICTED_ITEMS.contains(item.getItemId())))
			{
				return;
			}
			
			// Don't allow Hero items equipment if not a hero
			if (!player.isHero() && (((item.getItemId() >= 6611) && (item.getItemId() <= 6621)) || (item.getItemId() == 6842)) && !player.isGM())
			{
				return;
			}
			
			if (player.isMoving() && player.isAttackingNow() && ((bodyPart == ItemTemplate.SLOT_LR_HAND) || (bodyPart == ItemTemplate.SLOT_L_HAND) || (bodyPart == ItemTemplate.SLOT_R_HAND)))
			{
				final WorldObject target = player.getTarget();
				player.setTarget(null);
				player.stopMove(null);
				player.setTarget(target);
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK);
			}
			
			// Equip or unEquip
			List<Item> items = null;
			final boolean isEquiped = item.isEquipped();
			SystemMessage sm = null;
			if (item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON)
			{
				// if used item is a weapon
				Item wep = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LRHAND);
				if (wep == null)
				{
					wep = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
				}
				
				player.checkSSMatch(item, wep);
			}
			
			// Remove the item if it's equiped
			if (isEquiped)
			{
				if (item.getEnchantLevel() > 0)
				{
					sm = new SystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED);
					sm.addNumber(item.getEnchantLevel());
					sm.addItemName(itemId);
				}
				else
				{
					sm = new SystemMessage(SystemMessageId.S1_HAS_BEEN_DISARMED);
					sm.addItemName(itemId);
				}
				player.sendPacket(sm);
				
				switch (item.getEquipSlot())
				{
					case 1:
					{
						bodyPart = ItemTemplate.SLOT_L_EAR;
						break;
					}
					case 2:
					{
						bodyPart = ItemTemplate.SLOT_R_EAR;
						break;
					}
					case 4:
					{
						bodyPart = ItemTemplate.SLOT_L_FINGER;
						break;
					}
					case 5:
					{
						bodyPart = ItemTemplate.SLOT_R_FINGER;
						break;
					}
					default:
					{
						break;
					}
				}
				
				// remove cupid's bow skills on unequip
				if (item.isCupidBow())
				{
					if (item.getItemId() == 9140)
					{
						player.removeSkill(SkillTable.getInstance().getSkill(3261, 1));
					}
					else
					{
						player.removeSkill(SkillTable.getInstance().getSkill(3260, 0));
						player.removeSkill(SkillTable.getInstance().getSkill(3262, 0));
					}
				}
				
				// unEquipItem will call also the remove bonus for augment
				items = player.getInventory().unEquipItemInBodySlotAndRecord(bodyPart);
			}
			else
			{
				// Restrict bow weapon for class except Cupid bow.
				if ((item.getTemplate() instanceof Weapon) && (((Weapon) item.getTemplate()).getItemType() == WeaponType.BOW) && !item.isCupidBow())
				{
					// Restriction not valid on Olympiad matches
					if (Config.DISABLE_BOW_CLASSES.contains(player.getClassId().getId()) && !player.isInOlympiadMode())
					{
						player.sendMessage("This item can not be equipped by your class!");
						player.sendPacket(ActionFailed.STATIC_PACKET);
						return;
					}
				}
				
				final int tempBodyPart = item.getTemplate().getBodyPart();
				Item tempItem = player.getInventory().getPaperdollItemByItemId(tempBodyPart);
				
				// check if the item replaces a wear-item
				if ((tempItem != null) && tempItem.isWear())
				{
					// dont allow an item to replace a wear-item
					return;
				}
				else if (tempBodyPart == ItemTemplate.SLOT_LR_HAND)
				{
					// this may not remove left OR right hand equipment
					tempItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
					if ((tempItem != null) && tempItem.isWear())
					{
						return;
					}
					
					tempItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
					if ((tempItem != null) && tempItem.isWear())
					{
						return;
					}
				}
				else if (tempBodyPart == ItemTemplate.SLOT_FULL_ARMOR)
				{
					// this may not remove chest or leggings
					tempItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
					if ((tempItem != null) && tempItem.isWear())
					{
						return;
					}
					
					tempItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);
					if ((tempItem != null) && tempItem.isWear())
					{
						return;
					}
				}
				
				if (item.getEnchantLevel() > 0)
				{
					sm = new SystemMessage(SystemMessageId.EQUIPPED_S1_S2);
					sm.addNumber(item.getEnchantLevel());
					sm.addItemName(itemId);
				}
				else
				{
					sm = new SystemMessage(SystemMessageId.YOU_HAVE_EQUIPPED_YOUR_S1);
					sm.addItemName(itemId);
				}
				player.sendPacket(sm);
				
				// Apply cupid's bow skills on equip
				if (item.isCupidBow())
				{
					if (item.getItemId() == 9140)
					{
						player.addSkill(SkillTable.getInstance().getSkill(3261, 1));
					}
					else
					{
						player.addSkill(SkillTable.getInstance().getSkill(3260, 0));
					}
					
					player.addSkill(SkillTable.getInstance().getSkill(3262, 0));
				}
				
				items = player.getInventory().equipItemAndRecord(item);
				
				// Charge Soulshot/Spiritshot like L2OFF
				if (item.getTemplate() instanceof Weapon)
				{
					player.rechargeAutoSoulShot(true, true, false);
				}
				
				// Consume mana - will start a task if required; returns if item is not a shadow item.
				item.decreaseMana(false);
			}
			
			player.abortAttack();
			
			player.sendPacket(new EtcStatusUpdate(player));
			// If an "invisible" item has changed (Jewels, helmet), we dont need to send broadcast packet to all other users.
			if ((((item.getTemplate().getBodyPart() & ItemTemplate.SLOT_HEAD) <= 0) && ((item.getTemplate().getBodyPart() & ItemTemplate.SLOT_NECK) <= 0) && ((item.getTemplate().getBodyPart() & ItemTemplate.SLOT_L_EAR) <= 0) && ((item.getTemplate().getBodyPart() & ItemTemplate.SLOT_R_EAR) <= 0) && ((item.getTemplate().getBodyPart() & ItemTemplate.SLOT_L_FINGER) <= 0) && ((item.getTemplate().getBodyPart() & ItemTemplate.SLOT_R_FINGER) <= 0)))
			{
				player.broadcastUserInfo();
				final InventoryUpdate iu = new InventoryUpdate();
				iu.addItems(items);
				player.sendPacket(iu);
			}
			else if ((item.getTemplate().getBodyPart() & ItemTemplate.SLOT_HEAD) > 0)
			{
				final InventoryUpdate iu = new InventoryUpdate();
				iu.addItems(items);
				player.sendPacket(iu);
				player.updateUserInfo();
			}
			else // Because of complicated jewels problem I am forced to resend the item list. :(
			{
				player.sendPacket(new UserInfo(player)); // Mobius: send UserInfo before ItemList.
				player.sendPacket(new ItemList(player, true)); // Mobius: send ItemList after UserInfo.
			}
		}
		else
		{
			final Weapon weaponItem = player.getActiveWeaponItem();
			final int itemid = item.getItemId();
			if (itemid == 4393)
			{
				player.sendPacket(new ShowCalculator(4393));
			}
			else if ((weaponItem != null) && (weaponItem.getItemType() == WeaponType.ROD) && (((itemid >= 6519) && (itemid <= 6527)) || ((itemid >= 7610) && (itemid <= 7613)) || ((itemid >= 7807) && (itemid <= 7809)) || ((itemid >= 8484) && (itemid <= 8486)) || ((itemid >= 8505) && (itemid <= 8513))))
			{
				player.getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, item);
				player.broadcastUserInfo();
				// Send a Server->Client packet ItemList to this Player to update left hand equipement
				player.sendPacket(new ItemList(player, false));
			}
			else
			{
				final IItemHandler handler = ItemHandler.getInstance().getItemHandler(itemId);
				if (handler != null)
				{
					handler.useItem(player, item);
				}
			}
		}
	}
}