INSERT INTO "groups" (id, name, description, org_unit_id, created_by, created_on) VALUES(1, 'GROUP 1', 'Description for group 1', 1000, 1000, now());
INSERT INTO "groups" (id, name, description, org_unit_id, created_by, created_on) VALUES(2, 'GROUP 2', 'Description for group 2', 1000, 1000, now());
INSERT INTO "groups" (id, name, description, org_unit_id, created_by, created_on) VALUES(3, 'GROUP 3', 'Description for group 3: to be deleted', 1000, 1000, now());
ALTER SEQUENCE group_seq RESTART WITH 10;
