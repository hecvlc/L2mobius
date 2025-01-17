-- ---------------------------
-- Table structure for clanhall
-- ---------------------------
CREATE TABLE IF NOT EXISTS `clanhall` (
  `id` int(11) NOT NULL default '0',
  `name` varchar(40) NOT NULL default '',
  `ownerId` int(11) NOT NULL default '0',
  `lease` int(10) NOT NULL default '0',
  `desc` text NOT NULL,
  `location` varchar(15) NOT NULL default '',
  `paidUntil` decimal(20,0) NOT NULL default '0',
  `Grade` decimal(1,0) NOT NULL default '0',
  `paid` int( 1 ) NOT NULL default '0',
  PRIMARY KEY `id` (`id`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT IGNORE INTO `clanhall` VALUES ('21','Fortress of Resistance','0','100000','Ol Mahum Fortress of Resistance','Partisan','0','3','0');
INSERT IGNORE INTO `clanhall` VALUES ('22','Moonstone Hall','0','100000','Clan hall located in the Town of Gludio','Gludio','0','2','0');
INSERT IGNORE INTO `clanhall` VALUES ('23','Onyx Hall','0','100000','Clan hall located in the Town of Gludio','Gludio','0','2','0');
INSERT IGNORE INTO `clanhall` VALUES ('24','Topaz Hall','0','100000','Clan hall located in the Town of Gludio','Gludio','0','2','0');
INSERT IGNORE INTO `clanhall` VALUES ('25','Ruby Hall','0','100000','Clan hall located in the Town of Gludio','Gludio','0','2','0');
INSERT IGNORE INTO `clanhall` VALUES ('26','Crystal Hall','0','100000','Clan hall located in Gludin Village','Gludin','0','2','0');
INSERT IGNORE INTO `clanhall` VALUES ('27','Onyx Hall','0','100000','Clan hall located in Gludin Village','Gludin','0','2','0');
INSERT IGNORE INTO `clanhall` VALUES ('28','Sapphire Hall','0','100000','Clan hall located in Gludin Village','Gludin','0','2','0');
INSERT IGNORE INTO `clanhall` VALUES ('29','Moonstone Hall','0','100000','Clan hall located in Gludin Village','Gludin','0','2','0');
INSERT IGNORE INTO `clanhall` VALUES ('30','Emerald Hall','0','100000','Clan hall located in Gludin Village','Gludin','0','2','0');
INSERT IGNORE INTO `clanhall` VALUES ('31','The Atramental Barracks','0','100000','Clan hall located in the Town of Dion','Dion','0','1','0');
INSERT IGNORE INTO `clanhall` VALUES ('32','The Scarlet Barracks','0','100000','Clan hall located in the Town of Dion','Dion','0','1','0');
INSERT IGNORE INTO `clanhall` VALUES ('33','The Viridian Barracks','0','100000','Clan hall located in the Town of Dion','Dion','0','1','0');
INSERT IGNORE INTO `clanhall` VALUES ('34','Devastated Castle','0','100000','Contestable Clan Hall','Devastated','0','3','0');
INSERT IGNORE INTO `clanhall` VALUES ('35','Bandit Stronghold','0','100000','Contestable Clan Hall','Bandit','0','3','0');
INSERT IGNORE INTO `clanhall` VALUES ('36','The Golden Chamber','0','100000','Clan hall located in the Town of Aden','Aden','0','3','0');
INSERT IGNORE INTO `clanhall` VALUES ('37','The Silver Chamber','0','100000','Clan hall located in the Town of Aden','Aden','0','3','0');
INSERT IGNORE INTO `clanhall` VALUES ('38','The Mithril Chamber','0','100000','Clan hall located in the Town of Aden','Aden','0','3','0');
INSERT IGNORE INTO `clanhall` VALUES ('39','Silver Manor','0','100000','Clan hall located in the Town of Aden','Aden','0','3','0');
INSERT IGNORE INTO `clanhall` VALUES ('40','Gold Manor','0','100000','Clan hall located in the Town of Aden','Aden','0','3','0');
INSERT IGNORE INTO `clanhall` VALUES ('41','The Bronze Chamber','0','100000','Clan hall located in the Town of Aden','Aden','0','3','0');
INSERT IGNORE INTO `clanhall` VALUES ('42','The Golden Chamber','0','100000','Clan hall located in the Town of Giran','Giran','0','3','0');
INSERT IGNORE INTO `clanhall` VALUES ('43','The Silver Chamber','0','100000','Clan hall located in the Town of Giran','Giran','0','3','0');
INSERT IGNORE INTO `clanhall` VALUES ('44','The Mithril Chamber','0','100000','Clan hall located in the Town of Giran','Giran','0','3','0');
INSERT IGNORE INTO `clanhall` VALUES ('45','The Bronze Chamber','0','100000','Clan hall located in the Town of Giran','Giran','0','3','0');
INSERT IGNORE INTO `clanhall` VALUES ('46','Silver Manor','0','100000','Clan hall located in the Town of Giran','Giran','0','3','0');
INSERT IGNORE INTO `clanhall` VALUES ('47','Moonstone Hall','0','100000','Clan hall located in the Town of Goddard','Goddard','0','3','0');
INSERT IGNORE INTO `clanhall` VALUES ('48','Onyx Hall','0','100000','Clan hall located in the Town of Goddard','Goddard','0','3','0');
INSERT IGNORE INTO `clanhall` VALUES ('49','Emerald Hall','0','100000','Clan hall located in the Town of Goddard','Goddard','0','3','0');
INSERT IGNORE INTO `clanhall` VALUES ('50','Sapphire Hall','0','100000','Clan hall located in the Town of Goddard','Goddard','0','3','0');
-- INSERT IGNORE INTO `clanhall` VALUES ('51','Mont Chamber','0','100000','An upscale Clan hall located in the Rune Township','Rune','0','3','0');
-- INSERT IGNORE INTO `clanhall` VALUES ('52','Astaire Chamber','0','100000','An upscale Clan hall located in the Rune Township','Rune','0','3','0');
-- INSERT IGNORE INTO `clanhall` VALUES ('53','Aria Chamber','0','100000','An upscale Clan hall located in the Rune Township','Rune','0','3','0');
-- INSERT IGNORE INTO `clanhall` VALUES ('54','Yiana Chamber','0','100000','An upscale Clan hall located in the Rune Township','Rune','0','3','0');
-- INSERT IGNORE INTO `clanhall` VALUES ('55','Roien Chamber','0','100000','An upscale Clan hall located in the Rune Township','Rune','0','3','0');
-- INSERT IGNORE INTO `clanhall` VALUES ('56','Luna Chamber','0','100000','An upscale Clan hall located in the Rune Township','Rune','0','3','0');
-- INSERT IGNORE INTO `clanhall` VALUES ('57','Traban Chamber','0','100000','An upscale Clan hall located in the Rune Township','Rune','0','3','0');
-- INSERT IGNORE INTO `clanhall` VALUES ('58','Eisen Hall','0','100000','Clan hall located in the Town of Schuttgart','Schuttgart','0','2','0');
-- INSERT IGNORE INTO `clanhall` VALUES ('59','Heavy Metal Hall','0','100000','Clan hall located in the Town of Schuttgart','Schuttgart','0','2','0');
-- INSERT IGNORE INTO `clanhall` VALUES ('60','Molten Ore Hall','0','100000','Clan hall located in the Town of Schuttgart','Schuttgart','0','2','0');
-- INSERT IGNORE INTO `clanhall` VALUES ('61','Titan Hall','0','100000','Clan hall located in the Town of Schuttgart','Schuttgart','0','2','0');
INSERT IGNORE INTO `clanhall` VALUES ('62','Rainbow Springs','0','100000','','HotSpring','0','3','0');
INSERT IGNORE INTO `clanhall` VALUES ('63','Beast Farm','0','100000','','BeastFarm','0','3','0');
INSERT IGNORE INTO `clanhall` VALUES ('64','Fortress of the Dead','0','100000','','DeadForest','0','3','0');