-- ----------------------------
-- Table structure for rebirth_manager
-- ----------------------------
CREATE TABLE IF NOT EXISTS `rebirth_manager`(
  `playerId` int(20) NOT NULL,
  `rebirthCount` int(2) NOT NULL,
  PRIMARY KEY (`playerId`)
) DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;