use mfp;

CREATE TABLE IF NOT EXISTS chats (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  username VARCHAR(15) NOT NULL,
  text VARCHAR(150) NOT NULL,
  expiryTimestamp BIGINT(20) NOT NULL,
  PRIMARY KEY (id));