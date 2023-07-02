DO $$
DECLARE
	required_type required_item_types%ROWTYPE;
	assigned_amount BIGINT;
	remove_amount BIGINT;
	item items%ROWTYPE;
BEGIN
	FOR required_type IN SELECT * FROM required_item_types
	LOOP
		SELECT COUNT(*) FROM items INTO assigned_amount WHERE type_id = required_type.item_type_id AND assigned_project_id = required_type.project_id;
		RAISE NOTICE 'Found item type id %, project id %, requiredAmount %, assignedAmount %', required_type.item_type_id, required_type.project_id, required_type.required_amount, assigned_amount;

		IF required_type.required_amount < assigned_amount THEN
			remove_amount := assigned_amount - required_type.required_amount;
			RAISE NOTICE '-> needs correction, removeAmount %', remove_amount;

			FOR item IN SELECT * FROM items WHERE type_id = required_type.item_type_id AND assigned_project_id = required_type.project_id FETCH FIRST remove_amount ROWS ONLY
			LOOP
				UPDATE items SET assigned_project_id = null, status='IN_STOCK' WHERE id = item.id;
			END LOOP;
		END IF;
	END LOOP;
END$$;