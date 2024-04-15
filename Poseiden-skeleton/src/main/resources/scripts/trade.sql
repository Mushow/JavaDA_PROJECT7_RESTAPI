ALTER TABLE trade AUTO_INCREMENT = 1;
TRUNCATE TABLE trade;

insert into trade(account, type, buy_quantity) values ("Account #1", "Type #1", 1), ("Account #2", "Type #2", 2);