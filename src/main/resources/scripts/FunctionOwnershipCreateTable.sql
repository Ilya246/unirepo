CREATE TABLE FunctionOwnership (
    userId INT FOREIGN KEY REFERENCES User(userId),
    funcId INT FOREIGN KEY REFERENCES Function(funcId),
    createdDate DATETIME,
    funcName VARCHAR(100),
);