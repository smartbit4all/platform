--


--   |SAMPLEEMPLOYEE| <------- |SAMPLEDEPARTMENT| <------- |SAMPLECOMPANY|
--
create table if not exists SAMPLEEMPLOYEEDB (
  uri                     VARCHAR2(500) NOT NULL primary key,
  name                    VARCHAR2(500) NULL,
  id                      VARCHAR2(500) NULL,
  department              VARCHAR2(500) NULL
);
create table if not exists SAMPLEDEPARTMENTDB (
  uri                     VARCHAR2(500) NOT NULL primary key,
  name                    VARCHAR2(500) NULL,
  id                      VARCHAR2(500) NULL,
  company                 VARCHAR2(500) NULL
);
create table if not exists SAMPLECOMPANYDB (
  uri                     VARCHAR2(500) NOT NULL primary key,
  name                    VARCHAR2(500) NULL,
  id                      VARCHAR2(500) NULL
);
