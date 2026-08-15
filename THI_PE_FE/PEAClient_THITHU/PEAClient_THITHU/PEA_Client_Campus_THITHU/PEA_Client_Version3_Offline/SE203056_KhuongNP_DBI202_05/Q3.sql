select 
	[ProductID],
	[Name] ,
	[Color],
	[Cost] ,
	[Price] ,
	[SellEndDate]
from [Product]
where [Cost] < 100 and [SellEndDate] is not null
order by [Cost] asc