CREATE TABLE User (
    userId INT PRIMARY KEY,
    typeId INT CHECK (typeId>=1 AND typeId<=2),
    userName VARCHAR(100),
    password VARCHAR(20),
    createdDate DATETIME
);