-- The user every integration test acts as, with an org unit of its own to work in.
INSERT INTO users (id, issuer, subject, username, display_name, type)
VALUES (1000, 'urn:parts-picker:test', 'test-subject', 'testuser', 'Test User', 'HUMAN');

INSERT INTO org_units (id, name, short_description, owner_id, created_by, created_on)
VALUES (1000, 'Test Workshop', 'The org unit integration test fixtures live in', 1000, 1000, now());

INSERT INTO org_unit_entitlements (id, org_unit_id, user_id, access_level, joined_on)
VALUES (1000, 1000, 1000, 'MAINTAIN', now());
