CREATE TABLE IF NOT EXISTS function_ownership (
    user_id INT REFERENCES users(user_id),
    func_id INT REFERENCES function(func_id),
    created_date TIMESTAMP,
    func_name VARCHAR(100)
);
