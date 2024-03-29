--
--         parent
--        ------
--   -----ˇ--  |     primary --------         ---------
--   |TICKET|--|        ---->|Person|         |Address|
--   |      |-----------|    |      | <-------|       |
--   -------- *         ---->|      |        *|       |
--                 secondary --------         ---------
--


CREATE SEQUENCE SQL_TEST_EXISTS_SEQ START WITH 1000 INCREMENT BY 1;

CREATE TABLE TICKET (
  "ID" NUMBER(19) NOT NULL,
  "TITLE" VARCHAR2(255) NULL,
  "PRIMARY_PERSON_ID" NUMBER(19) NULL,
  "SECONDARY_PERSON_ID" NUMBER(19) NULL,
  "PARENT_ID" NUMBER(19) NULL
);
ALTER TABLE TICKET ADD CONSTRAINT SYS_001 PRIMARY KEY (ID);

CREATE TABLE PERSON (
  "ID" NUMBER(19) NOT NULL, 
  "NAME"  VARCHAR2(255) NULL
);
ALTER TABLE PERSON ADD CONSTRAINT SYS_002 PRIMARY KEY (ID);

CREATE TABLE ADDRESS (
  "ID" NUMBER(19) NOT NULL,
  "ZIP" VARCHAR2(255) NULL,
  "CITY" VARCHAR2(255) NULL,
  "PERSON_ID" NUMBER(19) NULL,
  "CREATED" VARCHAR2(4000) NULL
);
ALTER TABLE ADDRESS ADD CONSTRAINT SYS_003 PRIMARY KEY (ID);


CREATE INDEX TICKET_PRIMARY_PERSON_ID_IDX ON TICKET (PRIMARY_PERSON_ID);
CREATE INDEX TICKET_SECONDARY_PERSON_ID_IDX ON TICKET (SECONDARY_PERSON_ID);
CREATE INDEX TICKET_PARENT_ID_IDX ON TICKET (PARENT_ID);

CREATE INDEX ADDRESS_PERSON_ID_IDX ON ADDRESS (PERSON_ID);

ALTER TABLE TICKET ADD CONSTRAINT FK001 FOREIGN KEY (PRIMARY_PERSON_ID) REFERENCES PERSON (ID);
ALTER TABLE TICKET ADD CONSTRAINT FK002 FOREIGN KEY (SECONDARY_PERSON_ID) REFERENCES PERSON (ID);
ALTER TABLE TICKET ADD CONSTRAINT FK003 FOREIGN KEY (PARENT_ID) REFERENCES TICKET (ID);

ALTER TABLE ADDRESS ADD CONSTRAINT FK004 FOREIGN KEY (PERSON_ID) REFERENCES PERSON (ID);