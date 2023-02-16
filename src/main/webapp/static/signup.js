
function getSignupUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/session/signup";
}
function getLoginUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/site/login";
}
function addUser(event){
	var $form = $("#signup-form");
	var json = toJson($form);
	var url = getSignupUrl();
    console.log(json)
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
                    $("#form").prop("hidden", true);
                    $("#success").prop("hidden", false);
                    setTimeout(function(){
                                  window.location.href = "/pos/site/login";
                        }, 3000);
                    setTimer();
	   },
	   error: handleAjaxError
	});

	return false;
}
var i=3
function setTimer(){
    if(i==0){
        return;
    }
    console.log(i)
    $("#count").text(i)
    i=i-1
    setTimeout(setTimer, 1000);
}
function getLoginPage(){
	var url = getLoginUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {

	   },
	   error: handleAjaxError
	});
}
//INITIALIZATION CODE
function init(){
$('#signup').click(addUser);
}

$(document).ready(init);


