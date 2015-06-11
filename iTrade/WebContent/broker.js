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
	status.innerHTML+="Enter a Name!<br\>"
}
if(limit==''||limit<10){
	limit=10;
}
if(time==''||time<10){
	time=10;
}
if(valid.valueOf()==true){
	//document.getElementById("status").setAttribute("style", "background-color:red");
	var query="rest/broker/"+name+"/"+limit+"/"+time;
	ajax(query,function(html) {
		if(html==1){
			status.innerHTML="Username taken!<br\>"
		}
		else{
			
		}
		});
}
return false;
}

function ajax(query, callback) {
	$.ajax({
		type : "GET",
		url : query,
		data : "",
		cache : false,
		success : function(html) {
			callback(html);
		}
	});
}