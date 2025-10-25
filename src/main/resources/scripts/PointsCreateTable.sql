CREATE TABLE Points (
    funcId INT FOREIGN KEY REFERENCES Function(funcId),
    xValue FLOAT,
    yValue FLOAT
);