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

INSERT INTO workflow_instances VALUES(1, 200, 100, true, null, 'DEFAULT');
INSERT INTO workflow_instances VALUES(2, 200, 500, false, null, 'DEFAULT');
INSERT INTO workflow_instances VALUES(3, 200, 400, true, null, 'DEFAULT');
ALTER SEQUENCE instance_id_seq RESTART WITH 10;

INSERT INTO projects VALUES(1, 'PROJECT 1', 'Description for project 1', null, 1);
ALTER SEQUENCE project_seq RESTART WITH 10;

-- Workflow to test readEdgesBySourceNodeId
INSERT INTO workflows VALUES(300, 'Edgetest', 1, now());

INSERT INTO workflow_nodes VALUES(1000, 300, 'start', 'start', 'Start', 'WORKFLOW');
INSERT INTO workflow_nodes VALUES(1001, 300, 'planning', 'user_action', 'Planning');
INSERT INTO workflow_nodes VALUES(1002, 300, 'stop', 'stop', 'Stop');

INSERT INTO workflow_edges VALUES(1000, 300, 1000, 1001, '1->2', 'Start');
INSERT INTO workflow_edges VALUES(1001, 300, 1001, 1002, '2->3', 'Start implementing');
INSERT INTO workflow_edges VALUES(1002, 300, 1000, 1002, '1->3', 'Go to report');
