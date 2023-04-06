
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/product";
}

//BUTTON ACTIONS
function productFormToggle(event){
	//Set the values to update
	$('#product-add-form input[name=name]').val('');
    $('#product-add-form input[name=barcode]').val('');
    $('#product-add-form input[name=brandCategory]').val('');
    $('#product-add-form input[name=brandName]').val('');
    $('#product-add-form input[name=mrp]').val('');
	$('#product-add-modal').modal('toggle');
	return false;
}
function addProduct(event){
	//Set the values to update
	var $form = $("#product-add-form");
	var json = toJson($form);
	var url = getProductUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	        handleSuccessMessage("Product added successfully");
	        productFormToggle();
	   		getProductList();

	   },
	   error: handleAjaxError
	});

	return false;
}

function updateProduct(event){
	//Get the ID
	var id = $("#product-edit-form input[name=id]").val();
	var url = getProductUrl() + "/" + id;

	//Set the values to update
	var $form = $("#product-edit-form");
	var json = toJson($form);
	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	        $("#edit-product-modal").modal('toggle');
	        handleSuccessMessage("Product updated successfully");
	   		getProductList();

	   },
	   error: handleAjaxError
	});

	return false;
}


function getProductList(){
	var url = getProductUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayProductList(data);
	   },
	   error: handleAjaxError
	});
}

function deleteProduct(id){
	var url = getProductUrl() + "/" + id;

	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   		getProductList();
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
	var file = $('#productFile')[0].files[0];
	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results){
var headings = ["barcode", "name", "brandName", "brandCategory", "mrp"]
    if(JSON.stringify(results.meta.fields.sort()) != JSON.stringify(headings.sort())){
        handleErrorMsg("Data headings are invalid");
        return;
    }
	fileData = results.data;
	uploadRows();
}

function uploadRows(){
	 var form = $('#productFile')[0].files[0];
         var data = new FormData();
         data.append("file", form);
	var url = getProductUrl()+'/upload';

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
              getProductList();
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

function displayProductList(data){
	var $tbody = $('#product-table').find('tbody');
	$tbody.empty();
	var index=0
	for(var i in data){
	    index++;
		var e = data[i];
		var buttonHtml = ' <button onclick="displayEditProduct(' + e.id + ')" class="btn btn-outline-dark custom-button edit-button mx-auto" data-toggle="tooltip" data-placement="top" title="Edit Product"><i class="material-icons">edit</i></button>'
		var row = '<tr>'
		+ '<th scope="row">' + index+ '</th>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>'  + e.brandName + '</td>'
		+ '<td>' + e.brandCategory + '</td>'
        + '<td>'  + e.name + '</td>'
        + '<td>'  + e.mrp.toFixed(2) + '</td>'
		+ '<td class="text-center role">' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
	setRole();
}

function displayEditProduct(id){
	var url = getProductUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayProduct(data);
	   },
	   error: handleAjaxError
	});	
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#productFile');
	$file.val('');
	$('#productFileName').html("Choose File");
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
	var $file = $('#productFile');
	var fileName = $file.val();
	$('#productFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog(); 	
	$('#upload-product-modal').modal('toggle');
}

function displayProduct(data){
	$("#product-edit-form input[name=brandName]").val(data.brandName);
	$("#product-edit-form input[name=brandCategory]").val(data.brandCategory);
	$("#product-edit-form input[name=barcode]").val(data.barcode);
	$("#product-edit-form input[name=mrp]").val(data.mrp.toFixed(2));
	$("#product-edit-form input[name=name]").val(data.name);
	$("#product-edit-form input[name=id]").val(data.id);
	$('#edit-product-modal').modal('toggle');
}

function setRole(){
if(getRole() === 'operator'){
    $(".role").remove();
}
}
//INITIALIZATION CODE
function init(){
    $('#add-form').click(productFormToggle);
	$('#add-product').click(addProduct);
	$('#update-product').click(updateProduct);
	$('#refresh-data').click(getProductList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#productFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getProductList);
$(document).ready(setRole);

