--
--         parent
--        ------
--   -----ˇ--  |     primary --------         ---------
--   |TICKET|--|        ---->|Person|         |Address|
--   |      |-----------|    |      | <-------|       |
--   -------- *         ---->|      |        *|       |
--                 secondary --------         ---------
--
CREATE SCHEMA test1;
CREATE SCHEMA test2;

CREATE SEQUENCE SQL_TEST_EXISTS_SEQ START WITH 1000 INCREMENT BY 1;

CREATE TABLE test1.TICKET (
  "ID" NUMBER(19) NOT NULL,
  "TITLE" VARCHAR2(255) NULL,
  "PRIMARY_PERSON_ID" NUMBER(19) NULL,
  "SECONDARY_PERSON_ID" NUMBER(19) NULL,
  "PARENT_ID" NUMBER(19) NULL
);
ALTER TABLE test1.TICKET ADD CONSTRAINT SYS_001 PRIMARY KEY (ID);

CREATE TABLE test1.PERSON (
  "ID" NUMBER(19) NOT NULL, 
  "NAME"  VARCHAR2(255) NULL
);
ALTER TABLE test1.PERSON ADD CONSTRAINT SYS_002 PRIMARY KEY (ID);

CREATE TABLE test2.ADDRESS (
  "ID" NUMBER(19) NOT NULL,
  "ZIP" VARCHAR2(255) NULL,
  "CITY" VARCHAR2(255) NULL,
  "PERSON_ID" NUMBER(19) NULL
);
ALTER TABLE test2.ADDRESS ADD CONSTRAINT SYS_003 PRIMARY KEY (ID);


CREATE INDEX TICKET_PRIMARY_PERSON_ID_IDX ON test1.TICKET (PRIMARY_PERSON_ID);
CREATE INDEX TICKET_SECONDARY_PERSON_ID_IDX ON test1.TICKET (SECONDARY_PERSON_ID);
CREATE INDEX TICKET_PARENT_ID_IDX ON test1.TICKET (PARENT_ID);

CREATE INDEX ADDRESS_PERSON_ID_IDX ON test2.ADDRESS (PERSON_ID);

ALTER TABLE test1.TICKET ADD CONSTRAINT FK001 FOREIGN KEY (PRIMARY_PERSON_ID) REFERENCES PERSON (ID);
ALTER TABLE test1.TICKET ADD CONSTRAINT FK002 FOREIGN KEY (SECONDARY_PERSON_ID) REFERENCES PERSON (ID);
ALTER TABLE test1.TICKET ADD CONSTRAINT FK003 FOREIGN KEY (PARENT_ID) REFERENCES TICKET (ID);