
function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand";
}

function brandFormToggle(event){
	//Set the values to update
	$('#brand-add-form input[name=brand]').val('');
    $('#brand-add-form input[name=category]').val('');
	$('#brand-add-modal').modal('toggle');
	return false;
}

//BUTTON ACTIONS
function addBrand(event){
	//Set the values to update
	var $form = $("#brand-add-form");
	var json = toJson($form);
	var url = getBrandUrl();

    var brand = $('#brand-add-form input[name=brand]').val();
    var category = $('#brand-add-form input[name=category]').val();

    if (brand === null || brand.trim() === ""){
            handleErrorMsg("Brand cannot be empty")
            return;
        }
        if (category === null || category.trim() === ""){
                handleErrorMsg("Category cannot be empty")
                return;
            }

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	        handleSuccessMessage("Brand added successfully");
	        brandFormToggle();
	   		getBrandList();
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateBrand(event){

	//Get the ID
	var id = $("#brand-edit-form input[name=id]").val();
	var url = getBrandUrl() + "/" + id;

	//Set the values to update
	var $form = $("#brand-edit-form");
	var json = toJson($form);

	var brand = $('#brand-edit-form input[name=brand]').val();
    var category = $('#brand-edit-form input[name=category]').val();
    if (brand.trim() === ""){
            console.log(brand)
            handleErrorMsg("Brand cannot be empty")
            return;
        }
        if (category.trim() === ""){
                handleErrorMsg("Category cannot be empty")
                return;
            }

    delete json.id;
	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	         handleSuccessMessage("Brand updated successfully");
	        $('#edit-brand-modal').modal('toggle');
	   		getBrandList();

	   },
	   error: handleAjaxError
	});

	return false;
}


function getBrandList(){
	var url = getBrandUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrandList(data);  
	   },
	   error: handleAjaxError
	});
}

function deleteBrand(id){
	var url = getBrandUrl() + "/" + id;

	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   		getBrandList();  
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
    var file = $('#brandFile')[0].files[0];
    readFileData(file, readFileDataCallback);

}

function readFileDataCallback(results){
    var headings = ["brand", "category"]
    if(JSON.stringify(results.meta.fields.sort()) != JSON.stringify(headings.sort())){
        handleErrorMsg("Data headings are invalid");
        return;
    }
	fileData = results.data;
    uploadRows();
}

function uploadRows(){
	//Update progress
	 var form = $('#brandFile')[0].files[0];
     var data = new FormData();
     data.append("file", form);
	var url = getBrandUrl() + '/upload';
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
          getBrandList();
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

function displayBrandList(data){
	var $tbody = $('#brand-table').find('tbody');
	$tbody.empty();
	var index=0
	for(var i in data){
		var e = data[i];
		index++;
		var buttonHtml = '<button onclick="displayEditBrand(' + e.id + ')" class="btn btn-outline-dark custom-button edit-button mx-auto" data-toggle="tooltip" data-placement="top" title="Edit Brand"><i class="material-icons">edit</i></button>'
		var row = '<tr>'
		+ '<th scope="row">' + index + '</th>'
		+ '<td>' + e.brand + '</td>'
		+ '<td>'  + e.category + '</td>'
		+ '<td class="text-center role">' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
	setRole()
}

function displayEditBrand(id){
	var url = getBrandUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrand(data);   
	   },
	   error: handleAjaxError
	});	
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#brandFile');
	$file.val('');
	$('#brandFileName').html("Choose File");
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
	var $file = $('#brandFile');
	var fileName = $file.val();
	$('#brandFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-brand-modal').modal('toggle');
}

function displayBrand(data){
	$("#brand-edit-form input[name=brand]").val(data.brand);
	$("#brand-edit-form input[name=category]").val(data.category);
	$("#brand-edit-form input[name=id]").val(data.id);
	$('#edit-brand-modal').modal('toggle');
}
function setRole(){
if(getRole() === 'operator'){
    $(".role").remove();
}
}

//INITIALIZATION CODE
function init(){
$('#add-form').click(brandFormToggle);
	$('#add-brand').click(addBrand);
	$('#update-brand').click(updateBrand);
	$('#refresh-data').click(getBrandList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#brandFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getBrandList);
$(document).ready(setRole)

