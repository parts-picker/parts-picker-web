INSERT INTO workflows VALUES(1, 'Testflows', 1, now());
INSERT INTO workflows VALUES(2, 'Testflows', 2, now());


INSERT INTO workflow_nodes VALUES(1, 2, 'start', 'start', 'Start', 'WORKFLOW');
INSERT INTO workflow_nodes VALUES(2, 2, 'planning', 'user_action', 'Planning');
INSERT INTO workflow_nodes VALUES(3, 2, 'implementation', 'user_action', 'Implementation');
INSERT INTO workflow_nodes VALUES(4, 2, 'report', 'user_action', 'Report');
INSERT INTO workflow_nodes VALUES(5, 2, 'stop', 'stop', 'Stop');

INSERT INTO workflow_edges VALUES(1, 2, 1, 2, 'start_to_planning', 'Start');
INSERT INTO workflow_edges VALUES(2, 2, 2, 3, 'planning_to_implementation', 'Start planning');
INSERT INTO workflow_edges VALUES(3, 2, 3, 4, 'implementation_to_report', 'Go to report');
INSERT INTO workflow_edges VALUES(4, 2, 4, 5, 'report_to_stop', 'Stop');

INSERT INTO workflow_instances VALUES(1, 2, 1, true);
INSERT INTO workflow_instances VALUES(2, 2, 5, false);
INSERT INTO workflow_instances VALUES(3, 2, 4, true);
ALTER SEQUENCE instance_id_seq RESTART WITH 10;