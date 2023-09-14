-- Insert the "Sensor, Temperature and Humidity, DHT22" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Sensor, Temperature and Humidity, DHT22', 'A DHT22 sensor for measuring temperature and humidity.');

-- Create individual items with variations for the "Sensor, Temperature and Humidity, DHT22" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Removed from a climate monitoring system for temperature and humidity', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new DHT22 sensor in original packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working DHT22 temperature and humidity sensor', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Salvaged from a research project for environmental monitoring', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock of DHT22 temperature and humidity sensors', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and tested DHT22 sensor for accurate readings', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Unopened package containing a DHT22 temperature and humidity sensor', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null);

-- Insert the "Motion Sensor, PIR HC-SR501" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Motion Sensor, PIR HC-SR501', 'A PIR (Passive Infrared) motion sensor (HC-SR501) for detecting movement.');

-- Create individual items with variations for the "Motion Sensor, PIR HC-SR501" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Retired from a home security system', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new HC-SR501 PIR motion sensor in original packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working HC-SR501 motion sensor for motion detection', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Salvaged from an automation project for motion sensing', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock HC-SR501 PIR motion sensor', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and tested PIR motion sensor (HC-SR501)', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Unopened package containing an HC-SR501 PIR motion sensor', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null);

-- Insert the "Light Sensor, LDR-Photoresistor" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Light Sensor, LDR-Photoresistor', 'An LDR (Light Dependent Resistor) or photoresistor for measuring ambient light levels.');

-- Create individual items with variations for the "Light Sensor, LDR-Photoresistor" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Recovered LDR-Photoresistor from an old light control system', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new LDR-Photoresistor in original packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working LDR-Photoresistor for light sensing applications', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Salvaged LDR-Photoresistor from a light control project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock LDR-Photoresistor', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and tested LDR-Photoresistor for light-level detection', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Unopened package containing an LDR-Photoresistor for light sensing', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null);

-- Insert the "Sensor, Gas, MQ-2" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Sensor, Gas, MQ-2', 'An MQ-2 gas sensor for detecting various gases.');

-- Create individual items with variations for the "Sensor, Gas, MQ-2" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new MQ-2 gas sensor for accurate gas detection in various applications.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used MQ-2 gas sensor for monitoring gas levels in confined spaces.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Recovered from an old gas monitoring system but still reliable.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened MQ-2 gas sensor in its original packaging.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'MQ-2 gas sensor for detecting a range of gases in industrial environments.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned MQ-2 gas sensor with proven gas detection capabilities.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Previously used for gas leak detection but still functional.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package containing an MQ-2 gas sensor for safety applications.', currval('item_type_seq'), null);


-- Insert the "Ultrasonic Sensor, HC-SR04" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Ultrasonic Sensor, HC-SR04', 'HC-SR04 ultrasonic sensors for distance measurement and obstacle detection.');

-- Create individual items for the "Ultrasonic Sensor, HC-SR04" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new HC-SR04 ultrasonic sensor module.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used HC-SR04 ultrasonic sensor with accurate distance measurements.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened HC-SR04 ultrasonic sensor module in original packaging.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Brand new HC-SR04 ultrasonic sensor in its original wrapping.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned HC-SR04 ultrasonic sensor with minimal wear.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null);

-- Insert the "Sound Sensor Module, KY-038" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Sound Sensor Module, KY-038', 'A KY-038 sound sensor module for sound detection and audio-related projects.');

-- Create individual items for the "Sound Sensor Module, KY-038" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new KY-038 sound sensor module for sound detection.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used KY-038 sound sensor module for audio projects.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened KY-038 sound sensor module in original packaging.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Sound sensor module for detecting audio signals.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned KY-038 sound sensor module with sound detection capabilities.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package containing a KY-038 sound sensor module for audio projects.', currval('item_type_seq'), null);

-- Insert the "Sensor, Pressure, BMP180" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Sensor, Pressure, BMP180', 'A BMP180 pressure sensor for atmospheric pressure measurement.');

-- Create individual items with variations for the "Sensor, Pressure, BMP180" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new BMP180 pressure sensor for precise atmospheric pressure measurement.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used BMP180 pressure sensor for weather monitoring applications.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Removed from a climate data logger but still fully functional.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened BMP180 pressure sensor in its original packaging.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'BMP180 pressure sensor for atmospheric pressure measurements in weather stations.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned BMP180 pressure sensor with accurate pressure readings.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Sourced from a scrapped weather station but fully functional.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package containing a BMP180 pressure sensor for atmospheric studies.', currval('item_type_seq'), null);

-- Insert the "Sensor, Accelerometer, MPU6050" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Sensor, Accelerometer, MPU6050', 'An MPU6050 accelerometer sensor for motion sensing.');

-- Create individual items for the "Sensor, Accelerometer, MPU6050" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested MPU6050 accelerometer sensor', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new MPU6050 accelerometer sensor', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and tested MPU6050 sensor for motion detection', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Salvaged MPU6050 accelerometer sensor in excellent condition', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock MPU6050 sensor for motion sensing', currval('item_type_seq'), null);

