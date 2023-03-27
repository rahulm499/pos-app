
function init(){
if($('#info').text() != ""){
    handleErrorMsg($('#info').text());
    $('#info').text("")
}
}

$(document).ready(init);


