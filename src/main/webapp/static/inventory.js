
function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}
function inventoryFormToggle(event){
	//Set the values to update
	$('#inventory-add-form input[name=quantity]').val('');
    $('#inventory-add-form input[name=barcode]').val('');
	$('#inventory-add-modal').modal('toggle');
	return false;
}
//BUTTON ACTIONS
function addInventory(event){
	//Set the values to update
	var $form = $("#inventory-add-form");
	var json = toJson($form);
	var url = getInventoryUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	        handleSuccessMessage("Inventory added successfully");
	        inventoryFormToggle();
	   		getInventoryList();  
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateInventory(event){
	//Get the ID
	var id = $("#inventory-edit-form input[name=id]").val();
	var url = getInventoryUrl() + "/" + id;

	//Set the values to update
	var $form = $("#inventory-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	        handleSuccessMessage("Inventory updated successfully");
	        $('#edit-inventory-modal').modal('toggle');
	   		getInventoryList();   
	   },
	   error: handleAjaxError
	});

	return false;
}


function getInventoryList(){
	var url = getInventoryUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayInventoryList(data);  
	   },
	   error: handleAjaxError
	});
}

function deleteInventory(id){
	var url = getInventoryUrl() + "/" + id;

	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   		getInventoryList();  
	   },
	   error: handleAjaxError
	});
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var errorCount = 0;
var processCount = 0;
var successCount = 0;

function processData(){
	var file = $('#inventoryFile')[0].files[0];
	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results){
var headings = ["barcode", "quantity"]
    if(JSON.stringify(results.meta.fields.sort()) != JSON.stringify(headings.sort())){
        handleErrorMsg("Data headings are invalid");
        return;
    }
	fileData = results.data;
	uploadRows();
}

function uploadRows(){
	//Update progress
var form = $('#inventoryFile')[0].files[0];
     var data = new FormData();
     data.append("file", form);
	var url = getInventoryUrl()+'/upload';

	//Make ajax call
	$.ajax({
          url: url,
          type: 'POST',
          data: data,
          enctype: 'multipart/form-data',
          processData: false, //prevent jQuery from automatically transforming the data into a query string
          contentType: false,
          cache: false,
          xhrFields: {
            responseType: 'blob'
          },
          success: function(response) {
            // Parse TSV data from the response
            errorData = response;
            var reader = new FileReader();
            reader.onload = function() {
              var data = new TextDecoder("utf-8").decode(new Uint8Array(reader.result));

              // Split TSV data into rows
              var rows = data.split("\n");

              // Remove empty rows
              rows = rows.filter(function(row) {
                return row.trim().length > 0;
              });

              // Log the number of rows
              console.log("Number of rows: " + rows.length);
              if(rows.length!=0){
                errorCount = rows.length -1;
                processCount = fileData.length;
                successCount = processCount - errorCount;}else{
                processCount = fileData.length;
                successCount = processCount
                }
              updateUploadDialog();
                      getInventoryList();
            };
            reader.readAsArrayBuffer(response);

            // Rest of success callback function

            handleSuccessMessage("File uploaded successfully");
           if(errorData.size != 0){
            var a = document.createElement('a');
            var blob = new Blob([response], {type: "text/tsv"});
            var url = URL.createObjectURL(blob);
            a.href = url;
            a.download = 'data.tsv';
            document.body.append(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);}

          },
          error: function(response) {
            console.log(response);
            handleErrorMsg("Unable to process file");
            errorCount = fileData.length;
          }
        });

}

function downloadErrors(){
	var a = document.createElement('a');
                var blob = new Blob([errorData], {type: "text/tsv"});
                var url = URL.createObjectURL(blob);
                a.href = url;
                a.download = 'data.tsv';
                document.body.append(a);
                a.click();
                document.body.removeChild(a);
                URL.revokeObjectURL(url);
}

//UI DISPLAY METHODS

function displayInventoryList(data){
	var $tbody = $('#inventory-table').find('tbody');
	$tbody.empty();
	var index =0;
	for(var i in data){
	    index++
		var e = data[i];
		var buttonHtml = ' <button onclick="displayEditInventory(' + e.id + ')" class="btn btn-outline-dark custom-button edit-button mx-auto" data-toggle="tooltip" data-placement="top" title="Edit Inventory"><i class="material-icons">edit</i></button>'
		var row = '<tr>'
		+ '<th scope="row">' + index + '</th>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>'  + e.quantity + '</td>'
		+ '<td class="text-center role">' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
	setRole();
}

function displayEditInventory(id){
	var url = getInventoryUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayInventory(data);   
	   },
	   error: handleAjaxError
	});	
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#inventoryFile');
	$file.val('');
	$('#inventoryFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
    	errorCount = 0;
    	successCount = 0;
    	fileData = [];
    	errorData = [];
	//Update counts	
	updateUploadDialog();
}

function updateUploadDialog(){
	$('#successCount').html("" + successCount);
    	$('#processCount').html("" + processCount);
    	$('#errorCount').html("" + errorCount);
}

function updateFileName(){
	var $file = $('#inventoryFile');
	var fileName = $file.val();
	$('#inventoryFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog(); 	
	$('#upload-inventory-modal').modal('toggle');
}

function displayInventory(data){
	$("#inventory-edit-form input[name=barcode]").val(data.barcode);
	$("#inventory-edit-form input[name=quantity]").val(data.quantity);
	$("#inventory-edit-form input[name=id]").val(data.id);
	$('#edit-inventory-modal').modal('toggle');
}
function setRole(){
if(getRole() === 'operator'){
    $(".role").remove();
}
}

//INITIALIZATION CODE
function init(){
    $('#add-form').click(inventoryFormToggle);
	$('#add-inventory').click(addInventory);
	$('#update-inventory').click(updateInventory);
	$('#refresh-data').click(getInventoryList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#inventoryFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getInventoryList);
$(document).ready(setRole);



