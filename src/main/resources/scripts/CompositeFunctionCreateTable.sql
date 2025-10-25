CREATE TABLE CompositeFunction (
    funcId INT FOREIGN KEY REFERENCES Function(funcId),
    innerFuncId INT FOREIGN KEY REFERENCES Function(funcId),
    outerFuncId INT FOREIGN KEY REFERENCES Function(funcId),
);