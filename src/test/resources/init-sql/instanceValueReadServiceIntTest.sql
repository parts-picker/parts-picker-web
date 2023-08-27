INSERT INTO workflows VALUES(100, 'Testflows', 1, now());

INSERT INTO workflow_nodes VALUES(100, 100, 'planning', 'user_action', 'Planning');

INSERT INTO workflow_instances VALUES(100, 100, true, null, 'DEFAULT');

INSERT INTO workflow_instance_values VALUES(100, 100, 'userID', 'Leonard', 'STRING', 'WORKFLOW');
INSERT INTO workflow_instance_values VALUES(200, 100, 'amount', '7', 'LONG', 'SYSTEM');
