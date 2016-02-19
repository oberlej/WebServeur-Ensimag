/* Base URL of the web-service for the current user */
var wsBase = 'http://localhost:8080/bmt/chenalvi-oberlej/'
/* Shows the identity of the current user */
function setIdentity() {
	$("span.identity").prepend(wsBase.split("/")[4].replace("-"," & "));
}

/* Sets the height of <div id="#contents"> to benefit from all the remaining place on the page */
function setContentHeight() {
	$(window).on("resize load", function () {
        $("#contents").height($(window).height() - $("#contents").offset().top - 35);
    });
}

/* Selects a new object type : either "bookmarks" or "tags" */
function selectObjectType(type) {
	var selected = $("#menu .selected");
	if(!$(selected).hasClass(type)){
		if(type == "bookmarks"){
			listBookmarks();
			$("#add div.tag").removeClass("selected");
		}else if(type == "tags"){
			listTags();
			$("#add div.tag").addClass("selected");
		}else{
			alert("error selectObjectType");
		}

		$(selected).removeClass("selected");
		$("#menu ."+type).addClass("selected");
	}
}

/* Loads the list of all bookmarks and displays them */
function listBookmarks() {
	$.getJSON(wsBase+"bookmarks").done(function(data){
		//Empty bookmark list
		$("#items").empty();
		$(data).each(function(index, bookmark){

			var newBookmark = $("div.model.bookmark").clone();
			//Fill datas
			$(newBookmark).children("h2").html(bookmark["title"])

			$(newBookmark).children("a").html(bookmark["link"])
																	.attr("href",""+bookmark["link"]);

			$(newBookmark).children("div.description").html(bookmark["description"]);

			$(bookmark["tags"]).each(function(index, tag){
				var newLi = $("<li>").attr("data-id", tag["id"]).html(tag["name"]);
				$(newBookmark).children("ul.tags").append(newLi);
			});

			$(newBookmark).attr("num",bookmark["id"]);
			$(newBookmark).toggleClass("model item");
			$("#items").append(newBookmark);
		});

		//Add bookmark form
		var form = $('<form>').addClass("addBookmark").css("padding","10px").css("border","2px solid blue");
		form.append($("<h2>Add a bookmark</h2>"));
		form.append('Title : ');
		form.append($('<input>').attr({type: 'text', name:"newTitle", value:$(this).children("h2").html()}));
		form.append('Link : ');
		form.append($('<input>').attr({type: 'text', name:"newLink", value:$(this).children("a").html()}));
		form.append('Description : ');
		form.append($('<input>').attr({type: 'text', name:"newDescription", value:$(this).children("div.description").html()}));
		form.append($('<br>'));

		var divCheckboxes = $("<div>").addClass("divTags").css("border","1px solid black");
		$("div.bookmark.selected ul.tags li").each(function(index, tag){
		  $(divCheckboxes).append($("<input type='checkbox' name='tags' data='"+$(tag).html()+"' data-id='"+$(tag).attr("data-id")+"' checked='true'>"));
		  $(divCheckboxes).append(''+tag.innerHTML);
		});

		//Create available tags list
		var availableTags = $("<select>");
		//List all available tags
		$.getJSON(wsBase+"tags",function(tags){
		  $(tags).each(function(i,tag){
		    var option = $("<option>").attr({value:tag.id}).html(tag.name);
		    availableTags.append(option);
		    availableTags.append($("<br>"));
		  });
		});

		divCheckboxes.append($("<br>"));
		divCheckboxes.append(availableTags);
		divCheckboxes.append($("<input>").attr({type:"button", value: "Ajouter"}).click(function(e){
		  var selected = $("div.divTags select :selected");

		  //Add correspondind checkbox
		  $("form.addBookmark div.divTags").prepend($("<br>"));
		  $("form.addBookmark div.divTags").prepend($(selected).html());
		  var newCheckBox = $("<input type='checkbox' name='tags' data='"+$(selected).html()+"' data-id='"+$(selected).attr('value')+"' checked='true'></input>");
		  $("form.addBookmark div.divTags").prepend(newCheckBox);

		  //Remove the corresponding option
		  $("form.addBookmark div.divTags select :selected").remove();

			if($("form.addBookmark div.divTags select :selected").length == 0){
				$("form.addBookmark div.divTags input[type='button']").prop('disabled', true);
				return;
			}
		}));

		form.append(divCheckboxes);
		form.append($('<button>').attr('type', 'button').html("Add bookmark").click(addBookmark));
		$(form).children("input[type=text]").css("margin","0px 20px 20px 0px");
		$("#items").append(form);
	});
}

/* Loads the list of all tags and displays them */
function listTags() {
	$("#items").empty();

	$.getJSON(wsBase+"tags",function(tags){
		$(tags).each(function(i,tag){
			var div = $("div .model.tag").clone();
			$(div).children("h2").html(tag.name);
			$(div).toggleClass("model item");
			$(div).attr("num",tag.id);
			$("#items").append(div);
		});
	});
}

/* Adds a new tag */
function addTag() {
	var newTag = $("input[name=name]").val();
	if(!newTag){
		alert("The input field is empty !");
		return;
	}
	$("#addTag").siblings("input[type='text']").val("");

	$.ajax({
		url : wsBase+"tags?x-http-method=post&json={'name':"+newTag+"}",
		method : "POST"
	}).complete(listTags);
}

/* Handles the click on a tag */
function clickTag() {
	if(!$(this).hasClass("selected")){
		$("#items .item.tag.selected h2").show();
		$("#items .item.tag.selected form").remove();
		$("#items .item.tag.selected").removeClass("selected");

		$(this).addClass("selected");
		$(this).children("h2").hide();

		var form = $('<form>');
		form.append($('<input>').attr({type: 'text', name:"newTag", value:$(this).children("h2").html()}));
		form.append($('<button>').attr('type', 'button').html("Modify name").click(modifyTag));
		form.append($('<button>').attr('type', 'button').html("Remove tag").click(removeTag));

		$(this).append(form);
	}
}

/* Performs the modification of a tag */
function modifyTag() {
	var tag = $("#contents .selected");
	var json = {
		'id'  : $(tag).attr("num"),
		'name': $("input[name=newTag]").val()
	};

	$.ajax({
		url : wsBase+"tags/"+$(tag).attr("num")+"?x-http-method=put&json="+JSON.stringify(json,null,2),
		method : "PUT"
	}).complete(listTags);
}

/* Removes a tag */
function removeTag() {
	var tagToRemove = $("#contents .selected");
	$.ajax({
		url : wsBase+"tags/"+$(tagToRemove).attr("num")+"?x-http-method=delete",
		method: "DELETE"
	}).complete(listTags);
}

/* Handles the click on a tag */
function clickBookmark() {
	if(!$(this).hasClass("selected")){
		$("#items .item.bookmark.selected").children().show();
		$("#items .item.bookmark.selected form").remove();
		$("#items .item.bookmark.selected").removeClass("selected");

		$(this).addClass("selected");
		$(this).children().hide();

		var form = $('<form>').addClass("modifyBookmark").css("padding","10px");
		form.append('Title : ');
		form.append($('<input>').attr({type: 'text', name:"newTitle", value:$(this).children("h2").html()}));
		form.append('Link : ');
		form.append($('<input>').attr({type: 'text', name:"newLink", value:$(this).children("a").html()}));
		form.append('Description : ');
		form.append($('<input>').attr({type: 'text', name:"newDescription", value:$(this).children("div.description").html()}));
		form.append($('<br>'));

		var divCheckboxes = $("<div>").addClass("divTags").css("border","1px solid black");
		$("div.bookmark.selected ul.tags li").each(function(index, tag){
			$(divCheckboxes).append($("<input type='checkbox' name='tags' data='"+$(tag).html()+"' data-id='"+$(tag).attr("data-id")+"' checked='true'>"));
			$(divCheckboxes).append(''+tag.innerHTML);
		});

		//Create available tags list
		var availableTags = $("<select>");

		$.getJSON(wsBase+"tags",function(tags){
			$(tags).each(function(i,tag){
				if ($("div.divTags input[data-id='"+tag.id+"'][data='"+tag.name+"']").length == 0) {
					var option = $("<option>").attr({value:tag.id}).html(tag.name);
					availableTags.append(option);
					availableTags.append($("<br>"));
				}
			});
		});

		divCheckboxes.append($("<br>"));
		divCheckboxes.append(availableTags);
		divCheckboxes.append($("<input>").attr({type:"button", value: "Ajouter"}).click(function(e){
			var selected = $("div.divTags select :selected");

			//Add correspondind checkbox
			$("div.divTags").prepend($("<br>"));
			$("div.divTags").prepend($(selected).html());
			var newCheckBox = $("<input type='checkbox' name='tags' data='"+$(selected).html()+"' data-id='"+$(selected).attr('value')+"' checked='true'></input>");
			$("div.divTags").prepend(newCheckBox);

			//Remove the corresponding option
			$("div.divTags select :selected").remove();

			if($("div.divTags select :selected").length == 0){
				$("div.divTags input[type='button']").prop('disabled', true);
				return;
			}
		}));

		form.append(divCheckboxes);
		form.append($('<button>').attr('type', 'button').html("Modify bookmark").click(modifyBookmark));
		form.append($('<button>').attr('type', 'button').html("Remove bookmark").click(removeBookmark));
		$(form).children("input[type=text]").css("margin","0px 20px 20px 0px");
		$(this).append(form);
	}
}

function addBookmark(){
	var newBookmark = {
		"title":				$("form.addBookmark input[name='newTitle']").val(),
		"description": 	$("form.addBookmark input[name='newDescription']").val(),
		"link": 				$("form.addBookmark input[name='newLink']").val(),
		"tags":[]
	};

	$("form.addBookmark div.divTags input:checked").each(function(index,box){
		var tag = {
			"id":$(box).attr("data-id"),
			"name":$(box).attr("data")
		};
		newBookmark.tags.push(tag);
	});

	$.ajax({
		url : wsBase+"bookmarks?x-http-method=post&json="+JSON.stringify(newBookmark),
		method : "POST"
	}).complete(listBookmarks);
}

function modifyBookmark(){

	var modifiedBookmark = {
		"id": 					$("#items .item.bookmark.selected").attr("num"),
		"title":				$("form.modifyBookmark input[name='newTitle']").val(),
		"description":	$("form.modifyBookmark input[name='newDescription']").val(),
		"link":					$("form.modifyBookmark input[name='newLink']").val(),
		"tags":[]
	};

	//Iterate through the checked checkboxes
	$("div.divTags input:checked").each(function(index,box){
		var tag = {
			"id":		$(box).attr("data-id"),
			"name":	$(box).attr("data")
		};
		//Fill tags array
		modifiedBookmark.tags.push(tag);
	});

	$.ajax({
		url : wsBase+"bookmarks/"+modifiedBookmark.id+"?x-http-method=put&json="+JSON.stringify(modifiedBookmark,null,2),
		method : "PUT"
	}).complete(listBookmarks);
}

function removeBookmark(){
	var bookmarkToRemoveId = $("#items .selected").attr("num");

	//Get the current JSON representation
	$.getJSON(wsBase+"bookmarks/"+bookmarkToRemoveId,function(bookmark){
		//Empty its tag array in order to remove integrity constraints while removing
		bookmark.tags = [];
		//Update bookmark
		$.ajax({
			url : wsBase+"bookmarks/"+bookmark.id+"?x-http-method=put&json="+JSON.stringify(bookmark,null,2),
			method : "PUT"
		}).complete(function(){
			//Delete the bookmark
			$.ajax({
				url : wsBase+"bookmarks/"+bookmarkToRemoveId+"?x-http-method=delete",
				method : "DELETE"
			}).complete(listBookmarks);
		});
	});
}

/* On document loading */
$(function() {
	// Put the name of the current user into <h1>
	setIdentity()

	// Adapt the height of <div id="contents"> to the navigator window
	setContentHeight()
	// Listen to the clicks on menu items
	$('#menu li').on('click', function() {
		var isTags = $(this).hasClass('tags')
		selectObjectType(isTags ? "tags" : "bookmarks")
	})

	// Initialize the object type to "bookmarks"
	selectObjectType("bookmarks")

	// Listen to clicks on the "add tag" button
	$('#addTag').on('click', addTag)

	// Listen to clicks on the tag items
	$(document).on('click','#items .item.tag',clickTag)

	// Listen to clicks on the bookmark items
	$(document).on('click','#items .item.bookmark',clickBookmark)
})
