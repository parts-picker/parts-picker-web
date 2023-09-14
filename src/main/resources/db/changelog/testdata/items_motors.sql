-- Insert the "Motor, DC, 12V" item type
INSERT INTO public.item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Motor, DC, 12V', 'A 12V DC motor for various applications.');

-- Create individual items for the "Motor, DC, 12V" item type with null or random notes for "NEW" or "WRAPPED" items
INSERT INTO public.items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Removed from a robotics project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new in original packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Salvaged from a DIY project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and tested', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Unopened package', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null);

-- Insert the "Motor, DC, 6V" item type
INSERT INTO public.item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Motor, DC, 6V', 'A 6V DC motor for various applications.');

-- Create individual items for the "Motor, DC, 6V" item type with null or random notes for "NEW" or "WRAPPED" items
INSERT INTO public.items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Recovered from a toy car', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new in original packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Salvaged from a DIY project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and tested', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Unopened package', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null);

-- Insert the "Motor, Stepper, 5V" item type
INSERT INTO public.item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Motor, Stepper, 5V', 'A 5V stepper motor for precise control.');

-- Create individual items for the "Motor, Stepper, 5V" item type with null or random notes for "NEW" or "WRAPPED" items
INSERT INTO public.items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Retired from a 3D printer', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new in original packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Salvaged from an automation project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and tested', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Unopened package', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null);

-- Insert the "Motor, Servo, 9G" item type
INSERT INTO public.item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Motor, Servo, 9G', 'A 9G servo motor for precise control.');

-- Create individual items for the "Motor, Servo, 9G" item type with null or random notes for "NEW" or "WRAPPED" items
INSERT INTO public.items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Retired from a remote control car', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new in original packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Salvaged from a robotics project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and tested', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Unopened package', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null);

-- Insert the "Motor, Stepper, 12V" item type
INSERT INTO public.item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Motor, Stepper, 12V', 'A 12V stepper motor for precise control.');

-- Create individual items for the "Motor, Stepper, 12V" item type with null or random notes for "NEW" or "WRAPPED" items
INSERT INTO public.items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Retired from a CNC machine', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new in original packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Salvaged from an automation project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and tested', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Unopened package', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null);

-- Insert the "Stepper Motor, NEMA 17" item type
INSERT INTO public.item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Stepper Motor, NEMA 17', 'A NEMA 17 stepper motor for precise control in 3D printers and CNC machines.');

-- Create individual items for the "Stepper Motor, NEMA 17" item type with null or random notes for "NEW" or "WRAPPED" items
INSERT INTO public.items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Removed from a 3D printer', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new in original packaging', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Tested and working', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Salvaged from a CNC project', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Fresh stock', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Cleaned and tested', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new NEMA 17 stepper motor for precision control.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used NEMA 17 stepper motor for motion control applications.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened NEMA 17 stepper motor in original packaging.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Stepper motor for precise motion control.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned NEMA 17 stepper motor for motion control applications.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package containing a NEMA 17 stepper motor for precision control.', currval('item_type_seq'), null);

-- Insert the "Servo Motor, MG996R" item type
INSERT INTO public.item_types (id, name, description)
VALUES
    (nextval('item_type_seq'), 'Servo Motor, MG996R', 'An MG996R servo motor for precise control in robotics and automation applications.');

-- Create individual items for the "Servo Motor, MG996R" item type
INSERT INTO public.items (id, status, condition, note, type_id, assigned_project_id)
VALUES
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'Brand new MG996R servo motor for precise motion control.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Gently used MG996R servo motor for robotics applications.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened MG996R servo motor in original packaging.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', 'MG996R servo motor for precise control in robotics.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'NEW', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', 'Pre-owned MG996R servo motor for automation and robotics enthusiasts.', currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'USED', null, currval('item_type_seq'), null),
    (nextval('item_seq'), 'IN_STOCK', 'WRAPPED', 'Unopened package containing an MG996R servo motor for precise motion control.', currval('item_type_seq'), null);
