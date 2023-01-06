INSERT INTO workflows VALUES(1, 'Testflows', 1, now());

INSERT INTO workflow_nodes VALUES(1, 1, 'planning', 'user_action', 'Planning');

INSERT INTO workflow_instances VALUES(1, 1, 1, true);

INSERT INTO workflow_instance_values VALUES(1, 1, 'existing', 'oldValue', 'STRING', 'WORKFLOW');
ALTER SEQUENCE instance_value_id_seq RESTART WITH 10;