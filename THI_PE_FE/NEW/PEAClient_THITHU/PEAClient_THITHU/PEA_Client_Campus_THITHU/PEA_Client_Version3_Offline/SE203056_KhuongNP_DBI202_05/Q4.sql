select  p.ProductID,
		p.Name as ProductName, 
		p.Price, 
		pm.Name as ModelName, 
		ps.Name as SubCategogyName,
		ps.Category

from Product p
left join ProductModel pm on p.ModelID = pm.ModelID
left join ProductSubcategory ps on p.SubcategoryID = ps.SubcategoryID

where p.Color = 'Black' and p.Price < 100