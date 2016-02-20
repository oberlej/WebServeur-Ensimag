$(document).ready(function(){
	//clean the base first
	$.ajax({
		url : "http://localhost:8080/bmt/tata/clean?x-http-method=post",
		method : "post",
		error : function(){
			alert("Error cleaning the DB for tata");
		}
	});
	//get the tests form the tests.json file
	var tests;
	$.ajax({
		url: "./tests.json",
		dataType: 'json',
		success: function(data){
			if(data == null){
				alert("tests.json is empty!");
			}
			tests = data;
		},
		error : function(){
			alert("Error getting the tests form tests.json");
		},
		async: false
	});
	//execute each test
	$.each(tests, function(i, test){
		addTest(test);
	});
});




function addTest(test){
	var html = "<tr>";

	html += "<td>";
	html += test.title;
	html += "</td>";

	html += "<td>";
	//replace titi or toto by tata since all tests are supposed to be made on this DB
	test.url = test.url.replace("titi","tata");
	test.url = test.url.replace("toto", "tata");
	
	html += "<a href='"+test.url+"'>"+test.url+"</a>";
	html += "</td>";

	html += "<td>";
	html += test.retValue;
	html += "</td>";

	var ret;
	$.ajax({
		url : test.url,
		method : test.method,
		async: false,
		complete : function(e, xhr, settings){
			ret = e.status;
		}
	});
	if(ret == test.retValue){
		html += "<td class='ok'>";
	}else{
		html += "<td class='ko'>";
	}
	html += ret;
	html += "</td>";

	html += "</tr>";

	$("table#tests").append(html);
}
