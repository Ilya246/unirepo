CREATE TABLE Function (
    funcId INT PRIMARY KEY,
    typeId INT CHECK (typeId>=1 AND typeId<=3),
    expression VARCHAR(200)
);