function getRole(){
	return $("meta[name=role]").attr("content")
}
//HELPER METHOD
function toJson($form){
    var serialized = $form.serializeArray();
    console.log(serialized);
    var s = '';
    var data = {};
    for(s in serialized){
        data[serialized[s]['name']] = serialized[s]['value']
    }
    var json = JSON.stringify(data);
    return json;
}
function handleSuccessMessage(response){
	toastr.options = {
        "closeButton": true,
        "debug": false,
        "newestOnTop": true,
        "progressBar": false,
        "positionClass": "toast-top-right",
        "preventDuplicates": false,
        "onclick": null,
        "showDuration": "200",
        "hideDuration": "200",
        "timeOut": "3000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    }
	toastr["success"](response, "Success:");
	return;
}
function handleErrorMsg(response){
	toastr.options = {
      "closeButton": true,
        "debug": false,
        "newestOnTop": true,
        "progressBar": false,
        "positionClass": "toast-top-right",
        "preventDuplicates": false,
        "onclick": null,
        "showDuration": "200",
        "hideDuration": "200",
        "timeOut": "3000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    }
	toastr["error"](response, "Error:");
}
function handleAjaxError(response){
	var response = JSON.parse(response.responseText);
	handleErrorMsg(response.message)
}

function readFileData(file, callback){
	var config = {
		header: true,
		delimiter: "\t",
		skipEmptyLines: "greedy",
		complete: function(results) {
			callback(results);
	  	}	
	}
	Papa.parse(file, config);
}


function writeFileData(arr){
	var config = {
		quoteChar: '',
		escapeChar: '',
		delimiter: "\t"
	};
	
	var data = Papa.unparse(arr, config);
    var blob = new Blob([data], {type: 'text/tsv;charset=utf-8;'});
    var fileUrl =  null;

    if (navigator.msSaveBlob) {
        fileUrl = navigator.msSaveBlob(blob, 'download.tsv');
    } else {
        fileUrl = window.URL.createObjectURL(blob);
    }
    var tempLink = document.createElement('a');
    tempLink.href = fileUrl;
    tempLink.setAttribute('download', 'download.tsv');
    tempLink.click(); 
}

function setActive(){
var navLinks = document.querySelectorAll('.nav-link');

// Get the current URL
var currentURL = window.location.href;

// Loop through all the navbar links
for (var i = 0; i < navLinks.length; i++) {
  var link = navLinks[i];
  // If the link URL matches the current URL
  if (link.href === currentURL) {
    // Add the active class to the link
    link.classList.add('active');
    link.style.borderBottom = "2px solid #f16366";
  }
}
}
function logoutRedirect(){
localStorage.setItem('toastrMessage', 'User logged out successfully');
}
$(document).ready(setActive());
$(document).ready(()=>{
    var toastrMessage = localStorage.getItem('toastrMessage');
    if (toastrMessage) {
      // Display the Toastr notification
      handleSuccessMessage(toastrMessage);
      // Remove the message from the localStorage object
      localStorage.removeItem('toastrMessage');
    }
    console.log(getRole())
    if(getRole()==='supervisor'){
        $("#admin").prop("hidden", false)
    }
    if(getRole()==='operator'){
            $(".role").remove()
        }
})
