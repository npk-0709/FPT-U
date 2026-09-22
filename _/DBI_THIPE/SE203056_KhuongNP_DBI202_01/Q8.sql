create procedure proc_product_model 
 @modelID int ,
 @numberOfProducts int output 
as 
begin 
select COUNT(distinct p.ProductID) as NumberOfProducts
from Product p
where p.ModelID = @modelID
end;