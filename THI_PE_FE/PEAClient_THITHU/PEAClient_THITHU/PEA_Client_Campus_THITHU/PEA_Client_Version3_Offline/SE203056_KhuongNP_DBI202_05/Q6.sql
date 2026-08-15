select l.LocationID, l.Name as LocationName, COUNT(p.ProductID)
from Location l
join ProductInventory pin on l.LocationID = pin.LocationID
join Product p on p.ProductID = pin.ProductID
