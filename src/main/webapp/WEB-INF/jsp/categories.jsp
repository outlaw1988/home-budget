<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<div class="container">

	<h1>Zarządzanie kategoriami</h1>
	
	<br/>
	
	<div align="center">
		<span>Rodzaj:</span>
		<select id="sel-type">
		    <option value="expenditure">WYDATEK</option>
		    <option value="income">DOCHÓD</option>
		</select>
	</div>
	
	<br/>

	<table id="categories-table" class="table table-striped category-table">
		<thead>
			<tr>
				<th colspan="3" class="table-header"><b>Kategorie</b></th>
			</tr>
		</thead>
		
		<tbody>
			<c:forEach items="${categories}" var="category" varStatus="loop">
				<tr>
					<td style="display:none;">${category.id}</td>
					<td>${category.name}</td>
					<td>
		        		<div class="dropdown" onclick="dropDownCategory(${loop.index})">
			        		<img id="three-dots" class="three-dots" src="images/three_dots_res_2.png" 
			        		alt="Three dots">
			        		<div id="my-dropdown-category-${loop.index}" class="dropdown-content">
				            	<a href="/update-category-${category.id}">Edytuj</a>
				                <a href="/remove-category-${category.id}">Usuń</a>
				            </div>
			            </div>
		        	</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	
	<br/>
	
	<div style="text-align:center;">
		<a id="add-expenditure" type="button" class="btn btn-success" href="/add-category">Dodaj kategorię</a>
	</div>
	
	<br/>
	
	<table id="sub-categories-table" class="table table-striped category-table">
		<thead>
			<tr>
				<th colspan="2" class="table-header"><b>Podkategorie</b></th>
			</tr>
		</thead>
		
		<tbody>
			<c:forEach items="${subCategories}" var="subCategory" varStatus="loop">
				<tr>
					<td>${subCategory.name}</td>
					<td>
		        		<div class="dropdown" onclick="dropDownSubCategory(${loop.index})">
			        		<img id="three-dots" class="three-dots" src="images/three_dots_res_2.png" 
			        		alt="Three dots">
			        		<div id="my-dropdown-subcategory-${loop.index}" class="dropdown-content">
				            	<a href="/update-subcategory-${subCategory.id}">Edytuj</a>
				                <a href="/remove-subcategory-${subCategory.id}">Usuń</a>
				            </div>
			            </div>
		        	</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	
	<br/>
	
	<div id="add-subcategory-div" style="text-align:center;"></div>

</div>

<script type="text/javascript">

window.onload = loadPage;
var currSelectedCategory;

$(function () {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
});

function loadPage() {
	highlightFirstRow();
	manageAddSubCategoryButton();
}

function highlightFirstRow() {
	var firstRow = $("#categories-table tbody tr:first")
	firstRow.addClass('highlight');
	currSelectedCategory = firstRow.find('td').html()
	return firstRow;
}

function highlightFirstRowAndGetSubcategories() {
	var firstRow = highlightFirstRow();
	
	if (firstRow.length != 0) {
		var subCategoriesData = getSubcategories(firstRow.find('td').html());
		removeTableContent("sub-categories-table");
		updateTable("sub-categories-table", subCategoriesData);
	} else {
		removeTableContent("sub-categories-table");
	}
}

$("#categories-table > tbody").delegate('tr', 'click', function() {
	var selected = $(this).hasClass("highlight");
    if(!selected) {
    	$("#categories-table tr").removeClass("highlight");
    	$(this).addClass("highlight");
    } 
});

$("#categories-table > tbody").delegate('tr', 'click', function() {
	currSelectedCategory = $(this).find('td').html();
	manageAddSubCategoryButton();
	var subCategoriesData = getSubcategories(currSelectedCategory);
	removeTableContent("sub-categories-table");
	updateTable("sub-categories-table", subCategoriesData);
});

function getSubcategories(categoryId) {
	var data = {
		"categoryId": categoryId
	}
	
	var json = JSON.stringify(data);
	var response = null;
	
	$.ajax({
		type: "POST",
		contentType: "application/json; charset=utf-8",
	    dataType : 'json',
	    url: "/get-subcategories",
	    async: false,
	    data: json,
		success: function(result) {
			response = result;
		}
	});
	
	return response;
}

$("#sel-type").change(function() {
	var type = $(this).val();
	
	var data = {
		"type": type
	}
	
	var json = JSON.stringify(data);
	
	$.ajax({
		type: "POST",
		contentType: "application/json; charset=utf-8",
	    dataType : 'json',
	    url: "/change-type-get-categories",
	    data: json,
		success: function(result) {
			removeTableContent("categories-table");
			updateTable("categories-table", result);
			highlightFirstRowAndGetSubcategories();
			manageAddSubCategoryButton();
		}
	});
	
});

function updateTable(tableId, data) {
	
	var table = document.getElementById(tableId).getElementsByTagName('tbody')[0];
	
	for (var i = 0; i < data.length; i++) {
		var row = table.insertRow(-1);
		var cell1 = row.insertCell(0);
		var cell2 = row.insertCell(1);
		var cell3 = row.insertCell(2);
		
		cell1.style.display = 'none';
		cell1.innerHTML = data[i].id;
		cell2.innerHTML = data[i].name;
		
		if (tableId == "categories-table") {
			cell3.innerHTML = "<div class='dropdown' onclick='dropDownCategory(" + i + ")''> <img id='three-dots' class='three-dots' src='images/three_dots_res_2.png' alt='Three dots'> <div id='my-dropdown-category-" + i + "' class='dropdown-content'> <a href='/update-category-" + data[i].id + "'>Edytuj</a> <a href='/remove-category-" + data[i].id + "'>Usuń</a> </div> </div>";
		} else if (tableId == "sub-categories-table") {
			cell3.innerHTML = "<div class='dropdown' onclick='dropDownSubCategory(" + i + ")''> <img id='three-dots' class='three-dots' src='images/three_dots_res_2.png' alt='Three dots'> <div id='my-dropdown-subcategory-" + i + "' class='dropdown-content'> <a href='/update-subcategory-" + data[i].id + "'>Edytuj</a> <a href='remove-subcategory-" + data[i].id + "'>Usuń</a> </div> </div>";
		}
		
	}
	
}

function manageAddSubCategoryButton() {
	var rowsNum = $('#categories-table tbody tr').length;
	if (rowsNum > 0) {
		$("#add-subcategory-div").html("<a id='add-subcategory' type='button' class='btn btn-success' href='/add-subcategory-" + currSelectedCategory + "'>Dodaj podkategorię</a>");
	} else if (rowsNum == 0) {
		$("#add-subcategory-div").html("");
	}
}

function removeTableContent(tableId) {
	$("#" + tableId + " tbody tr").remove();
}

function dropDownCategory(counter) {
    currCounter = counter;
    document.getElementById("my-dropdown-category-" + counter).style.display = "block";
}

function dropDownSubCategory(counter) {
    currCounter = counter;
    document.getElementById("my-dropdown-subcategory-" + counter).style.display = "block";
}

window.onclick = function(event) {
    if (!event.target.matches('.three-dots')) {
        var dropdowns = document.getElementsByClassName("dropdown-content");
        var i;
        for (i = 0; i < dropdowns.length; i++) {
            var openDropdown = dropdowns[i];
            openDropdown.style.display = "none";
      }
    }
}

</script>

<%@ include file="common/footer.jspf"%>