var orderId;
var changeOrderData =[]
var deleteOrderData = []
function getOrderUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order";
}
function getOrderItemUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order-item";
}
function getInvoiceUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/invoice";
}
var orderData =[]
function createOrder(){
    $('.add-order').prop('hidden', false);
    $('.update-order').prop('hidden', true);
    $('#type').html('add');
    orderData =[]
    orderFormToggle()
}
// PLACE ORDER FUNCTIONS
function orderFormToggle(event){
	//Set the values to update
	$('#order-cart-form input[name=barcode]').val('');
    $('#order-cart-form input[name=quantity]').val('');
    $('#order-cart-form input[name=sellingPrice]').val('');
	$('#order-cart-modal').modal('toggle');
	cartItemList(orderData)
	return false;
}
function findOrderItem(data){
    for(var i in orderData){
        if(data.barcode == orderData[i].barcode){
            return i;
        }
    }
    return -1;
}

function findChangeOrderItem(data){

    for(var i in changeOrderData){
        if(data.barcode == changeOrderData[i].barcode){
            return i;
        }
    }
    return -1;
}


function editOrderItem(index){

    var id = '#edit-order-'+index+''
    console.log($(id).find('#inputBrand').val())
    var $form = {
        "barcode": $(id).find('#inputBarcode').val(),
        "quantity": $(id).find('#inputQuantity').val(),
        "sellingPrice": $(id).find('#inputPrice').val()
        }
    	var json = JSON.stringify($form);
    	var jsonParseData = JSON.parse(json);
    	var changeIndex = findOrderItem(jsonParseData);
    	var url = getOrderItemUrl();
        if($("#type").html() == "add"){
    	$.ajax({
            	   url: url,
            	   type: 'POST',
            	   data: json,
            	   headers: {
                   	'Content-Type': 'application/json'
                   },
            	   success: function(response) {

            	        handleSuccessMessage("Order item updated");
            	        orderData[changeIndex] = jsonParseData;

                        cartItemList(orderData);
            	   },
            	   error: handleAjaxError
            	});
         }else{
         url+='/'+orderId;
         console.log(url)
         $.ajax({
            	   url: url,
            	   type: 'POST',
            	   data: json,
            	   headers: {
                   	'Content-Type': 'application/json'
                   },
            	   success: function(response) {

            	        handleSuccessMessage("Order item updated");
            	        orderData[changeIndex] = jsonParseData;
                        cartItemList(orderData);
            	   },
            	   error: handleAjaxError
            	});
         }
        return false;
}
function addOrder(event){
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
    if($("#type").html() == "add"){
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
    }else{
        url+='/'+orderId;
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
    }

}

function editCartItem(index){
    data = orderData[index]
    var buttonHtml = '<div class="row"><button onclick="editOrderItem(' + index + ')" class="btn btn-dark mr-2 custom-button" data-toggle="tooltip" data-placement="top" title="Save Edit"><i class="material-icons">save</i></button>'
                        +'<button onclick="cancelCartItem(' + index + ')" class="btn btn-outline-dark custom-button" data-toggle="tooltip" data-placement="top" title="Close"><i class="material-icons">close</i></button></div>'
    var id = '#edit-order-'+index+''
    var form = '<td><input type="text" class="form-control" required name="barcode" id="inputBarcode" placeholder="Barcode" maxlength="100" value="'+data.barcode+'"></td>'
               		+'<td><input type="number" class="form-control" required name="quantity" id="inputQuantity" placeholder="Quantity" maxlength="100" value="'+data.quantity+'"></td>'
               		+'<td><input type="number" class="form-control" required name="sellingPrice" id="inputPrice" placeholder="Selling Price" maxlength="100" value="'+data.sellingPrice+'"></td>'
               		+'<input type="text" hidden class="form-control" name="index" value="'+index+'">'
               		+'<td class="text-center d-flex justify-content-center">' + buttonHtml + '</td>'

    $(id).html(form);


}
function cancelCartItem(index){
e = orderData[index]
     var id = '#edit-order-'+index+''
    var editButton = '<div class="row"><button onclick="editCartItem('+ index +')" class="btn btn-outline-dark mr-2 custom-button" data-toggle="tooltip" data-placement="top" title="Edit"><i class="material-icons">edit</i></button>'
    +'<button onclick="removeCartItem('+ index +')" class="btn btn-outline-dark custom-button" data-toggle="tooltip" data-placement="top" title="Remove Item"><i class="material-icons">delete</i></button> </div>'
    var row = '<td>' + e.barcode + '</td>'
    		+ '<td>' + e.quantity + '</td>'
    		+ '<td>' + e.sellingPrice + '</td>'
    		+ '<td class="text-center d-flex justify-content-center">' + editButton + '</td>'
    	$(id).html(row);
}



function removeCartItem(index){
    orderData.splice(index, 1);
    handleSuccessMessage("Order Item Removed")
    cartItemList(orderData);
}
function cartItemList(data){
	var $tbody = $('#order-item-table').find('tbody');
	console.log(data);
	$tbody.empty();
	for(var i in data){

		var e = data[i];
		console.log(e);
		var buttonHtml = '<div class="row"><button onclick="editCartItem('+ i +')" class="btn btn-outline-dark mr-2 custom-button" data-toggle="tooltip" data-placement="top" title="Edit Item"><i class="material-icons">edit</i></button>'
                             +'<button onclick="removeCartItem('+ i +')" class="btn btn-outline-dark custom-button" data-toggle="tooltip" data-placement="top" title="Remove Item"><i class="material-icons">delete</i></button></div>'
		var row = '<tr id="edit-order-'+ i +'">'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>' + e.quantity + '</td>'
		+ '<td>' + e.sellingPrice + '</td>'
		+ '<td class="text-center d-flex justify-content-center">' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}
function placeOrder(){
	var url = getOrderUrl();
	var data = {
        orderItems: orderData
    };
	data = JSON.stringify(data);
	console.log(data);
	$.ajax({
    	   url: url,
    	   type: 'POST',
    	   data: data,
    	   headers: {
           	'Content-Type': 'application/json'
           },
    	   success: function(response) {
    	        orderData =[]
    	        orderFormToggle();
    	        handleSuccessMessage("Order Created successfully");
    	   		getOrderList();
    	   },
    	   error: handleAjaxError
    	});
    return false;
}

// VIEW ORDER FUNCTIONS
function getOrderList(){
	var url = getOrderUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayOrderList(data);  
	   },
	   error: handleAjaxError
	});
}
function downloadInvoice(id){
	var url = getInvoiceUrl()+'/'+id;
	window.location.href = url;
}

function generateInvoice(id){
	var url = getInvoiceUrl();
    	var data = {
            orderId: id
        };
    	data = JSON.stringify(data);
    	$.ajax({
        	   url: url,
        	   type: 'POST',
        	   data: data,
        	   headers: {
               	'Content-Type': 'application/json'
               },
        	   success: function(response) {
        	        handleSuccessMessage("Invoice Generated");
        	        downloadInvoice(id);
        	   		getOrderList();
        	   },
        	   error: handleAjaxError
        	});
        return false;
}


function deleteOrder(id){
	var url = getOrderUrl() + "/" + id;

	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   		getOrderList();  
	   },
	   error: handleAjaxError
	});
}

function convertDateTime(dateTime){
console.log(dateTime);
dateTime = dateTime.split('[')[0]
let dateString = dateTime;
let date = new Date(dateString);
let localDateTime = date.toLocaleString();
console.log(localDateTime);
return localDateTime;
}


function updateOrder(){
    console.log(orderData);
	var url = getOrderUrl()+'/'+orderId;
	var data = {
        orderItems: orderData
    };
	data = JSON.stringify(data);
	console.log(data);
	$.ajax({
    	   url: url,
    	   type: 'PUT',
    	   data: data,
    	   headers: {
           	'Content-Type': 'application/json'
           },
    	   success: function(response) {
    	        orderData =[]
    	        changeOrderData = []
    	        orderFormToggle();
    	        handleSuccessMessage("Order Updated successfully");
    	   		getOrderList();
    	   },
    	   error: handleAjaxError
    	});
    return false;
}
function editOrder(id){
    $('.add-order').prop('hidden', true);
    $('.update-order').prop('hidden', false);
    $('#type').html('edit');
    var url = getOrderUrl() + "/" + id;
    	$.ajax({
    	   url: url,
    	   type: 'GET',
    	   success: function(data) {
    	        orderId = data.id
    	         orderData=data.order
    	         orderFormToggle();
    	   },
    	   error: handleAjaxError
    	});
}

function displayOrderList(data){
	var $tbody = $('#order-table').find('tbody');
	$tbody.empty();
    var index=0
	for(var i in data){
	    index++
		var e = data[data.length-i-1];
		var buttonHtml = '<div><div class="d-flex flex-row justify-content-center">'
		if(!e.isInvoiceGenerated){
		buttonHtml += '<button onclick="viewOrder(' + e.id + ')" class="btn btn-outline-dark custom-button" data-toggle="tooltip" data-placement="top" title="View Order"><i class="material-icons">visibility</i></button>&nbsp;'
		buttonHtml += '<button onclick="editOrder(' + e.id + ')" class="btn btn-outline-dark custom-button" data-toggle="tooltip" data-placement="top" title="Edit Order"><i class="material-icons">edit</i></button>'
		var invoice  =  '<button onclick="generateInvoice(' + e.id + ')" class="btn btn-dark custom-button mx-auto" data-toggle="tooltip" data-placement="top" title="Generate Invoice"><i class="material-icons">description</i></button>'}
		else{
		buttonHtml += '<button onclick="viewOrder(' + e.id + ')" class="btn btn-outline-dark custom-button" data-toggle="tooltip" data-placement="top" title="View Order"><i class="material-icons">visibility</i></button>&nbsp;'
        buttonHtml += '<button onclick="editOrder(' + e.id + ')" class="btn btn-outline-dark custom-button" data-toggle="tooltip" data-placement="top" title="Edit Order" disabled><i class="material-icons">edit</i></button>'
        var invoice  =  '<button onclick="downloadInvoice(' + e.id + ')" class="btn btn-dark custom-button mx-auto" data-toggle="tooltip" data-placement="top" title="Download Invoice"><i class="material-icons">file_download</i></button>'
		}
		buttonHtml+='</div></div>'
		var row = '<tr>'
		+ '<th scope="row">' + index + '</th>'
		+ '<td>' + convertDateTime(e.dateTime) + '</td>'
		+ '<td class="text-center">' + buttonHtml + '</td>'
		+ '<td class="text-center">' + invoice + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}
function deleteOrder(id){
    var url = getOrderUrl() + "/" + id;
    	$.ajax({
    	   url: url,
    	   type: 'DELETE',
    	   success: function(data) {
    	   		getOrderList();
    	   },
    	   error: handleAjaxError
    	});
}
function viewOrder(id){
    $('#order-view-modal').modal('toggle');
    var url = getOrderUrl() + "/" + id;
    	$.ajax({
    	   url: url,
    	   type: 'GET',
    	   success: function(data) {
    	   		displayOrderItemList(data, id);
    	   },
    	   error: handleAjaxError
    	});
}
function displayOrderItemList(data, id){
	var $tbody = $('#order-view-item-table').find('tbody');
	var $orderid = $('#order-id').find('div')
	$orderid.empty();
	$orderid.append('Order ID: '+id);
	console.log(data);
	data = data.order
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var row = '<tr>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>' + e.quantity + '</td>'
		+ '<td>' + e.sellingPrice + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}



//INITIALIZATION CODE
function init(){
    $('#create-order-item').click(createOrder);
	$('#add-order').click(addOrder);
	$('#place-order').click(placeOrder);
	$('#update-order').click(updateOrder);
}

$(document).ready(init);
$(document).ready(getOrderList);

