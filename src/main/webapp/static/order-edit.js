
function updateOrderItem(index){

    var id = '#edit-order-'+index+''
    console.log($(id).find('#inputBrand').val())
    var $form = {
        "barcode": $(id).find('#inputBarcode').val(),
        "quantity": $(id).find('#inputQuantity').val(),
        "sellingPrice": $(id).find('#inputPrice').val()
    }
    	var json = JSON.stringify($form);
    	var oldIndex = findOldOrderItem($(id).find('#inputBarcode').val());
    	var jsonParseData = JSON.parse(json);
    	if(oldIndex!=-1){
    	    console.log("Old Index", oldOrderData[oldIndex].quantity)
    	    var newQuantity = $(id).find('#inputQuantity').val()
    	    var oldQuantity = oldOrderData[oldIndex].quantity
    	    if(newQuantity > oldQuantity){
            $form["quantity"] = $(id).find('#inputQuantity').val()-oldOrderData[oldIndex].quantity;
            }else{
                if(newQuantity <= 0){
                    handleErrorMsg("Quantity must be positive")
                   return;
                }else{
                    handleSuccessMessage("Order item updated")
                    orderData[index] = jsonParseData;
                    cartItemList(orderData);
                    return;
                }
            }
            json = JSON.stringify($form);
    	}
    	var url = getOrderItemUrl();

    	console.log(json)
    	$.ajax({
            	   url: url,
            	   type: 'POST',
            	   data: json,
            	   headers: {
                   	'Content-Type': 'application/json'
                   },
            	   success: function(response) {

            	        handleSuccessMessage("Order item updated");
            	        orderData[index] = jsonParseData;
            	        console.log("Old Index success", oldOrderData[oldIndex].quantity)
                        cartItemList(orderData);
            	   },
            	   error: handleAjaxError
            	});

        return false;
}



function createOrderUpdate(){
    var $form = $("#order-cart-form");
    	var json = toJson($form);
    	var url = getOrderItemUrl();
    	var jsonParseData = JSON.parse(json);
    	jsonParseData.barcode = jsonParseData.barcode.toLowerCase()
    	var index = findOrderItem(jsonParseData);
    	if(index != -1){
    	    if( Number(jsonParseData.sellingPrice) !=  Number(orderData[index].sellingPrice)){
    	        handleErrorMsg("Selling Price cannot be different for same item");
    	        return false;
    	    }else{
    	        jsonParseData.quantity = String(Number(jsonParseData.quantity) + Number(orderData[index].quantity))
    	    }
    	}
    	$.ajax({
            	   url: url,
            	   type: 'POST',
            	   data: JSON.stringify(jsonParseData),
            	   headers: {
                   	'Content-Type': 'application/json'
                   },
            	   success: function(response) {
                        $('#order-cart-form input[name=barcode]').val('');
                        $('#order-cart-form input[name=quantity]').val('');
                        $('#order-cart-form input[name=sellingPrice]').val('');
            	        handleSuccessMessage("Order item added");
            	        tempOrderData=[]
                             for(var i in orderData){
                                if(i==index){
                                continue
                                }else{
                                tempOrderData.push(orderData[i]);
                                }
                             }
                             orderData = tempOrderData
            	        orderData.push(jsonParseData);
                        cartItemList(orderData);
            	   },
            	   error: handleAjaxError
            	});

        return false;
}