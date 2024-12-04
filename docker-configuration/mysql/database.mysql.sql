DROP TABLE IF EXISTS file;
CREATE TABLE file (
  file TEXT,
  metadata_schema VARCHAR(20),
  provider_id VARCHAR(150),
  provider_name VARCHAR(200),
  set_id VARCHAR(50),
  set_name VARCHAR(200),
  datum VARCHAR(20),
  size INTEGER
);
CREATE INDEX f_file_idx ON file (file(500));
CREATE INDEX f_schema_idx ON file (metadata_schema);
CREATE INDEX f_set_id_idx ON file (set_id);
CREATE INDEX f_provider_id_idx ON file (provider_id);

DROP TABLE IF EXISTS file_record;
CREATE TABLE file_record (
  file TEXT, 
  recordId VARCHAR(300)
);
CREATE INDEX fr_file_idx ON file_record (file(500));
CREATE INDEX fr_recordId_idx ON file_record (recordId);

DROP TABLE IF EXISTS issue;
CREATE TABLE issue (
  `metadata_schema` VARCHAR(20),
  `filename` VARCHAR(255),
  `recordId` VARCHAR(300),
  `providerid` VARCHAR(100),
  `Q-1.1:status` VARCHAR(10),
  `Q-1.1:score` INTEGER,
  `Q-1.2:status` VARCHAR(10),
  `Q-1.2:score` INTEGER,
  `Q-1.3:status` VARCHAR(10),
  `Q-1.3:score` INTEGER,
  `Q-1.4:status` VARCHAR(10),
  `Q-1.4:score` INTEGER,
  `Q-1.5:status` VARCHAR(10),
  `Q-1.5:score` INTEGER,
  `Q-2.1:status` VARCHAR(10),
  `Q-2.1:score` INTEGER,
  `Q-2.2:status` VARCHAR(10),
  `Q-2.2:score` INTEGER,
  `Q-2.3:status` VARCHAR(10),
  `Q-2.3:score` INTEGER,
  `Q-2.4:status` VARCHAR(10),
  `Q-2.4:score` INTEGER,
  `Q-2.5:status` VARCHAR(10),
  `Q-2.5:score` INTEGER,
  `Q-2.6:status` VARCHAR(10),
  `Q-2.6:score` INTEGER,
  `Q-3.1:status` VARCHAR(10),
  `Q-3.1:score` INTEGER,
  `Q-3.2:status` VARCHAR(10),
  `Q-3.2:score` INTEGER,
  `Q-3.3:status` VARCHAR(10),
  `Q-3.3:score` INTEGER,
  `Q-3.4:status` VARCHAR(10),
  `Q-3.4:score` INTEGER,
  `Q-3.5:status` VARCHAR(10),
  `Q-3.5:score` INTEGER,
  `Q-3.6:status` VARCHAR(10),
  `Q-3.6:score` INTEGER,
  `Q-4.1:status` VARCHAR(10),
  `Q-4.1:score` INTEGER,
  `Q-4.2:status` VARCHAR(10),
  `Q-4.2:score` INTEGER,
  `Q-4.3:status` VARCHAR(10),
  `Q-4.3:score` INTEGER,
  `Q-4.4:status` VARCHAR(10),
  `Q-4.4:score` INTEGER,
  `Q-4.5:status` VARCHAR(10),
  `Q-4.5:score` INTEGER,
  `Q-4.6:status` VARCHAR(10),
  `Q-4.6:score` INTEGER,
  `Q-5.1:status` VARCHAR(10),
  `Q-5.1:score` INTEGER,
  `Q-5.2:status` VARCHAR(10),
  `Q-5.2:score` INTEGER,
  `Q-5.3:status` VARCHAR(10),
  `Q-5.3:score` INTEGER,
  `Q-5.4:status` VARCHAR(10),
  `Q-5.4:score` INTEGER,
  `Q-5.5:status` VARCHAR(10),
  `Q-5.5:score` INTEGER,
  `Q-5.6:status` VARCHAR(10),
  `Q-5.6:score` INTEGER,
  `Q-5.7:status` VARCHAR(10),
  `Q-5.7:score` INTEGER,
  `Q-6.1:status` VARCHAR(10),
  `Q-6.1:score` INTEGER,
  `Q-6.2:status` VARCHAR(10),
  `Q-6.2:score` INTEGER,
  `Q-6.3:status` VARCHAR(10),
  `Q-6.3:score` INTEGER,
  `Q-6.4:status` VARCHAR(10),
  `Q-6.4:score` INTEGER,
  `Q-6.5:status` VARCHAR(10),
  `Q-6.5:score` INTEGER,
  `Q-7.1:status` VARCHAR(10),
  `Q-7.1:score` INTEGER,
  `Q-7.2:status` VARCHAR(10),
  `Q-7.2:score` INTEGER,
  `Q-7.3:status` VARCHAR(10),
  `Q-7.3:score` INTEGER,
  `Q-7.4:status` VARCHAR(10),
  `Q-7.4:score` INTEGER,
  `Q-7.5:status` VARCHAR(10),
  `Q-7.5:score` INTEGER,
  `Q-7.6:status` VARCHAR(10),
  `Q-7.6:score` INTEGER,
  `Q-7.7:status` VARCHAR(10),
  `Q-7.7:score` INTEGER,
  `Q-7.8:status` VARCHAR(10),
  `Q-7.8:score` INTEGER,
  `Q-8.1:status` VARCHAR(10),
  `Q-8.1:score` INTEGER,
  `Q-8.2:status` VARCHAR(10),
  `Q-8.2:score` INTEGER,
  `Q-8.3:status` VARCHAR(10),
  `Q-8.3:score` INTEGER,
  `Q-8.4:status` VARCHAR(10),
  `Q-8.4:score` INTEGER,
  `Q-9.1:status` VARCHAR(10),
  `Q-9.1:score` INTEGER,
  `Q-9.2:status` VARCHAR(10),
  `Q-9.2:score` INTEGER,
  `Q-9.3:status` VARCHAR(10),
  `Q-9.3:score` INTEGER,
  `Q-9.4:status` VARCHAR(10),
  `Q-9.4:score` INTEGER,
  `Q-9.5:status` VARCHAR(10),
  `Q-9.5:score` INTEGER,
  `Q-9.6:status` VARCHAR(10),
  `Q-9.6:score` INTEGER,
  `Q-9.7:status` VARCHAR(10),
  `Q-9.7:score` INTEGER,
  `Q-9.8:status` VARCHAR(10),
  `Q-9.8:score` INTEGER,
  `Q-9.9:status` VARCHAR(10),
  `Q-9.9:score` INTEGER,
  `Q-9.10:status` VARCHAR(10),
  `Q-9.10:score` INTEGER,
  `ruleCatalog:score` INTEGER
);

CREATE INDEX `i_recordId` ON issue (`recordId`);
CREATE INDEX `i_providerid` ON issue (`providerid`);
-- CREATE INDEX `Q-1.1:status` ON issue (`Q-1.1:status`);
CREATE INDEX `Q-1.1:score` ON issue (`Q-1.1:score`);
-- CREATE INDEX `Q-1.2:status` ON issue (`Q-1.2:status`);
CREATE INDEX `Q-1.2:score` ON issue (`Q-1.2:score`);
-- CREATE INDEX `Q-1.3:status` ON issue (`Q-1.3:status`);
CREATE INDEX `Q-1.3:score` ON issue (`Q-1.3:score`);
-- CREATE INDEX `Q-1.4:status` ON issue (`Q-1.4:status`);
CREATE INDEX `Q-1.4:score` ON issue (`Q-1.4:score`);
-- CREATE INDEX `Q-1.5:status` ON issue (`Q-1.5:status`);
CREATE INDEX `Q-1.5:score` ON issue (`Q-1.5:score`);
-- CREATE INDEX `Q-2.1:status` ON issue (`Q-2.1:status`);
CREATE INDEX `Q-2.1:score` ON issue (`Q-2.1:score`);
-- CREATE INDEX `Q-2.2:status` ON issue (`Q-2.2:status`);
CREATE INDEX `Q-2.2:score` ON issue (`Q-2.2:score`);
-- CREATE INDEX `Q-2.3:status` ON issue (`Q-2.3:status`);
CREATE INDEX `Q-2.3:score` ON issue (`Q-2.3:score`);
-- CREATE INDEX `Q-2.4:status` ON issue (`Q-2.4:status`);
CREATE INDEX `Q-2.4:score` ON issue (`Q-2.4:score`);
-- CREATE INDEX `Q-2.5:status` ON issue (`Q-2.5:status`);
CREATE INDEX `Q-2.5:score` ON issue (`Q-2.5:score`);
-- CREATE INDEX `Q-2.6:status` ON issue (`Q-2.6:status`);
CREATE INDEX `Q-2.6:score` ON issue (`Q-2.6:score`);
-- CREATE INDEX `Q-3.1:status` ON issue (`Q-3.1:status`);
CREATE INDEX `Q-3.1:score` ON issue (`Q-3.1:score`);
-- CREATE INDEX `Q-3.2:status` ON issue (`Q-3.2:status`);
CREATE INDEX `Q-3.2:score` ON issue (`Q-3.2:score`);
-- CREATE INDEX `Q-3.3:status` ON issue (`Q-3.3:status`);
CREATE INDEX `Q-3.3:score` ON issue (`Q-3.3:score`);
-- CREATE INDEX `Q-3.4:status` ON issue (`Q-3.4:status`);
CREATE INDEX `Q-3.4:score` ON issue (`Q-3.4:score`);
-- CREATE INDEX `Q-3.5:status` ON issue (`Q-3.5:status`);
CREATE INDEX `Q-3.5:score` ON issue (`Q-3.5:score`);
-- CREATE INDEX `Q-3.6:status` ON issue (`Q-3.6:status`);
CREATE INDEX `Q-3.6:score` ON issue (`Q-3.6:score`);
-- CREATE INDEX `Q-4.1:status` ON issue (`Q-4.1:status`);
CREATE INDEX `Q-4.1:score` ON issue (`Q-4.1:score`);
-- CREATE INDEX `Q-4.2:status` ON issue (`Q-4.2:status`);
CREATE INDEX `Q-4.2:score` ON issue (`Q-4.2:score`);
-- CREATE INDEX `Q-4.3:status` ON issue (`Q-4.3:status`);
CREATE INDEX `Q-4.3:score` ON issue (`Q-4.3:score`);
-- CREATE INDEX `Q-4.4:status` ON issue (`Q-4.4:status`);
CREATE INDEX `Q-4.4:score` ON issue (`Q-4.4:score`);
-- CREATE INDEX `Q-4.5:status` ON issue (`Q-4.5:status`);
CREATE INDEX `Q-4.5:score` ON issue (`Q-4.5:score`);
-- CREATE INDEX `Q-4.6:status` ON issue (`Q-4.6:status`);
CREATE INDEX `Q-4.6:score` ON issue (`Q-4.6:score`);
-- CREATE INDEX `Q-5.1:status` ON issue (`Q-5.1:status`);
CREATE INDEX `Q-5.1:score` ON issue (`Q-5.1:score`);
-- CREATE INDEX `Q-5.2:status` ON issue (`Q-5.2:status`);
CREATE INDEX `Q-5.2:score` ON issue (`Q-5.2:score`);
-- CREATE INDEX `Q-5.3:status` ON issue (`Q-5.3:status`);
CREATE INDEX `Q-5.3:score` ON issue (`Q-5.3:score`);
-- CREATE INDEX `Q-5.4:status` ON issue (`Q-5.4:status`);
CREATE INDEX `Q-5.4:score` ON issue (`Q-5.4:score`);
-- CREATE INDEX `Q-5.5:status` ON issue (`Q-5.5:status`);
CREATE INDEX `Q-5.5:score` ON issue (`Q-5.5:score`);
-- CREATE INDEX `Q-5.6:status` ON issue (`Q-5.6:status`);
CREATE INDEX `Q-5.6:score` ON issue (`Q-5.6:score`);
-- CREATE INDEX `Q-5.7:status` ON issue (`Q-5.7:status`);
CREATE INDEX `Q-5.7:score` ON issue (`Q-5.7:score`);
-- CREATE INDEX `Q-6.1:status` ON issue (`Q-6.1:status`);
CREATE INDEX `Q-6.1:score` ON issue (`Q-6.1:score`);
-- CREATE INDEX `Q-6.2:status` ON issue (`Q-6.2:status`);
CREATE INDEX `Q-6.2:score` ON issue (`Q-6.2:score`);
-- CREATE INDEX `Q-6.3:status` ON issue (`Q-6.3:status`);
CREATE INDEX `Q-6.3:score` ON issue (`Q-6.3:score`);
-- CREATE INDEX `Q-6.4:status` ON issue (`Q-6.4:status`);
CREATE INDEX `Q-6.4:score` ON issue (`Q-6.4:score`);
-- CREATE INDEX `Q-6.5:status` ON issue (`Q-6.5:status`);
CREATE INDEX `Q-6.5:score` ON issue (`Q-6.5:score`);
-- CREATE INDEX `Q-7.1:status` ON issue (`Q-7.1:status`);
CREATE INDEX `Q-7.1:score` ON issue (`Q-7.1:score`);
-- CREATE INDEX `Q-7.2:status` ON issue (`Q-7.2:status`);
CREATE INDEX `Q-7.2:score` ON issue (`Q-7.2:score`);
-- CREATE INDEX `Q-7.3:status` ON issue (`Q-7.3:status`);
CREATE INDEX `Q-7.3:score` ON issue (`Q-7.3:score`);
-- CREATE INDEX `Q-7.4:status` ON issue (`Q-7.4:status`);
CREATE INDEX `Q-7.4:score` ON issue (`Q-7.4:score`);
-- CREATE INDEX `Q-7.5:status` ON issue (`Q-7.5:status`);
CREATE INDEX `Q-7.5:score` ON issue (`Q-7.5:score`);
-- CREATE INDEX `Q-7.6:status` ON issue (`Q-7.6:status`);
CREATE INDEX `Q-7.6:score` ON issue (`Q-7.6:score`);
-- CREATE INDEX `Q-7.7:status` ON issue (`Q-7.7:status`);
CREATE INDEX `Q-7.7:score` ON issue (`Q-7.7:score`);
-- CREATE INDEX `Q-7.8:status` ON issue (`Q-7.8:status`);
CREATE INDEX `Q-7.8:score` ON issue (`Q-7.8:score`);
CREATE INDEX `ruleCatalog:score` ON issue (`ruleCatalog:score`);

DROP TABLE IF EXISTS variability;
CREATE TABLE variability (
  field VARCHAR(20),
  number_of_values INTEGER,
  metadata_schema VARCHAR(20),
  set_id VARCHAR(50),
  provider_id VARCHAR(150)
);

DROP TABLE IF EXISTS frequency;
CREATE TABLE frequency (
  field VARCHAR(20),
  value VARCHAR(255),
  frequency INTEGER,
  metadata_schema VARCHAR(20),
  set_id VARCHAR(50),
  provider_id VARCHAR(150)
);

DROP TABLE IF EXISTS count;
CREATE TABLE count (
  metadata_schema VARCHAR(20),
  set_id VARCHAR(50),
  provider_id VARCHAR(150),
  count INTEGER
);
