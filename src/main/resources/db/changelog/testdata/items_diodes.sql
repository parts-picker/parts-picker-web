-- Insert the "Diode, Zener, 1N4733A" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Diode, Zener, 1N4733A', 'A 1N4733A Zener diode for voltage regulation.');

-- Create individual items for the "Diode, Zener, 1N4733A" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working fine', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new in unopened packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Recovered from an old power supply', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pulled from an old circuit board', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Freshly manufactured', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Working condition', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'High-quality diode', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Salvaged from a surplus electronics lot', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened and ready for use', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Recovered from a retired PCB', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Reliable voltage regulation diode', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and tested', currval('item_type_seq'), null);

-- Insert the "Diode, 1N4148" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Diode, 1N4148', 'A small signal diode commonly used in electronics.');

-- Create individual items for the "Diode, 1N4148" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Removed from a PCB board', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Brand new in original packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Salvaged from an electronics project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and tested', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null);

-- Insert the "Diode, 1N4001" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Diode, 1N4001', 'A general-purpose diode with a voltage rating of 50V.');

-- Create individual items for the "Diode, 1N4001" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Recovered from an old power supply', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Brand new in original packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Salvaged from an electronics project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and tested', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null);

-- Insert the "Schottky Diode, 1N5819" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Schottky Diode, 1N5819', 'A Schottky diode with a voltage rating of 40V.');

-- Create individual items for the "Schottky Diode, 1N5819" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Recovered from a damaged power supply', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Brand new in original packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Salvaged from an electronics project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and tested', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null);

-- Insert the "Diode, Fast Recovery, UF4007" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Diode, Fast Recovery, UF4007', 'A UF4007 fast recovery diode for high-speed rectification.');

-- Create individual items for the "Diode, Fast Recovery, UF4007" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Recovered from a voltage regulator circuit', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Brand new in original packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Salvaged from an electronics project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and tested', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null);
