CREATE TABLE users (
    user_id INT PRIMARY KEY,
    type_id INT CHECK (type_id>=1 AND type_id<=2),
    user_name VARCHAR(100),
    password VARCHAR(20),
    created_date DATETIME
);
