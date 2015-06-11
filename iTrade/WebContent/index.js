var brokerId = 0;
var brokerName = "";
var selectedFund = "";
var style = "";
function ifunds() {
	style = document.getElementById("container").getAttribute("style");
	funds();
}
function funds() {
	setFolderColor("#F1AB00");
	var status = document.getElementById("brokers");
	var query = "rest/fund/all";
	ajax(query, function(html) {
		status.innerHTML = html;
	});
}

function brokers() {
	var status = document.getElementById("brokers");
	var valid = true;
	var text;
	setFolderColor("#CD1E10");
	var query = "rest/broker/all";
	ajax(query, function(html) {
		status.innerHTML = html;
	});
}

function login() {
	var shaObj = new jsSHA($("#pass").val(), "ASCII");
	var hash = shaObj.getHash("SHA-512", "HEX");
	// alert(hash);
	var query = "rest/user/" + $("#user").val() + "/" + hash;
	ajax(query, function(html) {
		location.reload();
	});
}

function logout() {
	var query = "rest/user/logout";
	ajax(query, function(html) {
		location.reload()
	});
}

function policies(id, name) {
	brokerId = id;
	brokerName = name;
	$("#label1").text("Type");
	$("#label2").text("Frequency");
	$("#label3").text("Condition");
	var query = "rest/policy/all/exclusive/" + id;

	ajax(query, function(html) {
		var status = document.getElementById("brokers");
		if ($("#balance").text() != "") {
			status.innerHTML = "<div class=\"innerbubble\"><div id=\"brokername\">"
			+ brokerName
			+ "</br><input type=\"submit\" style=\"width:100px;\"value=\"Back\" onClick=\"brokers();\"></div></br><div style=\"text-align: center;\"><div class=\"title\" id=\"curbal\"></div>"
					+ "<input type=\"text\" id=\"amt\"><input type=\"submit\" value=\"Create Account\" style=\"width:120px;\" id=\"sbalance\" onclick=\"addBroker();\"></div>"+html+"</div>";
			$("#curbal").text("Free Capital: " + $("#balance").text());
		}
		else
		status.innerHTML = "<div class=\"innerbubble\"><div id=\"brokername\">"
			+ brokerName
			+ "</br><input type=\"submit\" style=\"width:100px;\"value=\"Back\" onClick=\"brokers();\"></div></br>"+html+"</div>";
	});
}

function exclusivePolicies(id, name, balance) {
	brokerId = id;
	brokerName = name;
	brokerBalance = balance;
	$("#label1").text("Type");
	$("#label2").text("Frequency");
	$("#label3").text("Condition");
	document.getElementById("brokers").innerHTML = "<div id=\"brokername\">"
			+ brokerName
			+ "</br><input type=\"submit\" style=\"width:100px;\"value=\"Back\" onClick=\"brokers();\"></div></br>"
			+ "<div style=\"text-align: center;\"><div class=\"title\" id=\"curbal\"></div>"
			+ "<div class=\"title\">Account Balance: "
			+ balance
			+ "</div>Add <input type=\"text\" id=\"amt\">"
			+ " from <select class=\"biginput\" id=\"bselect\"><option value=\"-1\">Free Capital</option></select> <input type=\"submit\" value=\"Submit\" style=\"width:80px;\" onclick=\"transfer('#amt','#bselect');\">"
			+ "</br>Move <input type=\"text\" id=\"amtto\"> to <select class=\"biginput\" id=\"bselectto\"><option value=\"-1\">Free Capital</option></select>"
			+ " <input type=\"submit\" value=\"Submit\" style=\"width:80px;\" onclick=\"transfer('#amtto','#bselectto');\">"
			+ "</div>";
	var query = "rest/broker/dropdown"
	ajax(query, function(html) {
		$("#bselect").html($("#bselect").html() + html);
		$("#bselectto").html($("#bselectto").html() + html);
	});
	query = "rest/policy/all/exclusive/" + id;
	$("#curbal").text("Free Capital: " + $("#balance").text());
	ajax(
			query,
			function(html) {
				var status = document.getElementById("brokers");
				status.innerHTML += "<div class=\"innerbubble\" style=\"height:62%;\">"
						+ html
						+ "</br><input type=\"submit\" value=\"Close Account\" style=\"width:120px;\" onclick=\"closeAcct();\"></div>";
			});
}

function transfer(amt, id) {
	var query;
	if (amt == "#amt")
		query = "rest/user/transfer/" + $(id).val() + "/" + brokerId + "/"
				+ $(amt).val();
	else
		query = "rest/user/transfer/" + brokerId + "/" + $(id).val() + "/"
				+ $(amt).val();
	ajax(query, function(html) {
		location.reload();
	});
}

function selectFund(fund) {
	selectedFund = fund;
	var status = document.getElementById("brokers");
	var query = "rest/fund/reqoffer/" + fund;
	ajax(query, function(html) {
		status.innerHTML = html;
		query = "rest/broker/dropdown"
		ajax(query, function(html) {
			$("#bselect").html(html);
		});
	});
}

function submitOffer() {
	var broker = $("#bselect").val();
	var type = $("#btype").val();
	var price = $("#sprice").text();
	var shares = $("#shares").val();
	var query = "rest/offer/" + price + "/" + type + "/" + shares + "/"
			+ broker + "/" + selectedFund;
	ajax(query, function(html) {
		location.reload();
	});
}

function addBroker() {
	var query = "rest/broker/add/" + brokerId + "/" + $("#amt").val();
	ajax(query, function(html) {
		location.reload();
	});
}

function closeAcct() {
	var query = "rest/broker/close/" + brokerId;
	ajax(query, function(html) {
		location.reload();
	});
}

function adjustEstimate() {
	$("#eprice").text(
			"Estimated price: " + $("#shares").val() * $("#sprice").text());
}

function userData() {
	var status = document.getElementById("brokers");
	setFolderColor("#FADF00");
	status.innerHTML = "";
	var query = "rest/fund/transactions";
	ajax(query, function(html) {
		status.innerHTML = html;
	});
}
function equity() {
	setFolderColor("#007E3A");
	var status = document.getElementById("brokers");
	var query = "rest/fund/equity";
	ajax(query, function(html) {
		status.innerHTML = html;
	});
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

function setFolderColor(color) {
	document.getElementById("container").setAttribute("style",
			style + "background-color:" + color);
}

function brokerSelect(value) {
	alert($("#b" + value).val() + value)
}
function transactions() {
	var b = $("#transb").val();
	var status = $("#brokers");
	var query = "rest/fund/transactions/" + b;
	ajax(query, function(html) {
		status.html(html);
	});
}

function register() {
	var name = document.getElementById("name").value;
	var password = document.getElementById("password").value;
	var passwordCheck = document.getElementById("passwordcheck").value;
	var difficulty = document.getElementById("difficulty").value;
	var status = document.getElementById("status");
	var valid = true;
	var shaObj = new jsSHA($("#password").val(), "ASCII");
	var hash = shaObj.getHash("SHA-512", "HEX");
	status.innerHTML = "";
	if (name == '') {
		valid = false;
		status.innerHTML += "Enter a Username!<br\>"
	}
	if (password == '') {
		valid = false;
		status.innerHTML += "Enter a Password!<br\>"
	} else if (password != passwordCheck) {
		valid = false;
		status.innerHTML += "Passwords don't match!<br\>"
	}
	if (valid.valueOf() == true) {
		// document.getElementById("status").setAttribute("style",
		// "background-color:red");
		var query = "rest/user/" + name + "/" + hash + "/" + difficulty;
		ajax(query, function(html) {
			if (html == 1) {
				status.innerHTML = "Username taken!<br\>"
			} else {
				location.reload();
			}
		});
	}
	return false;
}