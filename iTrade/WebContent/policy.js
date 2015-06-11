function myFunction() {
var name = document.getElementById("name").value;
var limit = document.getElementById("limit").value;
var time = document.getElementById("time").value;
var status = document.getElementById("status");
// Returns successful data submission message when the entered information is stored in database.
var valid=true;
status.innerHTML="";
if(name==''){
	valid=false;
	status.innerHTML+="Enter a policy type!<br\>";
}
if(limit==''){
	valid=false;
	status.innerHTML+="Enter a frequency!<br\>";
}
if(time==''){
	time="";
}
alert(time)
if(valid.valueOf()==true){
	//document.getElementById("status").setAttribute("style", "background-color:red");
	var query="rest/policy/"+name+"/"+limit+"/"+time;
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