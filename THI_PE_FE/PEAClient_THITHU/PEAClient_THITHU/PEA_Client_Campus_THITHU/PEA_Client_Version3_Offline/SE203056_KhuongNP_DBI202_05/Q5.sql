select ps.SubcategoryID,
 ps.Name as SubCategogyname,
 ps.Category,
COUNT(distinct p.ProductID) as NumberOfProducts
	
from ProductSubcategory ps
join Product p on p.SubcategoryID=ps.SubcategoryID
group by
	ps.SubcategoryID,
	ps.Name,
	ps.Category

order by 
	ps.Category asc , 
	COUNT(p.SubcategoryID) desc,
	ps.Name asc