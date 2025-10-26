CREATE TABLE IF NOT EXISTS composite_function (
    func_id INT REFERENCES function(func_id),
    inner_func_id INT REFERENCES function(func_id),
    outer_func_id INT REFERENCES function(func_id)
);
