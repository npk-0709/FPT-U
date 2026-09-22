DELETE FROM ProductInventory
WHERE ProductID IN (select ProductID from Product where ModelID =33)