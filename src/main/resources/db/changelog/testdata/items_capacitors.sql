-- Insert the "Electrolytic Capacitor, 100μF" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Electrolytic Capacitor, 100μF', '100μF electrolytic capacitors for power supply filtering.');

-- Create individual items for the "100μF" capacitor item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new 100μF electrolytic capacitor', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package containing a 100μF capacitor', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used 100μF capacitor, still functional', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working 100μF capacitor from a previous project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Brand new 100μF capacitor in original packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock 100μF capacitor for your electronics experiments', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned 100μF capacitor with minor cosmetic wear', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened box containing a 100μF capacitor', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Removed from an old amplifier circuit', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Freshly manufactured', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Surplus from previous project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Unused spare part', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working perfectly', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Recycled from an old PCB', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new in original packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Slightly used but fully functional', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Manufactured recently', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Salvaged from an old power supply', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'High-quality capacitor', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Removed from a damaged motherboard', currval('item_type_seq'), null);

-- Insert the "Ceramic Capacitor, 10nF" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Ceramic Capacitor, 10nF', '10nF ceramic capacitors for decoupling and noise suppression.');

-- Create individual items for the "10nF" capacitor item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new 10nF ceramic capacitor for your projects', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package containing a 10nF capacitor', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used 10nF capacitor, still functional', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working 10nF capacitor from a previous project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Brand new 10nF capacitor in original packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock of 10nF capacitors for your electronics experiments', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned 10nF capacitor with minor cosmetic wear', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Unopened box containing a 10nF capacitor', currval('item_type_seq'), null);

-- Insert the "Polyester Film Capacitor, 100nF" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Polyester Film Capacitor, 100nF', '100nF polyester film capacitors for coupling and signal filtering.');

-- Create individual items for the "100nF" polyester film capacitor item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new 100nF polyester film capacitor', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package containing a 100nF polyester film capacitor', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used 100nF polyester film capacitor, still functional', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working 100nF polyester film capacitor from a previous project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Brand new 100nF polyester film capacitor in original packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock 100nF polyester film capacitor for your electronics experiments', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned 100nF polyester film capacitor with minor cosmetic wear', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened box containing a 100nF polyester film capacitor', currval('item_type_seq'), null);

-- Insert the "Aluminum Electrolytic Capacitor, 10μF" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Aluminum Electrolytic Capacitor, 10μF', '10μF aluminum electrolytic capacitors for various electronic applications.');

-- Create individual items for the "10μF" aluminum electrolytic capacitor item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new 10μF aluminum electrolytic capacitor', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package containing a 10μF aluminum electrolytic capacitor', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used 10μF aluminum electrolytic capacitor, still functional', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working 10μF aluminum electrolytic capacitor from a previous project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Brand new 10μF aluminum electrolytic capacitor in original packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock 10μF aluminum electrolytic capacitor for your electronics experiments', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned 10μF aluminum electrolytic capacitor with minor cosmetic wear', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened box containing a 10μF aluminum electrolytic capacitor', currval('item_type_seq'), null);

-- Insert the "Tantalum Capacitor, 22μF" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Tantalum Capacitor, 22μF', '22μF tantalum capacitors known for high reliability and stability.');

-- Create individual items for the "22μF" tantalum capacitor item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new 22μF tantalum capacitor', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package containing a 22μF tantalum capacitor', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used 22μF tantalum capacitor, still functional', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working 22μF tantalum capacitor from a previous project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Brand new 22μF tantalum capacitor in original packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock 22μF tantalum capacitor for your electronics experiments', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned 22μF tantalum capacitor with minor cosmetic wear', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened box containing a 22μF tantalum capacitor', currval('item_type_seq'), null);
