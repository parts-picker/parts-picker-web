UPDATE items i
SET assigned_project_id = null, status = 'IN_STOCK'
WHERE i.STATUS = 'RESERVED'
  AND i.assigned_project_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM required_item_types
    WHERE i.type_id = item_type_id
      AND i.assigned_project_id = project_id
  );