INSERT INTO workflows VALUES(100, 'Testflows', 1, now());

INSERT INTO workflow_nodes VALUES(100, 100, 'planning', 'user_action', 'Planning');
INSERT INTO workflow_nodes VALUES(200, 100, 'implementation', 'user_action', 'Implementation');
INSERT INTO workflow_nodes VALUES(300, 100, 'report', 'user_action', 'Report');
INSERT INTO workflow_nodes VALUES(400, 100, 'stop', 'stop', 'Stop');


INSERT INTO workflow_edges VALUES(100, 100, 100, 200, 'planning_to_implementation', 'Start planning');
INSERT INTO workflow_edges VALUES(200, 100, 200, 300, 'implementation_to_report', 'Go to report');
INSERT INTO workflow_edges VALUES(300, 100, 300, 400, 'report_to_stop', 'Finish');


INSERT INTO workflow_instances VALUES(100, 100, true, null, 'DEFAULT');
INSERT INTO workflow_instances VALUES(200, 100, true, null, 'DEFAULT');
INSERT INTO workflow_instances VALUES(300, 200, true, null, 'DEFAULT');
INSERT INTO workflow_instances VALUES(400, 100, false, null, 'DEFAULT');
INSERT INTO workflow_instances VALUES(500, 400, true, null, 'DEFAULT');

INSERT INTO workflow_instance_values VALUES(100, 100, 'userID', 'Leonard', 'STRING', 'WORKFLOW');
INSERT INTO workflow_instance_values VALUES(200, 100, 'amount', '7', 'LONG', 'WORKFLOW');
