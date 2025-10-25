CREATE TABLE function_ownership (
    user_id INT FOREIGN KEY REFERENCES user(user_id),
    func_id INT FOREIGN KEY REFERENCES function(func_id),
    created_date DATETIME,
    func_name VARCHAR(100),
);
