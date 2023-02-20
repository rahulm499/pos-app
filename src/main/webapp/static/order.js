
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
function findOrderItem(data){
     console.log(data);
    for(var i in orderData){
        if(data.barcode == orderData[i].barcode){
            return i;
        }
    }
    return -1;
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
	$.ajax({
        	   url: url,
        	   type: 'POST',
        	   data: JSON.stringify(jsonParseData),
        	   headers: {
               	'Content-Type': 'application/json'
               },
        	   success: function(response) {
                    $('#order-cart-form input[name=barcode').val('');
                    $('#order-cart-form input[name=quantity').val('');
                    $('#order-cart-form input[name=sellingPrice').val('');
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
function editCartItem(index){
    data = orderData[index]
    $('#order-item-edit-form input[name=barcode]').val(data.barcode);
     $('#order-item-edit-form input[name=quantity]').val(data.quantity);
     $('#order-item-edit-form input[name=sellingPrice]').val(data.sellingPrice);
     $('#order-item-edit-form input[name=index]').val(index);
     $("#order-item-edit-modal").modal('toggle');
      var $form = $("#order-item-edit-form");
         console.log($form)
//     $("#order-cart-modal").modal('toggle');
     console.log("hello2")
}

function updateOrderItem(){
    console.log("hello")
    var $form = $("#order-item-edit-form");
    console.log($form)
    var index = $('#order-item-edit-form input[name=index]').val();
    	var json = toJson($form);
    	var url = getOrderItemUrl();
    	var jsonParseData = JSON.parse(json);
    	console.log(jsonParseData)
    	$.ajax({
            	   url: url,
            	   type: 'POST',
            	   data: json,
            	   headers: {
                   	'Content-Type': 'application/json'
                   },
            	   success: function(response) {
                        $('#order-item-edit-form input[name=barcode]').val('');
                            $('#order-item-edit-form input[name=quantity]').val('');
                            $('#order-item-edit-form input[name=sellingPrice]').val('');
                            $('#order-item-edit-form input[name=index]').val('');
                            $("#order-item-edit-modal").modal('toggle');
//                            $("#order-cart-modal").modal('toggle');
            	        handleSuccessMessage("Order item updated");
            	        orderData[index] = jsonParseData;
                        cartItemList(orderData);
            	   },
            	   error: handleAjaxError
            	});

        return false;
}
function cartItemList(data){
	var $tbody = $('#order-item-table').find('tbody');
	console.log(data);
	$tbody.empty();
	for(var i in data){

		var e = data[i];
		console.log(e);
		var buttonHtml = '<button onclick="editCartItem('+ i +')" class="btn btn-dark custom-button mx-auto"><i class="material-icons">edit</i>Edit</button>'
		var row = '<tr>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>' + e.quantity + '</td>'
		+ '<td>' + e.sellingPrice + '</td>'
		+ '<td class="text-center">' + buttonHtml + '</td>'
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
		var buttonHtml = '<div class="d-flex flex-row justify-content-center">'
		buttonHtml += '<button onclick="viewOrder(' + e.id + ')" class="btn btn-dark custom-button mr-2"><i class="material-icons">visibility</i>View</button>'
		if(!e.isInvoiceGenerated){
		buttonHtml += '<button onclick="viewOrder(' + e.id + ')" class="btn btn-dark custom-button"><i class="material-icons">edit</i>Edit</button>'
		var invoice  =  '<button onclick="generateInvoice(' + e.id + ')" class="btn btn-dark custom-button mx-auto"><i class="material-icons">description</i>Generate Invoice</button>'}
		else{
		buttonHtml += '<button onclick="viewOrder(' + e.id + ')" class="btn btn-dark custom-button" disabled><i class="material-icons">edit</i>Edit</button>'
		var invoice  =  '<button onclick="downloadInvoice(' + e.id + ')" class="btn btn-dark custom-button mx-auto"><i class="material-icons">file_download</i>Download Invoice</button>'
		}
		buttonHtml+='</div>'
		var row = '<tr>'
		+ '<th scope="row">' + e.id + '</th>'
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
    $('#create-order-item').click(orderFormToggle);
	$('#add-order').click(addOrder);
	$('#place-order').click(placeOrder);
//	$('#update-order').click(updateOrder);
//	$('#refresh-data').click(getOrderList);
    $('#update-order-item').click(updateOrderItem);
}

$(document).ready(init);
$(document).ready(getOrderList);

