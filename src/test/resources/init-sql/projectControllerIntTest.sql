INSERT INTO "groups" (id, name, description, org_unit_id, created_by, created_on) VALUES(1, 'GROUP 1', 'Description for group 1', 1000, 1000, now());

INSERT INTO workflows VALUES(nextval('workflow_id_seq'), 'Testflows', 1, now());
INSERT INTO workflow_nodes VALUES(nextval('node_id_seq'), currval('workflow_id_seq'), 'start', 'start', 'Start', 'WORKFLOW');

INSERT INTO workflow_instances VALUES(1, currval('node_id_seq'), true, null, 'DEFAULT');
INSERT INTO workflow_instances VALUES(2, currval('node_id_seq'), true, null, 'DEFAULT');
INSERT INTO workflow_instances VALUES(3, currval('node_id_seq'), true, null, 'DEFAULT');
INSERT INTO workflow_instances VALUES(4, currval('node_id_seq'), true, null, 'DEFAULT');
ALTER SEQUENCE instance_id_seq RESTART WITH 10;

INSERT INTO projects (id, name, short_description, group_id, instance_id, org_unit_id, created_by, created_on) VALUES(1, 'PROJECT 1', 'Description for project 1', 1, 1, 1000, 1000, now());
INSERT INTO projects (id, name, short_description, group_id, instance_id, org_unit_id, created_by, created_on) VALUES(2, 'PROJECT 2', 'Description for project 2', 1, 2, 1000, 1000, now());
INSERT INTO projects (id, name, short_description, group_id, instance_id, org_unit_id, created_by, created_on) VALUES(3, 'PROJECT 3', 'Description for project 3', null, 3, 1000, 1000, now());
INSERT INTO projects (id, name, short_description, group_id, instance_id, org_unit_id, created_by, created_on) VALUES(4, 'PROJECT 4', 'Description for project 4: to be deleted', 1, 4, 1000, 1000, now());
ALTER SEQUENCE project_seq RESTART WITH 10;

INSERT INTO "groups" (id, name, description, org_unit_id, created_by, created_on) VALUES(2, 'GROUP 2', 'Description for group 2', 1000, 1000, now());
