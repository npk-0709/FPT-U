select  p.ProductID,
		p.Name as ProductName, 
		p.Color,
		p.Cost, 
		p.Price,  
		pin.LocationID,
		l.Name as LocationName,
		pin.Shelf,
		pin.Bin,
		pin.Quantity
		
from Product p
left join ProductInventory pin on p.ProductID = pin.ProductID
left join Location l on l.LocationID = pin.LocationID

where p.Color = 'Yellow' and p.Cost < 400