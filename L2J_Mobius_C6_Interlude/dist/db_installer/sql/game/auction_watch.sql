-- ---------------------------
-- Table structure for auction_watch
-- ---------------------------
CREATE TABLE IF NOT EXISTS auction_watch (
  charObjId INT NOT NULL default 0,
  auctionId INT NOT NULL default 0,
  PRIMARY KEY  (charObjId, auctionId)
) DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;