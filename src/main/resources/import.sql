INSERT INTO groups values (1, null, 'First group')
INSERT INTO groups values (2, null, 'Second group')
INSERT INTO groups values (3, null, 'Third group')

INSERT INTO projects VALUES(1, null, 'Awesome first project', 2)
INSERT INTO projects VALUES(2, null, 'Awesome second project', 3)
INSERT INTO projects VALUES(3, null, 'Third project', null)
INSERT INTO projects VALUES(4, null, 'Fourth project', 2)

INSERT INTO item_types VALUES(1, 'Small 8 Ohm Speaker', 'Small Speaker')
INSERT INTO item_types VALUES(2, 'A small motor', 'Small Motor')
INSERT INTO item_types VALUES(3, 'A 1/4 W Resistor with 220 Ohm', 'Resistor 220 Ohm')

INSERT INTO items VALUES(1, 'NEW', 'Some notes about this item', 'NEEDED', 3)
INSERT INTO items VALUES(2, 'NEW', 'Some notes about this item', 'NEEDED', 3)
INSERT INTO items VALUES(3, 'NEW', 'Some notes about this item', 'NEEDED', 3)
INSERT INTO items VALUES(4, 'NEW', 'Some notes about this item', 'NEEDED', 3)

INSERT INTO items VALUES(5, 'USED', 'Salvaged Speaker', 'IN_STOCK', 1)
INSERT INTO items VALUES(6, 'USED', 'Salvaged Speaker', 'IN_STOCK', 1)
INSERT INTO items VALUES(7, 'USED', 'Salvaged Speaker', 'IN_STOCK', 1)

-- Hibernate does not increase Ids for created entries. Thereby an offset is set that import data does not conflict with created data on runtime
ALTER SEQUENCE hibernate_sequence RESTART WITH 100;
