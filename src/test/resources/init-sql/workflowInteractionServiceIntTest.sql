INSERT INTO workflows VALUES(100, 'Testflows', 1, now());
INSERT INTO workflows VALUES(200, 'Testflows', 2, now());


INSERT INTO workflow_nodes VALUES(100, 200, 'start', 'start', 'Start', 'WORKFLOW');
INSERT INTO workflow_nodes VALUES(200, 200, 'planning', 'user_action', 'Planning');
INSERT INTO workflow_nodes VALUES(300, 200, 'implementation', 'user_action', 'Implementation');
INSERT INTO workflow_nodes VALUES(400, 200, 'report', 'user_action', 'Report');
INSERT INTO workflow_nodes VALUES(500, 200, 'stop', 'stop', 'Stop');

INSERT INTO workflow_edges VALUES(100, 200, 100, 200, 'start_to_planning', 'Start');
INSERT INTO workflow_edges VALUES(200, 200, 200, 300, 'planning_to_implementation', 'Start implementing');
INSERT INTO workflow_edges VALUES(300, 200, 300, 400, 'implementation_to_report', 'Go to report');
INSERT INTO workflow_edges VALUES(400, 200, 400, 500, 'report_to_stop', 'Stop');

INSERT INTO workflow_instances VALUES(1, 200, 100, true);
INSERT INTO workflow_instances VALUES(2, 200, 500, false);
INSERT INTO workflow_instances VALUES(3, 200, 400, true);
ALTER SEQUENCE instance_id_seq RESTART WITH 10;

INSERT INTO projects VALUES(1, 'PROJECT 1', 'Description for project 1', null, 1);
