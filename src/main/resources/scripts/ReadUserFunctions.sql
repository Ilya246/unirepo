SELECT f.*, fo.*
FROM function_ownership fo
JOIN function f ON fo.func_id=f.func_id
WHERE fo.user_id=?;