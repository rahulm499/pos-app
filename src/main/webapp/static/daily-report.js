
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


function downloadReport(event){
	//Set the values to update

	var url = getDailyReportUrl() +'/download' ;
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: {},
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
		+ '<td>'  + e.total_revenue.toFixed(2) + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

//INITIALIZATION CODE
function init(){
	$('#generate-report').click(generateReport);
	$('#download-report').click(downloadReport);
}

$(document).ready(init);
$(document).ready(getReport);


