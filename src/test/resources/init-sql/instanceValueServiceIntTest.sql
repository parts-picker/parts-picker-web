INSERT INTO workflows VALUES(1, 'Testflows', 1, now());

INSERT INTO workflow_nodes VALUES(1, 1, 'planning', 'user_action', 'Planning');

INSERT INTO workflow_instances VALUES(1, 1, 1, true);
