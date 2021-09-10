-- persons with one digit ids
INSERT INTO test1.PERSON (ID, NAME) VALUES (1, 'Álmos');
INSERT INTO test1.PERSON (ID, NAME) VALUES (2, 'Előd');
INSERT INTO test1.PERSON (ID, NAME) VALUES (3, 'Ond');
INSERT INTO test1.PERSON (ID, NAME) VALUES (4, 'Kond');
INSERT INTO test1.PERSON (ID, NAME) VALUES (5, 'Tas');
INSERT INTO test1.PERSON (ID, NAME) VALUES (6, 'Huba');
INSERT INTO test1.PERSON (ID, NAME) VALUES (7, 'Töhötöm');

-- addresses with 2 digit ids
INSERT INTO test2.ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES (10, '6060', 'Tiszakécske', 1);
INSERT INTO test2.ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES (11, '6035', 'Ballószög', 1);
INSERT INTO test2.ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES (20, '6000', 'Kecskemét', 2);
INSERT INTO test2.ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES (21, '5000', 'Szolnok', 2);
INSERT INTO test2.ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES (22, '1011', 'Budapest I', 2);
INSERT INTO test2.ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES (30, '3000', 'Hatvan', 3);
INSERT INTO test2.ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES (40, '7000', 'Sárbogárd', 4);
INSERT INTO test2.ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES (41, '1041', 'Budapest IV', 4);
INSERT INTO test2.ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES (50, '1065', 'Budapest VI', 5);
INSERT INTO test2.ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES (51, '2335', 'Taksony', 5);
INSERT INTO test2.ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES (52, '3800', 'Szikszó', 5);
INSERT INTO test2.ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES (60, '6000', 'Szikszó', null);

-- tickets with 3 digit ids
INSERT INTO test1.TICKET (ID, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID) 
		  VALUES (100, 'Ügy 100', 1, 2, null);
INSERT INTO test1.TICKET (ID, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID) 
		  VALUES (110, 'Ügy 110', 1, 2, 100);
INSERT INTO test1.TICKET (ID, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID) 
		  VALUES (111, 'Ügy 111', 1, 2, 110);
INSERT INTO test1.TICKET (ID, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID) 
		  VALUES (112, 'Ügy 112', 1, 2, 110);
INSERT INTO test1.TICKET (ID, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID) 
		  VALUES (200, 'Ügy 200', 1, null, null);
INSERT INTO test1.TICKET (ID, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID) 
		  VALUES (300, 'Ügy 300', 1, null, null);
		  
-- the ticket 400 series contains a single primary person to each person, no secondary person.
INSERT INTO test1.TICKET (ID, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID) 
		  VALUES (401, 'Ügy 401', 1, null, null);
INSERT INTO test1.TICKET (ID, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID) 
		  VALUES (402, 'Ügy 402', 2, null, null);
INSERT INTO test1.TICKET (ID, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID) 
		  VALUES (403, 'Ügy 403', 3, null, null);
INSERT INTO test1.TICKET (ID, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID) 
		  VALUES (404, 'Ügy 404', 4, null, null);
INSERT INTO test1.TICKET (ID, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID) 
		  VALUES (405, 'Ügy 405', 5, null, null);
INSERT INTO test1.TICKET (ID, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID) 
		  VALUES (406, 'Ügy 406', 6, null, null);
INSERT INTO test1.TICKET (ID, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID) 
		  VALUES (407, 'Ügy 407', 7, null, null);
		 
		  