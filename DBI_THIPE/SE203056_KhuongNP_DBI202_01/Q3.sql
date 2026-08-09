select 
	ProductID,
	LocationID ,
	Quantity
from ProductInventory
where Quantity > 250 and LocationID = 7
order by Quantity desc