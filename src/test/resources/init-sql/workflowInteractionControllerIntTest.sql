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

-- one project per instance, since the workflow routes are addressed through their owning project
INSERT INTO projects (id, name, short_description, group_id, instance_id, org_unit_id, created_by, created_on)
VALUES(100, 'PROJECT 100', null, null, 100, 1000, 1000, now());
INSERT INTO projects (id, name, short_description, group_id, instance_id, org_unit_id, created_by, created_on)
VALUES(200, 'PROJECT 200', null, null, 200, 1000, 1000, now());
INSERT INTO projects (id, name, short_description, group_id, instance_id, org_unit_id, created_by, created_on)
VALUES(300, 'PROJECT 300', null, null, 300, 1000, 1000, now());
INSERT INTO projects (id, name, short_description, group_id, instance_id, org_unit_id, created_by, created_on)
VALUES(400, 'PROJECT 400', null, null, 400, 1000, 1000, now());
INSERT INTO projects (id, name, short_description, group_id, instance_id, org_unit_id, created_by, created_on)
VALUES(500, 'PROJECT 500', null, null, 500, 1000, 1000, now());
