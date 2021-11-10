-- persons with one digit ids
INSERT INTO PERSON (ID, NAME) VALUES ('1', 'Álmos');
INSERT INTO PERSON (ID, NAME) VALUES ('2', 'Előd');
INSERT INTO PERSON (ID, NAME) VALUES ('3', 'Ond');
INSERT INTO PERSON (ID, NAME) VALUES ('4', 'Kond');
INSERT INTO PERSON (ID, NAME) VALUES ('5', 'Tas');
INSERT INTO PERSON (ID, NAME) VALUES ('6', 'Huba');
INSERT INTO PERSON (ID, NAME) VALUES ('7', 'Töhötöm');

-- addresses with 2 digit ids
INSERT INTO ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES ('10', '6060', 'Tiszakécske', '1');
INSERT INTO ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES ('11', '6035', 'Ballószög', '1');
INSERT INTO ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES ('20', '6000', 'Kecskemét', '2');
INSERT INTO ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES ('21', '5000', 'Szolnok', '2');
INSERT INTO ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES ('22', '1011', 'Budapest I', '2');
INSERT INTO ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES ('30', '3000', 'Hatvan', '3');
INSERT INTO ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES ('40', '7000', 'Sárbogárd', '4');
INSERT INTO ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES ('41', '1041', 'Budapest IV', '4');
INSERT INTO ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES ('50', '1065', 'Budapest VI', '5');
INSERT INTO ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES ('51', '2335', 'Taksony', '5');
INSERT INTO ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES ('52', '3800', 'Szikszó', '5');
INSERT INTO ADDRESS (ID, ZIP, CITY, PERSON_ID) VALUES ('60', '6000', 'Szikszó', null);

-- tickets with 3 digit ids
INSERT INTO TICKET (ID, ID_STRING, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID, URI) 
		  VALUES (100, '100', 'Ügy 100', '1', '2', NULL, 'uri-100');
INSERT INTO TICKET (ID, ID_STRING, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID, URI) 
		  VALUES (110, '110', 'Ügy 110', '1', '2', '100', 'uri-110');
INSERT INTO TICKET (ID, ID_STRING, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID, URI) 
		  VALUES (111, '111', 'Ügy 111', '1', '2', '110', 'uri-111');
INSERT INTO TICKET (ID, ID_STRING, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID, URI) 
		  VALUES (112, '112', 'Ügy 112', '1', '2', '110', 'uri-112');
INSERT INTO TICKET (ID, ID_STRING, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID, URI) 
		  VALUES (200, '200', 'Ügy 200', '1', null, null, 'uri-200');
INSERT INTO TICKET (ID, ID_STRING, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID, URI) 
		  VALUES (300, '300', 'Ügy 300', '1', null, null, 'uri-300');
		  
-- the ticket 400 series contains a single primary person to each person, no secondary person.
INSERT INTO TICKET (ID, ID_STRING, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID, URI) 
		  VALUES (401, '401', 'Ügy 401', '1', null, null, 'uri-401');
INSERT INTO TICKET (ID, ID_STRING, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID, URI) 
		  VALUES (402, '402', 'Ügy 402', '2', null, null, 'uri-402');
INSERT INTO TICKET (ID, ID_STRING, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID, URI) 
		  VALUES (403, '403', 'Ügy 403', '3', null, null, 'uri-403');
INSERT INTO TICKET (ID, ID_STRING, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID, URI) 
		  VALUES (404, '404', 'Ügy 404', '4', null, null, 'uri-404');
INSERT INTO TICKET (ID, ID_STRING, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID, URI) 
		  VALUES (405, '405', 'Ügy 405', '5', null, null, 'uri-405');
INSERT INTO TICKET (ID, ID_STRING, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID, URI) 
		  VALUES (406, '406', 'Ügy 406', '6', null, null, 'uri-406');
INSERT INTO TICKET (ID, ID_STRING, TITLE, PRIMARY_PERSON_ID, SECONDARY_PERSON_ID, PARENT_ID, URI) 
		  VALUES (407, '407', 'Ügy 407', '7', null, null, 'uri-407');
		 
INSERT INTO B (ID, UID, BF1, BF2) 
		  VALUES (1000, 'b_0', 'b_0_BF1','b_0_BF2');
INSERT INTO B (ID, UID, BF1, BF2) 
		  VALUES (1001, 'b_1', 'b_1_BF1','b_1_BF2');
		  