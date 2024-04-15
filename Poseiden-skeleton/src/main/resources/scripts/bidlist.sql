ALTER TABLE bid_list AUTO_INCREMENT = 1;
TRUNCATE TABLE bid_list;

insert into bid_list(account, type, bid_quantity) values ("Account #1", "Type #1", 2.0), ("Account #2", "Type #2", 9.0);