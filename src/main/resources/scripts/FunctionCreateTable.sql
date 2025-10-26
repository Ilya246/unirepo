CREATE TABLE IF NOT EXISTS function (
    func_id INT PRIMARY KEY,
    type_id INT CHECK (type_id>=1 AND type_id<=3),
    expression VARCHAR(200)
);
