ALTER TABLE rating AUTO_INCREMENT = 1;
TRUNCATE TABLE rating;

insert into rating(moodys_rating, sand_p_rating, fitch_rating, order_number) values (1, 2, 3, 10), (4, 5, 6, 20);