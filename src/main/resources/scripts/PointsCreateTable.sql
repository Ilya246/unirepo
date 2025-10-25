CREATE TABLE points (
    func_id INT FOREIGN KEY REFERENCES function(func_id),
    x_value FLOAT,
    y_value FLOAT
);
