CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION postgis; 
       
insert into "GeneralSettings"("Id","Name","Value","CreatedAt") 
VALUES(uuid_generate_v4(),'one_kilo_price',150,CURRENT_TIMESTAMP);

insert into "GeneralSettings"("Id","Name","Value","CreatedAt")
VALUES(uuid_generate_v4(),'one_kilo_price',150,CURRENT_TIMESTAMP);

insert into "Currency"("Id","Name","Value","Symbol","IsDefault","CreatedAt") 
values(uuid_generate_v4(),'usa dolar',1,'$',true,CURRENT_TIMESTAMP);


CREATE OR REPLACE FUNCTION get_monthly_stats()
RETURNS TABLE(
  totalFee numeric,
  totalOrder bigint,
  totalDeliveryDistance numeric,
  userCount bigint,
  productCount bigint
) AS $$
BEGIN
RETURN QUERY
    WITH monthRevenue AS
       (SELECT SUM("TotalPrice") AS totalFee,
       count(*)as totoalOrder,
       SUM("DistanceFee") as totalDeliveryDistance
       FROM "Orders" 
       WHERE "Status"=5 AND "CreatedAt" = DATE_TRUNC('month',NOW()))
    ,toalUser AS 
        (SELECT COUNT(*) AS userCount
        FROM "Users"),
    toalProduct AS (
        SELECT COUNT(*) as toalProduct 
        FROM "Products")
    SELECT
    COALESCE(mr.totalFee,0.0) as totalFee,
    COALESCE(mr.totoalOrder,0) as totoalOrder,
    COALESCE(mr.totalDeliveryDistance,0.0) as totalDeliveryDistance,
    COALESCE(tu.userCount,0) as userCount,
    COALESCE(tp.toalProduct,0) as toalProduct
FROM monthRevenue mr, toalUser tu, toalProduct tp;
END;
$$ LANGUAGE plpgsql;


--triger for prevent deleted the OrderItems  after it is complated received
-- CREATE OR REPLACE FUNCTION Fun_prevent_delete_orderItem()
-- RETURNS Trigger As $$
-- DECLARE
-- isCanModifiOrder BOOLEAN :=false;
-- BEGIN

--  SELECT "Status"<=4 into isCanModifiOrder FROM "Orders" where "Id"=OLD."OrderId";
 
--  if OLD."Status">3 and isCanModifiOrder THEN
--    RETURN NULL;
--  END IF;
--  return OLD;
-- END
-- $$ LANGUAGE plpgsql;

-- CREATE OR REPLACE TRIGGER tr_prevent_delete_orderItem
-- BEFORE DELETE ON "OrderItems" FOR EACH ROW EXECUTE FUNCTION Fun_prevent_delete_orderItem();

              
              
              
 --triger for prevent uppdate the order after it is complated
-- CREATE OR REPLACE 
-- FUNCTION fun_prevent_update_orderItem_to_less_state_of_previes()
-- RETURNS Trigger As $$
-- DECLARE
--  	isCanModifiOrderItem BOOLEAN :=false;
-- BEGIN
--  	SELECT ("Status">= 4) 
-- 	INTO isCanModifiOrderItem 
-- 	FROM "Orders" WHERE "Id" = OLD."OrderId";
--  	IF  isCanModifiOrderItem = TRUE  THEN   -- this to prevent update on orderitme if the order is complate
-- 	    RETURN NULL;
-- 	END IF;
--  	RETURN NEW;
-- END
-- $$ LANGUAGE plpgsql;


-- CREATE OR REPLACE TRIGGER tr_prevent_update_orderItem 
-- BEFORE UPDATE ON "OrderItems"   FOR EACH ROW EXECUTE FUNCTION fun_prevent_update_orderItem_to_less_state_of_previes();




--triger for prevent deleted the order after it is complated
-- CREATE OR REPLACE FUNCTION Fun_prevent_delete_order()
-- RETURNS Trigger As $$
-- DECLARE
-- orderStatus INT :=0;
-- BEGIN
--  	SELECT "Status" into orderStatus FROM "Orders" where "Id"=OLD."Id";
-- 	IF orderStatus>4 THEN 
-- 		return null;
-- 	END IF;
--  return OLD;
-- END
-- $$ LANGUAGE plpgsql;

-- CREATE OR REPLACE TRIGGER tr_prevent_delete_order 
-- BEFORE DELETE ON "Orders"   FOR EACH ROW EXECUTE FUNCTION Fun_prevent_delete_order();




--triger for prevent uppdate the order after it is complated
-- CREATE OR REPLACE FUNCTION fun_prevent_update_order_to_less_state_of_previes()
-- RETURNS Trigger As $$
-- DECLARE

-- isThereOrderItemsNotSelected BOOLEAN :=false;
-- BEGIN
--    -- prevent any update on status 
--     SELECT COUNT(*)>1 INTO isThereOrderItemsNotSelected FROM "OrderItems" WHERE "Status"<3;
	
-- 	IF OLD."Status">4 OR isThereOrderItemsNotSelected  THEN -- prevent update in order if it is complate or the items is recived from delivery
-- 	    RETURN NULL;
-- 	END IF;
-- 	return NEW;
-- END
-- $$ LANGUAGE plpgsql;

-- CREATE OR REPLACE TRIGGER tr_prevent_delete_order 
-- BEFORE UPDATE ON "Orders"   FOR EACH ROW EXECUTE FUNCTION fun_prevent_update_order_to_less_state_of_previes();




CREATE OR REPLACE FUNCTION Calculate_distance_from_store_to_order_location(orderId UUID,storeId UUID)
RETURNS Int AS $$
DECLARE
    total_distance_km DOUBLE PRECISION := 0.0;
    user_long         NUMERIC;
    user_lat          NUMERIC;
    store_long         NUMERIC;
    store_lat          NUMERIC;
    kilo_price        NUMERIC;
    store_distance    DOUBLE PRECISION;
BEGIN
    -- Fetch per-km price with error handling
    SELECT "Value" INTO kilo_price
    FROM "GeneralSettings"
    WHERE "Name" = 'one_kilo_price';
    
    IF kilo_price IS NULL THEN
        RAISE EXCEPTION 'one_kilo_price not found in GeneralSettings';
    END IF;

    -- Get user coordinates with validation
    SELECT "Longitude", "Latitude" 
    INTO user_long, user_lat
    FROM "Orders" WHERE "Id" = orderId;

    
    SELECT "Longitude", "Latitude" 
    INTO store_long, store_lat
    FROM "Address" WHERE "OwnerId" = store_id;

    
    store_distance := ST_Distance(
        ST_SetSRID(ST_MakePoint(user_long, user_lat), 4326)::GEOGRAPHY,
        ST_SetSRID(ST_MakePoint(store_long, store_lat), 4326)::GEOGRAPHY
    ) / 1000.0;


    total_distance_km := total_distance_km + GREATEST(1, CEIL(store_distance));

    RETURN total_distance_km::INT;
 
END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION calculate_order_item_price(OrderItemId UUID,product_price NUMERIC )
RETURNS NUMERIC AS $$
DECLARE
    order_product_varient      RECORD;
    precentage_holder NUMERIC  ;
    price   NUMERIC := product_price;
BEGIN
    

     FOR order_product_varient IN
        SELECT 
             "ProductVarientId"
        FROM "OrdersProductsVarients"
         
    LOOP
        SELECT "Precentage" FROM "ProductVarients" INTO precentage_holder;
   
       price := price*precentage_holder;
    END LOOP;

    RETURN price;
 
END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION fun_remove_user_orderItem(userId UUID, OrderItemId UUID)
RETURNS BOOLEAN 
AS $$
DECLARE
 order_id      UUID;
 store_id     UUID;
 order_item_price  NUMERIC:=0;
 order_items_count  Int;
 order_items_store_count  Int;

 order_itmes_price  NUMERIC;
 distance_to_user_from_store   INT :=0;

 order_distance     Int;
 order_distance_fee     Int;
 order_fee          NUMERIC;
 is_block_user          BOOLEAN;


BEGIN

    SELECT IsBlocked INTO is_block_user WHERE "Id" = userId;
    IF IsBlocked THEN 
        RAISE EXCEPTION 'user is Blocked';
        RETURN FALSE;
    END IF;

    SELECT "OrderId","StoreId","Price" FROM "OrderItems" INTO order_id,store_id,order_item_price WHERE "Id" = OrderItemId;

    IF order_item_record."Status"!=0 THEN 
        RAISE EXCEPTION 'order is not in progress';
        RETURN FALSE;
    END IF;
    
     -- this to  get the order items 
    SELECT count(*) FROM "OrderItems" INTO order_items_count WHERE "OrderId" = order_id;

 
    IF  order_items_count = 1 THEN 
      DELETE FROM "Orders" WHERE "Id" = order_id;
      RETURN true;
    END IF;

    -- #this to save the origin distance order and fee 
    SELECT "DistanceToUser","DistanceToUser","DistanceFee","TotalPrice" 
			FROM "Orders" INTO order_distance,order_distance,order_distance_fee,order_fee 
			WHERE "OrderId" = order_id;
			

    SELECT count(*) 
			FROM "OrderItems" INTO order_items_store_count 
			WHERE "OrderId" = order_id AND "StoreId" = store_id;

    IF order_items_store_count=1 THEN
        SELECT * FROM Calculate_distance_from_store_to_order_location(order_id,store_id) INTO   distance_to_user_from_store ;    
    END IF;
    
    -- # this to calculate the total price for order item with the product varients 
    order_itmes_price := calculate_order_item_price(OrderItemId, order_item_price);
    
	DELETE FROM "OrderItems" WHERE "Id" = OrderItemId;
    UPDATE "Orders" SET  
           "DistanceToUser" = order_distance-distance_to_user_from_store,
           "DistanceFee" = DistanceFee-((order_distance_fee/order_distance)*distance_to_user_from_store),
           "TotalPrice"= order_fee - order_itmes_price;
    RETURN TRUE;

EXCEPTION 
    WHEN OTHERS THEN
        RAISE WARNING 'this error from remove user OrderItems %: %', orderId, SQLERRM;
        RETURN FALSE;
END;
$$ LANGUAGE plpgsql;




CREATE OR REPLACE FUNCTION fun_calculate_distance_between_user_and_stores(orderId UUID)
RETURNS BOOLEAN AS $$
DECLARE
    total_distance_km DOUBLE PRECISION := 0.0;
    user_long         NUMERIC;
    user_lat          NUMERIC;
    kilo_price        NUMERIC;
    store_coords      RECORD;
    store_distance    DOUBLE PRECISION;
BEGIN
    -- Fetch per-km price with error handling
    SELECT "Value" INTO kilo_price
    FROM "GeneralSettings"
    WHERE "Name" = 'one_kilo_price';
    
    IF kilo_price IS NULL THEN
        RAISE EXCEPTION 'one_kilo_price not found in GeneralSettings';
    END IF;

    -- Get user coordinates with validation
    SELECT "Longitude", "Latitude" 
    INTO user_long, user_lat
    FROM "Orders" WHERE "Id" = orderId;
    
    IF user_long IS NULL OR user_lat IS NULL THEN
        RAISE EXCEPTION 'NULL coordinates for order %', orderId;
    END IF;

     FOR store_coords IN
        (SELECT 
            a."Longitude" AS store_long,
            a."Latitude" AS store_lat
        FROM "OrderItems" oi
        JOIN "Address" a 
          ON a."OwnerId" = oi."StoreId"
        WHERE oi."OrderId" = orderId
          AND a."Longitude" IS NOT NULL
          AND a."Latitude" IS NOT NULL)
    LOOP
        -- Calculate distance in meters, convert to km
        store_distance := ST_Distance(
            ST_SetSRID(ST_MakePoint(user_long, user_lat), 4326)::GEOGRAPHY,
            ST_SetSRID(ST_MakePoint(store_coords.store_long, store_coords.store_lat), 4326)::GEOGRAPHY
        ) / 1000.0;

   
        total_distance_km := total_distance_km + GREATEST(1, CEIL(store_distance));
    END LOOP;

    -- Update order with calculated fee
    UPDATE "Orders"
    SET "DistanceFee" = kilo_price * total_distance_km,
	"DistanceToUser"=total_distance_km
    WHERE "Id" = orderId;

    RETURN TRUE;
EXCEPTION 
    WHEN OTHERS THEN
        RAISE WARNING 'Calculation failed for order %: %', orderId, SQLERRM;
        RETURN FALSE;
END;
$$ LANGUAGE plpgsql;






CREATE OR REPLACE FUNCTION get_delivery_fee_info(
 deleiveryId UUID)
RETURNS TABLE (
dayFee DECIMAL,
weekFee DECIMAL,
monthFee DECIMAL,
dayOrder INT,
weekOrder Int)
AS $$
DECLARE
	dayFee DECIMAL:=0.0;
	weekFee DECIMAL :=0.0;
	monthFee DECIMAL :=0.0;
	orders   RECORD;
	dayOrder INT:=0;
    weekOrder Int:=0;
	--this to get the current day of week
	currentDay INT:=0;
	--this to get rest days of week
	restDaies INT :=7;
	--this to holde the start week and end week date 
	startWeek DATE ;
	endWeek DATE ;
	
BEGIN
   -- submite the current day of week
   SELECT 
  (((EXTRACT(ISODOW FROM now())::int + 1) % 7) + 1) 
  INTO currentDay;

   -- this to get the rest of days until end of week
   restDaies:= restDaies-(currentDay+1);-- 7-6
   
   endWeek:= (now()::DATE+restDaies);
   startWeek:= (now()::DATE -(currentDay+1));

   FOR orders IN
	   (SELECT "DistanceFee" as fee,
	           "CreatedAt" as createdDate
	   FROM "Orders" 
       WHERE "Id"=deleiveryId
	   AND "Status">=5
	   AND 
	   date_trunc('month',"CreatedAt")=
	   date_trunc('month',now())
	   )
   LOOP 
    IF orders.createdDate=now()::DATE THEN 
	  dayFee:= dayFee + orders.fee;
	  dayOrder:= dayOrder+1;
	END IF;

	IF orders.createdDate<=endWeek AND orders.createdDate>=startWeek THEN 
	  weekFee := weekFee +  orders.fee;
	  weekOrder:= weekOrder+1;
	END IF ;
     monthFee := monthFee + orders.fee;
   END LOOP;

   RETURN  QUERY SELECT dayFee,weekFee,monthFee,dayOrder,weekOrder;
EXCEPTION
   WHEN OTHERS THEN 
   RAISE EXCEPTION 'Something went wrong: %',SQLERRM;
   RETURN 	 QUERY SELECT NULL,NULL,NULL,NULL,NULL;
END 
$$ LANGUAGE plpgsql;