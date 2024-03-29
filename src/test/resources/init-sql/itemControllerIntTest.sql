INSERT INTO item_types VALUES(1, 'Small Speaker', 'Small 8 Ohm Speaker');
INSERT INTO items VALUES(4, 'IN_STOCK', 'USED', 'Salvaged Speaker', 1);
INSERT INTO items VALUES(5, 'IN_STOCK', 'USED', 'Salvaged Speaker', 1);
INSERT INTO items VALUES(6, 'IN_STOCK', 'USED', 'Salvaged Speaker', 1);


INSERT INTO item_types VALUES(2, 'ITEMTYPE WITHOUT ITEMS', 'THIS TYPE SHOULD NOT HAVE ITEMS');


INSERT INTO item_types VALUES(3, 'ITEMTYPE WITH TWO ITEMS', 'THIS TYPE SHOULD HAVE TWO ITEMS');
INSERT INTO items VALUES(7, 'IN_STOCK', 'USED', 'ITEM ONE', 3);
INSERT INTO items VALUES(8, 'IN_STOCK', 'USED', 'ITEM TWO', 3);


INSERT INTO item_types VALUES(4, 'ITEMTYPE TO USE TO CREATE NEW ITEMS', 'THIS TYPE SHOULD HAVE NO ITEMS');


INSERT INTO item_types VALUES(5, 'ITEMTYPE TO USE TO DELETE ITEMS', 'THIS TYPE SHOULD HAVE ONE ITEMS');
INSERT INTO items VALUES(9, 'IN_STOCK', 'USED', 'ITEM ONE', 5);

INSERT INTO workflows VALUES(nextval('workflow_id_seq'), 'Testflows', 1, now());
INSERT INTO workflow_nodes VALUES(nextval('node_id_seq'), currval('workflow_id_seq'), 'start', 'start', 'Start', 'WORKFLOW');
INSERT INTO workflow_instances VALUES(1, currval('node_id_seq'), true, null, 'DEFAULT');

INSERT INTO projects VALUES(1, 'PROJECT 1', 'Description for project 1: used for PUT projectId', null, 1);

