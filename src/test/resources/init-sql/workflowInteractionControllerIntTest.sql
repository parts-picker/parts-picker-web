INSERT INTO workflows VALUES(1, 'Testflows', 1, now());

INSERT INTO workflow_nodes VALUES(1, 1, 'planning', 'user_action', 'Planning');
INSERT INTO workflow_nodes VALUES(2, 1, 'implementation', 'user_action', 'Implementation');
INSERT INTO workflow_nodes VALUES(3, 1, 'report', 'user_action', 'Report');

INSERT INTO workflow_edges VALUES(1, 1, 1, 2, 'planning_to_implementation', 'Start planning');
INSERT INTO workflow_edges VALUES(2, 1, 2, 3, 'implementation_to_report', 'Go to report');

INSERT INTO workflow_instances VALUES(1, 1, 1, true);
INSERT INTO workflow_instances VALUES(2, 1, 1, true);
INSERT INTO workflow_instances VALUES(3, 1, 2, true);
INSERT INTO workflow_instances VALUES(4, 1, null, false);

INSERT INTO workflow_instance_values VALUES(1, 1, 'userID', 'Leonard', 'STRING', 'WORKFLOW');
INSERT INTO workflow_instance_values VALUES(2, 1, 'amount', '7', 'LONG', 'WORKFLOW');