
function getDailyReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/daily-report";
}

function getReport(event){
	var url = getDailyReportUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayDailyList(data);
	   },
	   error: handleAjaxError
	});

	return false;
}
function generateReport(event){
	//Set the values to update
	var url = getDailyReportUrl();
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: {},
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function() {
	   		getReport();
	   },
	   error: handleAjaxError
	});

	return false;
}

function convertDateTime(dateTime){
console.log(dateTime);
dateTime = dateTime.split('[')[0]
let dateString = dateTime;
let date = new Date(dateString);
let localDateTime = date.toLocaleString();
console.log(localDateTime);
return localDateTime.split(',')[0];
}

//UI DISPLAY METHODS

function displayDailyList(data){
	var $tbody = $('#daily-report-table').find('tbody');
	$tbody.empty();
	var index = 0
	for(var i in data){
		var e = data[i];
		console.log(e);
		index+=1
		var row = '<tr>'
		+ '<th scope="row">' + index + '</th>'
		+ '<td>' + convertDateTime(e.date) + '</td>'
		+ '<td>'  + e.invoiced_orders_count + '</td>'
		+ '<td>'  + e.invoiced_items_count + '</td>'
		+ '<td>'  + e.total_revenue + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

//INITIALIZATION CODE
function init(){
	$('#generate-report').click(generateReport);
	$('#refresh-report').click(getReport);
}

$(document).ready(init);
$(document).ready(getReport);


