var tests = '['
+'{"title":"test1", "url": "http://localhost:8080/bmt/titi/bookmarks?x-http-method=get", "method": "GET", "retValue" : "204"},'
+'{"title":"test2", "url": "test2/url", "retValue" : "200"},'
+'{"title":"test insert bookmark","url":"http://localhost:8080/bmt/titi/bookmarks?x-http-method=post&json={"title":"Facebook","description":"facebook","link":"facebook.com"}","method":"POST","retValue":"201"},'
+']';


$(document).ready(function(){

	$.each(JSON.parse(tests), function(i, test){
		addTest(test);
	});
});




function addTest(test){
	var html = "<tr>";

	html += "<td>";
	html += test.title;
	html += "</td>";


	html += "<td>";
	html += test.url;
	html += "</td>";


	html += "<td>";
	html += test.retValue;
	html += "</td>";

	$.ajax({
		url : test.url,
		method : test.method
	})
	.error(function(a,b,c){
		alert();
	})
	.complete(function(ret, val, r){
		alert(ret);
		alert(val);
	});	


	html += "<td>";
	html += "</td>";

	html += "</tr>";

	$("table#tests").append(html);
}
