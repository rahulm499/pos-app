
function getSalesReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/sales-report";
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

function setMinEndDate(event){
    const dateInput = document.getElementById("end-date");
    $("#end-date").val("");
    dateInput.setAttribute("min", event.target.value);
 }
 function setMaxStartDate(event){
     const dateInput = document.getElementById("start-date");
     dateInput.setAttribute("max", event.target.value);
  }
 var reportData =[]
function generateReport(event){
	//Set the values to update
	var $form = $("#sales-report-form");
	var json = toJson($form);
	var url = getSalesReportUrl();
    console.log(json)
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(data) {
	        reportData = data
	   		displaySalesList(data);
	   },
	   error: handleAjaxError
	});

	return false;
}


function downloadReport(event){
	//Set the values to update
	var json = JSON.stringify(reportData);
	var url = getSalesReportUrl() +'/download' ;
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

function displaySalesList(data){
    $("#report").show()
	var $tbody = $('#sales-report-table').find('tbody');
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
		+ '<td>'  + e.revenue + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

//INITIALIZATION CODE
function init(){
    $("#report").hide()
	$('#generate-report').click(generateReport);
	$('#start-date').change(setMinEndDate);
	$('#download-report').click(downloadReport);
}

$(document).ready(init);
$(document).ready(getBrandList);
$(document).ready(addOptionValues);

