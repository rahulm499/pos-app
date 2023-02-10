
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

// PLACE ORDER FUNCTIONS
function orderFormToggle(event){
	//Set the values to update
	orderData =[]
	$('#order-cart-modal').modal('toggle');
	cartItemList(orderData)
	return false;
}

function addOrder(event){
	var $form = $("#order-cart-form");
	var json = toJson($form);
	var url = getOrderItemUrl();
	$('#order-cart-form input[name=barcode').val('');
	$('#order-cart-form input[name=quantity').val('');
	$('#order-cart-form input[name=sellingPrice').val('');
	$.ajax({
        	   url: url,
        	   type: 'POST',
        	   data: json,
        	   headers: {
               	'Content-Type': 'application/json'
               },
        	   success: function(response) {
        	         handleSuccessMessage("Order item added");
        	        orderData.push(JSON.parse(json));
                    cartItemList(orderData);
        	   },
        	   error: handleAjaxError
        	});

    return false;
}
function editCartItem(index){
    data = orderData[index]
    $('#order-cart-form input[name=barcode').val(data.barcode);
     $('#order-cart-form input[name=quantity').val(data.quantity);
     $('#order-cart-form input[name=sellingPrice').val(data.sellingPrice);
     tempOrderData=[]
     for(var i in orderData){
        if(i==index){
        continue
        }else{
        tempOrderData.push(orderData[i]);
        }
     }
     orderData = tempOrderData
    cartItemList(orderData)
    return false;

}
function cartItemList(data){
	var $tbody = $('#order-item-table').find('tbody');
	console.log(data);
	$tbody.empty();
	for(var i in data){

		var e = data[i];
		console.log(e);
		var buttonHtml = '<button onclick="editCartItem('+ i+')">Edit</button>'
		var row = '<tr>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>' + e.quantity + '</td>'
		+ '<td>' + e.sellingPrice + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}
function placeOrder(){
	var url = getOrderUrl();
	var data = {
        order: orderData
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


function displayOrderList(data){
	var $tbody = $('#order-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var buttonHtml = '<button onclick="viewOrder(' + e.id + ')">View Order</button>'
		buttonHtml +=  '<button onclick="generateInvoice(' + e.id + ')">Generate Invoice</button>'
		buttonHtml +=  '<button onclick="downloadInvoice(' + e.id + ')">Download Invoice</button>'
		var row = '<tr>'
		+ '<td>' + e.id + '</td>'
		+ '<td>' + convertDateTime(e.dateTime) + '</td>'
		+ '<td>' + buttonHtml + '</td>'
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
    $('#create-order-item').click(orderFormToggle);
	$('#add-order').click(addOrder);
	$('#place-order').click(placeOrder);
	$('#update-order').click(updateOrder);
	$('#refresh-data').click(getOrderList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#orderFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getOrderList);

