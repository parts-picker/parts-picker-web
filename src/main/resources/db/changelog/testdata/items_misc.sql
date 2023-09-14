-- Insert the "GPS Module for Arduino" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'GPS Module for Arduino', 'A GPS module designed for Arduino projects, allowing precise location tracking and navigation.');

-- Create individual items with concrete details and optional notes for the "GPS Module for Arduino" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working GPS module, used in a weather station project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock from a reputable supplier', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Recovered from a DIY quadcopter project, fully functional', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package, ready for your project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Sealed and untouched GPS module for your IoT application', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null);

-- Insert the "Breadboard" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Breadboard', 'A versatile solderless breadboard for prototyping electronic circuits and testing components.');

-- Create individual items with concrete details and optional notes for the "Breadboard" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new full-sized breadboard with 830 tie points', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used breadboard in excellent condition', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package containing a mini breadboard for compact projects', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new half-sized breadboard, great for workshops', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock breadboard with adhesive backing for easy mounting', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Well-maintained breadboard with labeled rows and columns', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and tested breadboard with built-in power rails', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package containing a breadboard kit with jumper wires', currval('item_type_seq'), null);

-- Insert the "Voltage Regulator, LM317" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Voltage Regulator, LM317', 'LM317 voltage regulator for precise voltage control.');

-- Create individual items for the "Voltage Regulator, LM317" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new LM317 voltage regulator.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened unit in excellent condition.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used, fully tested and functional.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and functional LM317 voltage regulator.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Brand new in original wrapping.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock for voltage control projects.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and functional LM317 voltage regulator.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null);

-- Insert the "Bluetooth Module HC-05" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Bluetooth Module HC-05', 'A HC-05 Bluetooth module for wireless communication in DIY electronics projects.');

-- Create individual items for the "Bluetooth Module HC-05" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new HC-05 Bluetooth module for wireless projects.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used HC-05 Bluetooth module for DIY electronics.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened HC-05 Bluetooth module in original packaging.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Bluetooth module for wireless communication in DIY projects.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned HC-05 Bluetooth module for DIY electronics enthusiasts.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package containing an HC-05 Bluetooth module for wireless projects.', currval('item_type_seq'), null);
