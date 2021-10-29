INSERT INTO groups values (1, null, 'First group')
INSERT INTO groups values (2, null, 'Second group')
INSERT INTO groups values (3, null, 'Third group')

INSERT INTO projects VALUES(1, null, 'Awesome first project', 2)
INSERT INTO projects VALUES(2, null, 'Awesome second project', 3)
INSERT INTO projects VALUES(3, null, 'Third project', null)
INSERT INTO projects VALUES(4, null, 'Fourth project', 2)

-- Hibernate does not increase Ids for created entries. Thereby an offset is set that import data does not conflict with created data on runtime
ALTER SEQUENCE hibernate_sequence RESTART WITH 100;
