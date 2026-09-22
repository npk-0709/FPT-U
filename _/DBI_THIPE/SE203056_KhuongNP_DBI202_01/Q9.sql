create trigger tr_insert_Product on Product
after  insert 
as
begin

	select i.ProductID,
	i.Name as ProductName,
	i.ModelID,
	pm.Name as ModelName
	from inserted i , ProductModel pm
	where i.ModelID = pm.ModelID
	
end;