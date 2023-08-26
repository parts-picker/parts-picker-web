INSERT INTO "groups" VALUES(1, 'GROUP 1', 'Description for group 1');

INSERT INTO workflows VALUES(nextval('workflow_id_seq'), 'Testflows', 1, now());
INSERT INTO workflow_nodes VALUES(nextval('node_id_seq'), currval('workflow_id_seq'), 'start', 'start', 'Start', 'WORKFLOW');

INSERT INTO workflow_instances VALUES(1, currval('workflow_id_seq'), currval('node_id_seq'), true, null, 'DEFAULT');
INSERT INTO workflow_instances VALUES(2, currval('workflow_id_seq'), currval('node_id_seq'), true, null, 'DEFAULT');
INSERT INTO workflow_instances VALUES(3, currval('workflow_id_seq'), currval('node_id_seq'), true, null, 'DEFAULT');
INSERT INTO workflow_instances VALUES(4, currval('workflow_id_seq'), currval('node_id_seq'), true, null, 'DEFAULT');
ALTER SEQUENCE instance_id_seq RESTART WITH 10;

INSERT INTO projects VALUES(1, 'PROJECT 1', 'Description for project 1', 1, 1);
INSERT INTO projects VALUES(2, 'PROJECT 2', 'Description for project 2', 1, 2);
INSERT INTO projects VALUES(3, 'PROJECT 3', 'Description for project 3', null, 3);
INSERT INTO projects VALUES(4, 'PROJECT 4', 'Description for project 4: to be deleted', 1, 4);

INSERT INTO "groups" VALUES(2, 'GROUP 2', 'Description for group 2');
