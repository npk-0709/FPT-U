select p.ProductID,
p.Name as ProductName,
p.price,
pm.Name as ModelName,
psc.Name as SubCategoryName,
psc.Category
from Product p
left join ProductModel pm on p.ModelID = pm.ModelID
left join ProductSubcategory psc on p.SubcategoryID = psc.SubcategoryID

where p.price < 100 and p.Color = 'Black'
