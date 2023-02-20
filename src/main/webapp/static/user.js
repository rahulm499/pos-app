
function getUserUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/admin/user";
}
function userFormToggle(event){
	//Set the values to update
	$('#user-add-form input[name=email').val('');
    $('#user-add-form input[name=password').val('');
    $('#user-add-form input[name=role').val('');
	$('#user-add-modal').modal('toggle');
	return false;
}
//BUTTON ACTIONS
function addUser(event){
	//Set the values to update
	var $form = $("#user-add-form");
	var json = toJson($form);
	var url = getUserUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	        $('#user-add-modal').modal('toggle');
	        handleSuccessMessage("User added successfully");
	   		getUserList();    
	   },
	   error: handleAjaxError
	});

	return false;
}

function editUser(event){
	//Set the values to update
	var $form = $("#user-edit-form");
	var id = $("#user-edit-form input[name=id]").val();
	var json = toJson($form);
	var url = getUserUrl()+'/'+id;
	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	        $('#user-edit-modal').modal('toggle');
	        handleSuccessMessage("User updated successfully");
	   		getUserList();
	   },
	   error: handleAjaxError
	});

	return false;
}

function getUserList(){
	var url = getUserUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayUserList(data);
	   },
	   error: handleAjaxError
	});
}

function deleteUser(){
    var id = $("#user-delete-form input[name=id]").val();
	var url = getUserUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	        handleSuccessMessage("User deleted")
	        $('#user-delete-modal').modal('toggle');
	   		getUserList();    
	   },
	   error: handleAjaxError
	});
}
function deleteUserModal(id){
	$('#user-delete-form input[name=id]').val(id);
    $('#user-delete-modal').modal('toggle');
}
//UI DISPLAY METHODS
function displayUser(data){
    $('#user-edit-form input[name=email]').val(data.email);
        $('#user-edit-form input[name=id]').val(data.id);
        $('#user-edit-modal').modal('toggle');
}
function displayUserList(data){
	console.log('Printing user data');
	var $tbody = $('#user-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
        var buttonHtml = '<div class="d-flex flex-row justify-content-center">'
		buttonHtml += ' <button onclick="displayEditUser(' + e.id + ')" class="btn btn-dark custom-button mr-1"><i class="material-icons">edit</i>Edit</button>'
		buttonHtml += '<button onclick="deleteUserModal(' + e.id + ')" class="btn btn-dark custom-button"><i class="material-icons">delete</i>Delete</button>'
		buttonHtml += '</div>'
		const capitalizedStr = e.role.charAt(0).toUpperCase() + e.role.slice(1);
		var row = '<tr>'
		+ '<td scope="row">' + e.id + '</td>'
		+ '<td>' + e.email + '</td>'
		+ '<td>' + capitalizedStr + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}
function displayEditUser(id){
	var url = getUserUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayUser(data);
	   },
	   error: handleAjaxError
	});
}

//INITIALIZATION CODE
function init(){
$('#add-form').click(userFormToggle);
	$('#add-user').click(addUser);
	$('#refresh-data').click(getUserList);
	$('#edit-user').click(editUser);
	$('#delete-user').click(deleteUser)

}

$(document).ready(init);
$(document).ready(getUserList);

