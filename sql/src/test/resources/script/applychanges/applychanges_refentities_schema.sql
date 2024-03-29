--
--    A -> B -> C -> F
--	  ^    ^    ^
--	   \G-/     \--- D -> E

CREATE TABLE A (
  "ID" NUMBER(19) NOT NULL, 
  "UID" VARCHAR2(255) NOT NULL,
  "AF1" VARCHAR2(255) NULL,
  "AF2" VARCHAR2(255) NULL,
  "B_ID" NUMBER(19) NULL
);

CREATE TABLE B (
  "ID" NUMBER(19) NOT NULL, 
  "UID" VARCHAR2(255) NOT NULL,
  "BF1" VARCHAR2(255) NULL,
  "BF2" VARCHAR2(255) NULL,
  "C_ID" NUMBER(19) NULL
);

CREATE TABLE C (
  "ID" NUMBER(19) NOT NULL, 
  "UID" VARCHAR2(255) NOT NULL,
  "CF1" VARCHAR2(255) NULL,
  "CF2" VARCHAR2(255) NULL,
  "CF3" VARCHAR2(255) NULL,
  "F_ID" NUMBER(19) NULL
);

CREATE TABLE D (
  "ID" NUMBER(19) NOT NULL, 
  "UID" VARCHAR2(255) NOT NULL,
  "DF1" VARCHAR2(255) NULL,
  "DF2" VARCHAR2(255) NULL,
  "E_ID" NUMBER(19) NULL,
  "C_ID" NUMBER(19) NULL
);

CREATE TABLE E (
  "ID" NUMBER(19) NOT NULL, 
  "UID" VARCHAR2(255) NOT NULL,
  "EF1" VARCHAR2(255) NULL,
  "EF2" VARCHAR2(255) NULL
);

CREATE TABLE F (
  "ID" NUMBER(19) NOT NULL, 
  "UID" VARCHAR2(255) NOT NULL,
  "FF1" VARCHAR2(255) NULL,
  "FF2" VARCHAR2(255) NULL
);

CREATE TABLE G (
  "ID" NUMBER(19) NOT NULL, 
  "UID" VARCHAR2(255) NOT NULL,
  "AF1" VARCHAR2(255) NULL,
  "BF1" VARCHAR2(255) NULL,
  "A_ID" NUMBER(19) NULL,
  "B_ID" NUMBER(19) NULL
);

-- ids
ALTER TABLE A ADD CONSTRAINT SYS_A PRIMARY KEY (ID);
ALTER TABLE B ADD CONSTRAINT SYS_B PRIMARY KEY (ID);
ALTER TABLE C ADD CONSTRAINT SYS_C PRIMARY KEY (ID);
ALTER TABLE D ADD CONSTRAINT SYS_D PRIMARY KEY (ID);
ALTER TABLE E ADD CONSTRAINT SYS_E PRIMARY KEY (ID);
ALTER TABLE F ADD CONSTRAINT SYS_F PRIMARY KEY (ID);
ALTER TABLE G ADD CONSTRAINT SYS_G PRIMARY KEY (ID);

-- fk indexes
CREATE INDEX A_B_ID_IDX ON A (B_ID);
CREATE INDEX B_C_ID_IDX ON B (C_ID);
CREATE INDEX C_F_ID_IDX ON C (F_ID);
CREATE INDEX D_C_ID_IDX ON D (C_ID);
CREATE INDEX D_E_ID_IDX ON D (E_ID);
CREATE INDEX G_A_ID_IDX ON G (A_ID);
CREATE INDEX G_B_ID_IDX ON G (B_ID);

-- fks
ALTER TABLE A ADD CONSTRAINT FK011 FOREIGN KEY (B_ID) REFERENCES B (ID);
ALTER TABLE B ADD CONSTRAINT FK012 FOREIGN KEY (C_ID) REFERENCES C (ID);
ALTER TABLE C ADD CONSTRAINT FK013 FOREIGN KEY (F_ID) REFERENCES F (ID);
ALTER TABLE D ADD CONSTRAINT FK014 FOREIGN KEY (C_ID) REFERENCES C (ID);
ALTER TABLE D ADD CONSTRAINT FK015 FOREIGN KEY (E_ID) REFERENCES E (ID);
ALTER TABLE G ADD CONSTRAINT FK016 FOREIGN KEY (A_ID) REFERENCES A (ID);
ALTER TABLE G ADD CONSTRAINT FK017 FOREIGN KEY (B_ID) REFERENCES B (ID);
