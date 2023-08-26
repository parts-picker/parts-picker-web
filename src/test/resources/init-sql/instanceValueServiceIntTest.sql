INSERT INTO workflows VALUES(100, 'Testflows', 1, now());

INSERT INTO workflow_nodes VALUES(100, 100, 'planning', 'user_action', 'Planning');

INSERT INTO workflow_instances VALUES(100, 100, 100, true, null, 'DEFAULT');
INSERT INTO workflow_instances VALUES(101, 100, 100, true, null, 'DEFAULT');

INSERT INTO workflow_instance_values VALUES(1, 100, 'existing', 'oldValue', 'STRING', 'WORKFLOW');
ALTER SEQUENCE instance_value_id_seq RESTART WITH 10;
