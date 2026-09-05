INSERT INTO item_types (id, name, description, org_unit_id, created_by, created_on) VALUES(1, 'Small Speaker', 'Small 8 Ohm Speaker', 1000, 1000, now());
INSERT INTO items (id, status, condition, note, type_id, org_unit_id, created_by, created_on) VALUES(4, 'IN_STOCK', 'USED', 'Salvaged Speaker', 1, 1000, 1000, now());
INSERT INTO items (id, status, condition, note, type_id, org_unit_id, created_by, created_on) VALUES(5, 'IN_STOCK', 'USED', 'Salvaged Speaker', 1, 1000, 1000, now());
INSERT INTO items (id, status, condition, note, type_id, org_unit_id, created_by, created_on) VALUES(6, 'IN_STOCK', 'USED', 'Salvaged Speaker', 1, 1000, 1000, now());


INSERT INTO item_types (id, name, description, org_unit_id, created_by, created_on) VALUES(2, 'ITEMTYPE WITHOUT ITEMS', 'THIS TYPE SHOULD NOT HAVE ITEMS', 1000, 1000, now());


INSERT INTO item_types (id, name, description, org_unit_id, created_by, created_on) VALUES(3, 'ITEMTYPE WITH TWO ITEMS', 'THIS TYPE SHOULD HAVE TWO ITEMS', 1000, 1000, now());
INSERT INTO items (id, status, condition, note, type_id, org_unit_id, created_by, created_on) VALUES(7, 'IN_STOCK', 'USED', 'ITEM ONE', 3, 1000, 1000, now());
INSERT INTO items (id, status, condition, note, type_id, org_unit_id, created_by, created_on) VALUES(8, 'IN_STOCK', 'USED', 'ITEM TWO', 3, 1000, 1000, now());


INSERT INTO item_types (id, name, description, org_unit_id, created_by, created_on) VALUES(4, 'ITEMTYPE TO USE TO CREATE NEW ITEMS', 'THIS TYPE SHOULD HAVE NO ITEMS', 1000, 1000, now());


INSERT INTO item_types (id, name, description, org_unit_id, created_by, created_on) VALUES(5, 'ITEMTYPE TO USE TO DELETE ITEMS', 'THIS TYPE SHOULD HAVE ONE ITEMS', 1000, 1000, now());
INSERT INTO items (id, status, condition, note, type_id, org_unit_id, created_by, created_on) VALUES(9, 'IN_STOCK', 'USED', 'ITEM ONE', 5, 1000, 1000, now());

INSERT INTO workflows VALUES(nextval('workflow_id_seq'), 'Testflows', 1, now());
INSERT INTO workflow_nodes VALUES(nextval('node_id_seq'), currval('workflow_id_seq'), 'start', 'start', 'Start', 'WORKFLOW');
INSERT INTO workflow_instances VALUES(1, currval('node_id_seq'), true, null, 'DEFAULT');

INSERT INTO projects (id, name, short_description, group_id, instance_id, org_unit_id, created_by, created_on) VALUES(1, 'PROJECT 1', 'Description for project 1: used for PUT projectId', null, 1, 1000, 1000, now());

