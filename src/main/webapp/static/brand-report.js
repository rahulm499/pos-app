
function getBrandReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand-report";
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

//BUTTON ACTIONS
function generateReport(event){
	//Set the values to update
	var $form = $("#brand-form");
	var json = toJson($form);
	var url = getBrandReportUrl();
    console.log(json)
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(data) {
	   		displayBrandReportList(data);
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

function displayBrandReportList(data){
    $("#report").show()
	var $tbody = $('#brand-report-table').find('tbody');
	var index=0
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		console.log("DISPLAY",e)
        index += 1;
		var row = '<tr>'
		+ '<th scope="row">' + index + '</th>'
		+ '<td>' + e.brand + '</td>'
		+ '<td>'  + e.category + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

//INITIALIZATION CODE
function init(){
    $("#report").hide()
	$('#generate-report').click(generateReport);
}

$(document).ready(init);
$(document).ready(getBrandList);
$(document).ready(addOptionValues);

