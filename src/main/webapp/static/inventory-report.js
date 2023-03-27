
function getInventoryReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory-report";
}
function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand";
}
const brandSet = new Set()
const categorySet = new Set()
function getBrandList(){
    var url = getBrandUrl();
    $.ajax({
    	   url: url,
    	   type: 'GET',
    	   success: function(data) {
    	   		createSet(data);
    	   },
    	   error: handleAjaxError
    	});
}

function createSet(data){
    for(var i in data){
        brandSet.add(data[i].brand);
        categorySet.add(data[i].category);
    }
    console.log(brandSet)
    console.log(categorySet)
    addOptionValues();

}
var reportData =[]
//BUTTON ACTIONS
function generateReport(event){
	//Set the values to update
	var $form = $("#inventory-report-form");
	var json = toJson($form);
	var url = getInventoryReportUrl();
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(data) {
	        reportData = data
	   		displayInventoryList(data);
	   },
	   error: handleAjaxError
	});

	return false;
}

function downloadReport(event){
	var $form = $("#inventory-report-form");
    	var json = toJson($form);
    	var url = getInventoryReportUrl() +'/download' ;
    	$.ajax({
    	   url: url,
    	   type: 'POST',
    	   data: json,
    	   headers: {
           	'Content-Type': 'application/json'
           },
            success: function(data) {
                    var a = document.createElement('a');
                    var blob = new Blob([data], {type: "text/plain"});
                    var url = URL.createObjectURL(blob);
                    a.href = url;
                    a.download = 'data.csv';
                    document.body.append(a);
                    a.click();
                    document.body.removeChild(a);
                    URL.revokeObjectURL(url);
           },
           error: handleAjaxError
    	});

	return false;
}

function addOptionValues(){
    brandSet.forEach(key => {
      $('#inputBrand').append('<option value="' + key + '">' + key + '</option>');
    })


    categorySet.forEach(key => {
        $('#inputCategory').append('<option value="' + key + '">' + key + '</option>');
     })
}


//UI DISPLAY METHODS

function displayInventoryList(data){
    $("#report").show()
	var $tbody = $('#inventory-report-table').find('tbody');
	$tbody.empty();
	var index = 0
	for(var i in data){
		var e = data[i];
		index+=1
		var row = '<tr>'
		+ '<th scope="row">' + index + '</th>'
		+ '<td>' + e.brand + '</td>'
		+ '<td>'  + e.category + '</td>'
		+ '<td>'  + e.quantity + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

//INITIALIZATION CODE
function init(){
    $("#report").hide()
	$('#generate-report').click(generateReport);
	$('#download-report').click(downloadReport);
}

$(document).ready(init);
$(document).ready(getBrandList);
$(document).ready(addOptionValues);

