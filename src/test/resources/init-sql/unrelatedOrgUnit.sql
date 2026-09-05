-- An org unit unrelated to the test user, to prove requests across the boundary are refused.
INSERT INTO users (id, issuer, subject, username, display_name, type)
VALUES (2000, 'urn:parts-picker:test', 'unrelated-test-subject', 'unrelateduser', 'Unrelated User', 'HUMAN');

INSERT INTO org_units (id, name, short_description, owner_id, created_by, created_on)
VALUES (2000, 'Unrelated Workshop', 'An org unit the test user holds nothing in', 2000, 2000, now());

INSERT INTO org_unit_entitlements (id, org_unit_id, user_id, access_level, joined_on)
VALUES (2000, 2000, 2000, 'MAINTAIN', now());

INSERT INTO item_types (id, name, description, org_unit_id, created_by, created_on)
VALUES (2000, 'UNRELATED ITEM TYPE', 'Belongs to the unrelated org unit', 2000, 2000, now());

INSERT INTO items (id, status, condition, note, type_id, org_unit_id, created_by, created_on)
VALUES (2000, 'IN_STOCK', 'NEW', 'Belongs to the unrelated org unit', 2000, 2000, 2000, now());

INSERT INTO "groups" (id, name, description, org_unit_id, created_by, created_on)
VALUES (2000, 'UNRELATED GROUP', 'Belongs to the unrelated org unit', 2000, 2000, now());
