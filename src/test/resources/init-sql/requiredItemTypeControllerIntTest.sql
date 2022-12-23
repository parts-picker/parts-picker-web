INSERT INTO projects VALUES(1, 'PROJECT 1', 'Description for project 1');
INSERT INTO projects VALUES(2, 'PROJECT 2', 'Description for project 2');

INSERT INTO item_types VALUES(1, 'Small 8 Ohm Speaker', 'Small Speaker');
INSERT INTO item_types VALUES(2, 'A screw', 'A small screw');
INSERT INTO item_types VALUES(3, 'A nail', 'A small nail');

INSERT INTO required_item_types VALUES(1, 1, 2);
INSERT INTO required_item_types VALUES(1, 2, 4);
INSERT INTO required_item_types VALUES(1, 3, 6);