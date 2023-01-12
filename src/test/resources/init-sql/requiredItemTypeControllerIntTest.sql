INSERT INTO workflows VALUES(nextval('workflow_id_seq'), 'Testflows', 1, now());
INSERT INTO workflow_nodes VALUES(nextval('node_id_seq'), currval('workflow_id_seq'), 'start', 'start', 'Start', 'WORKFLOW');
INSERT INTO workflow_instances VALUES(1, currval('workflow_id_seq'), currval('node_id_seq'), true);
INSERT INTO workflow_instances VALUES(2, currval('workflow_id_seq'), currval('node_id_seq'), true);

INSERT INTO projects VALUES(1, 'PROJECT 1', 'Description for project 1', null, 1);
INSERT INTO projects VALUES(2, 'PROJECT 2', 'Description for project 2', null, 2);

INSERT INTO item_types VALUES(1, 'Small 8 Ohm Speaker', 'Small Speaker');
INSERT INTO item_types VALUES(2, 'A screw', 'A small screw');
INSERT INTO item_types VALUES(3, 'A nail', 'A small nail');

INSERT INTO required_item_types VALUES(1, 1, 2);
INSERT INTO required_item_types VALUES(1, 2, 4);
INSERT INTO required_item_types VALUES(1, 3, 6);
