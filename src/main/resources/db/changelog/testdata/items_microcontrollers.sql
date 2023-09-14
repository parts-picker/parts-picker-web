-- Insert the "Microcontroller, Arduino Mega 2560" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Microcontroller, Arduino Mega 2560', 'An Arduino Mega 2560 microcontroller board.');

-- Create individual items for the "Microcontroller, Arduino Mega 2560" item type with null for assigned project ID
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Retired from a robotics project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Freshly unboxed', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Previously used in a home automation system', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Removed from a drone project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new and unused', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and functional', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Factory fresh', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Recovered from an experimental setup', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Unopened package', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Retired from a robotics competition', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Arduino Mega 2560', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and ready for use', currval('item_type_seq'), null);

-- Insert the "Microcontroller, Arduino Uno" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Microcontroller, Arduino Uno', 'An Arduino Uno microcontroller board.');

-- Create individual items for the "Microcontroller, Arduino Uno" item type with null for assigned project ID
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Retired from a robotics project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Previously used in a home automation system', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Removed from a drone project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and functional', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Factory fresh', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Recovered from an experimental setup', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Retired from a robotics competition', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and ready for use', currval('item_type_seq'), null);

-- Insert the "Microprocessor, Raspberry Pi 4 Model B" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Microprocessor, Raspberry Pi 4 Model B', 'A Raspberry Pi 4 Model B microprocessor board.');

-- Create individual items for the "Microprocessor, Raspberry Pi 4 Model B" item type with null for assigned project ID
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Retired from a computer cluster project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Previously used as a home server', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Removed from an IoT project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and functional', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Recovered from an educational setup', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Retired from a media center project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and ready for use', currval('item_type_seq'), null);

-- Insert the "Microcontroller, Arduino Nano" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Microcontroller, Arduino Nano', 'An Arduino Nano microcontroller board for compact designs.');

-- Create individual items for the "Microcontroller, Arduino Nano" item type with null for assigned project ID
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Retired from a robotics project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Previously used in a home automation system', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Removed from a drone project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and functional', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Recovered from an experimental setup', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Retired from a robotics competition', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and ready for use', currval('item_type_seq'), null);

-- Insert the "Microcontroller, ESP8266 NodeMCU" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Microcontroller, ESP8266 NodeMCU', 'An ESP8266 NodeMCU microcontroller board for IoT projects.');

-- Create individual items for the "Microcontroller, ESP8266 NodeMCU" item type with null for assigned project ID
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Previously used in a smart home project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and functional', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Recovered from an IoT sensor project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Retired from a weather monitoring station', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and ready for use', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Retired from a smart agriculture project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and ready for programming', currval('item_type_seq'), null);

-- Insert the "Microcontroller, ESP32" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Microcontroller, ESP32', 'An ESP32 microcontroller board for IoT and wireless projects.');

-- Create individual items for the "Microcontroller, ESP32" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new ESP32 microcontroller board for IoT projects.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used ESP32 microcontroller for wireless projects.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened ESP32 microcontroller board in original packaging.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'ESP32 microcontroller for IoT and wireless applications.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned ESP32 microcontroller with wireless capabilities.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package with an ESP32 microcontroller board for IoT projects.', currval('item_type_seq'), null);
