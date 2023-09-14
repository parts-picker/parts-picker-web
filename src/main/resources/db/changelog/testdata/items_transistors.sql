-- Insert the "NPN Transistor, 2N3904" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'NPN Transistor, 2N3904', 'A 2N3904 NPN transistor commonly used for switching and amplification in electronic circuits.');

-- Create individual items for the "NPN Transistor, 2N3904" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new 2N3904 NPN transistor for switching and amplification.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used 2N3904 NPN transistor for electronic applications.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened 2N3904 NPN transistor in original packaging.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', '2N3904 NPN transistor for switching and amplification in circuits.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned 2N3904 NPN transistor for electronic enthusiasts.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package containing a 2N3904 NPN transistor for various applications.', currval('item_type_seq'), null);

-- Insert the "NPN Transistor, 2N2222A" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'NPN Transistor, 2N2222A', 'A 2N2222A NPN transistor widely used for general-purpose switching and amplification in electronic circuits.');

-- Create individual items for the "NPN Transistor, 2N2222A" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new 2N2222A NPN transistor for versatile switching and amplification.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used 2N2222A NPN transistor for general-purpose electronics.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened 2N2222A NPN transistor in its original packaging.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', '2N2222A NPN transistor for various switching and amplification tasks.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned 2N2222A NPN transistor for electronics hobbyists.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package containing a 2N2222A NPN transistor for versatile applications.', currval('item_type_seq'), null);

-- Insert the "PNP Transistor, 2N3906" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'PNP Transistor, 2N3906', 'A 2N3906 PNP transistor commonly used for switching and amplification in electronic circuits.');

-- Create individual items for the "PNP Transistor, 2N3906" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new 2N3906 PNP transistor for various electronic applications.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used 2N3906 PNP transistor for switching tasks.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened 2N3906 PNP transistor in original packaging.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', '2N3906 PNP transistor for various switching and amplification needs.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned 2N3906 PNP transistor for electronics enthusiasts.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package containing a 2N3906 PNP transistor for electronic projects.', currval('item_type_seq'), null);

-- Insert the "Darlington Transistor, TIP120" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Darlington Transistor, TIP120', 'A TIP120 Darlington transistor ideal for high-power switching applications in electronics.');

-- Create individual items for the "Darlington Transistor, TIP120" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new TIP120 Darlington transistor for high-power switching.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used TIP120 Darlington transistor for robust switching tasks.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened TIP120 Darlington transistor in its original packaging.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'TIP120 Darlington transistor for high-power electronic applications.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned TIP120 Darlington transistor for heavy-duty switching.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null);

-- Insert the "NPN Transistor, BC547" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'NPN Transistor, BC547', 'A BC547 NPN transistor commonly used for low-power switching and amplification in electronic circuits.');

-- Create individual items for the "NPN Transistor, BC547" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new BC547 NPN transistor for low-power switching and amplification.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used BC547 NPN transistor for low-power electronic projects.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened BC547 NPN transistor in original packaging.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'BC547 NPN transistor for low-power switching and amplification in circuits.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned BC547 NPN transistor for DIY electronics.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package containing a BC547 NPN transistor for various applications.', currval('item_type_seq'), null);

-- Insert the "PNP Transistor, BC557" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'PNP Transistor, BC557', 'A BC557 PNP transistor commonly used for low-power switching and amplification in electronic circuits.');

-- Create individual items for the "PNP Transistor, BC557" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new BC557 PNP transistor for low-power switching and amplification.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used BC557 PNP transistor for low-power electronic projects.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened BC557 PNP transistor in original packaging.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'BC557 PNP transistor for low-power switching and amplification in circuits.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned BC557 PNP transistor for DIY electronics.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package containing a BC557 PNP transistor for various applications.', currval('item_type_seq'), null);

-- Insert the "MOSFET Transistor, IRF520" item type
INSERT INTO item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'MOSFET Transistor, IRF520', 'An IRF520 MOSFET transistor suitable for electronic switching and amplification applications.');

-- Create individual items for the "MOSFET Transistor, IRF520" item type
INSERT INTO items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new IRF520 MOSFET transistor for electronic switching and amplification.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used IRF520 MOSFET transistor for electronic projects.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened IRF520 MOSFET transistor in original packaging.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'IRF520 MOSFET transistor for electronic switching and amplification tasks.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned IRF520 MOSFET transistor for DIY electronics enthusiasts.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package containing an IRF520 MOSFET transistor for various applications.', currval('item_type_seq'), null);
