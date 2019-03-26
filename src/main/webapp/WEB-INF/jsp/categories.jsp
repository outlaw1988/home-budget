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

	<table id="categories-table" class="table table-striped table-hover category-table">
		<thead>
			<tr>
				<th colspan="2" class="table-header"><b>Kategorie</b></th>
			</tr>
		</thead>
		
		<tbody>
			<c:forEach items="${categories}" var="category" varStatus="loop">
				<tr>
					<td>${category.name}</td>
					<td>
		        		<div class="dropdown" onclick="dropDown(${loop.index})">
			        		<img id="three-dots" class="three-dots" src="images/three_dots_res_2.png" 
			        		alt="Three dots">
			        		<div id="my-dropdown-${loop.index}" class="dropdown-content">
				            	<a href="#">Edytuj</a>
				                <a href="/remove-category-${category.id}">Usuń</a>
				            </div>
			            </div>
		        	</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	
	<table id="sub-categories-table" class="table table-striped table-hover category-table">
		<thead>
			<tr>
				<th class="table-header"><b>Podkategorie</b></th>
			</tr>
		</thead>
		
		<tbody>
			
		</tbody>
	</table>

</div>

<script type="text/javascript">

$(function () {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
});

$("#sel-type").change(function() {
	var type = $(this).val();
	console.log("Type has changed: " + type);
	
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
			/* console.log("Success...");
			for (var i = 0; i < result.length; i++) {
				console.log(result[i]);
			} */
			removeTableContent("categories-table");
			updateTable("categories-table", result);
		}
	});
});

function updateTable(tableId, data) {
	
	var table = document.getElementById(tableId).getElementsByTagName('tbody')[0];
	
	for (var i = 0; i < data.length; i++) {
		var row = table.insertRow(-1);
		var cell1 = row.insertCell(0);
		var cell2 = row.insertCell(1);
		cell1.innerHTML = data[i].name;
		cell2.innerHTML = "<div class='dropdown' onclick='dropDown(" + i + ")''> <img id='three-dots' class='three-dots' src='images/three_dots_res_2.png' alt='Three dots'> <div id='my-dropdown-" + i + "' class='dropdown-content'> <a href='#'>Edytuj</a> <a href='remove-category-" + data[i].id + "'>Usuń</a> </div> </div>";
	}
	
}

function removeTableContent(tableId) {
	var table = document.getElementById(tableId);
	var rowCount = table.rows.length;
	for (var x = rowCount - 1; x > 0; x--) {
		table.deleteRow(x);
	}
}

function dropDown(counter) {
    currCounter = counter;
    document.getElementById("my-dropdown-" + counter).style.display = "block";
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