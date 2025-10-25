CREATE TABLE composite_function (
    func_id INT FOREIGN KEY REFERENCES function(func_id),
    inner_func_id INT FOREIGN KEY REFERENCES function(func_id),
    outer_func_id INT FOREIGN KEY REFERENCES function(func_id),
);
