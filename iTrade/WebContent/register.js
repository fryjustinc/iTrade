function myFunction() {
var name = document.getElementById("name").value;
var password = document.getElementById("password").value;
var passwordCheck = document.getElementById("passwordcheck").value;
var difficulty = document.getElementById("difficulty").value;
var status = document.getElementById("status");
// Returns successful data submission message when the entered information is stored in database.
var valid=true;
status.innerHTML="";
if(name==''){
	valid=false;
	status.innerHTML+="Enter a Username!<br\>"
}
if(password==''){
	valid=false;
	status.innerHTML+="Enter a Password!<br\>"
}
else if(password!=passwordCheck){
	valid=false;
	status.innerHTML+="Passwords don't match!<br\>"
}
if(valid.valueOf()==true){
	//document.getElementById("status").setAttribute("style", "background-color:red");
	var query="rest/user/"+name+"/"+password+"/"+difficulty;
	$.ajax({
		type: "GET",
		url: query,
		data: "",
		cache: false,
		success: function(html) {
		if(html==1){
			status.innerHTML="Username taken!<br\>"
		}
		else{
			
		}
		}
		});
}
return false;
}