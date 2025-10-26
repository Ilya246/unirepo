CREATE TABLE IF NOT EXISTS points (
    func_id INT REFERENCES function(func_id),
    x_value FLOAT,
    y_value FLOAT
);
