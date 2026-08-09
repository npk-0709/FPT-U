SELECT TOP 1 WITH TIES
l.LocationID,l.Name as LocationName,COUNT(l.LocationID) as NumberOfProducts
FROM Location l, ProductInventory pin, Product p
WHERE l.LocationID = pin.LocationID
  AND pin.ProductID = p.ProductID
GROUP BY l.LocationID, l.Name
ORDER BY COUNT(l.LocationID) ASC;