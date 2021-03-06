<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<div class="container">

	<h1>Dotacje</h1>
	
	<div style="text-align:center;">
		<a id="add-donation" type="button" class="btn btn-success" href="/add-donation">Dodaj</a>
	</div>
	
	<br/>
	
	<span>Rok:</span>
	<select id="sel-year">
	    <c:forEach items="${years}" var="year"> 
           	<option value="${year}">${year}</option> 
        </c:forEach>
	</select>
	
	<span>Miesiąc:</span>
	<select id="sel-month">
	    <c:forEach items="${months}" var="month"> 
           	<option value="${month}">${month}</option> 
        </c:forEach>
	</select>
	
	<br/><br/>
	
	<span>Procent od dochodów:</span>
	<div>
		<input id="donation-rate" style="width: 150px; float: left;" type="number" 
		step="0.01" min="0" max="100" class="form-control" required="required" 
		value="${donationRate}" />
		<a onclick=""><input type="button" id="confirm-rate" class="btn btn-success" 
		   value="Zatwierdź"/></a>
	</div>
	
	<br/><br/>
	
	<table id="donations-table" class="table table-striped table-hover">
		<col width="20%">
		<col width="25%">
  		<col width="10%">
  		<col width="5%">
		<thead>
		    <tr>
		      <th scope="col">Data</th>
		      <th scope="col">Opis</th>
		      <th scope="col">Wartość</th>
		      <th></th>
		    </tr>
	  	</thead>
		<tbody>
			 <c:forEach items="${donations}" var="donation" varStatus="loop">
			 	<tr>
			 		<td>
		        		<fmt:formatDate value="${donation.dateTime}" pattern="yyyy-MM-dd HH:mm:ss" var="dateFormatted" />
		        		${dateFormatted}
		        	</td>
		        	<td>${donation.description}</td>
		        	<td>${donation.value}</td>
		        	<td>
		        		<div class="dropdown" onclick="dropDown(${loop.index})">
			        		<img id="three-dots" class="three-dots" src="images/three_dots_res_2.png" 
			        		alt="Three dots">
			        		<div id="my-dropdown-${loop.index}" class="dropdown-content">
				            	<a href="/update-donation-${donation.id}">Edytuj</a>
				                <a href="/remove-donation-${donation.id}">Usuń</a>
				            </div>
			            </div>
		        	</td>
			 	</tr>
			 </c:forEach>
			 <tr>
	  		<td></td>
	  		<td><b>Suma: </b></td>
	  		<td><b>${donationsSum}</b></td>
	  		<td></td>
	  	</tr>
		</tbody>	  
	</table>
	
	<table id="summary-table" class="table table-striped table-hover">
		<col width="20%">
		<col width="25%">
  		<col width="10%">
  		<col width="5%">
  		<thead>
  			<tr>
  				<th scope="col"></th>
	  			<th scope="col"></th>
	  			<th scope="col"></th>
	  			<th scope="col"></th>
  			</tr>
  		</thead>
  		<tbody>
			<tr>
				<td></td>
				<td><b>Do wpłacenia:</b></td>
				<td><b>${totalToPay}</b></td>
				<td></td>
			</tr>
			<tr>
				<td></td>
				<td><b>Pozostało:</b></td>
				<td><b>${remaining}</b></td>
				<td></td>
			</tr>
		</tbody>
	</table>

</div>

<script language="javascript" type="text/javascript"> 

$(function () {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
});

$("#confirm-rate").click(function() {
	var data = {
		"rate": $("#donation-rate").val(),
		"month": $("#sel-month").val(),
		"year": $("#sel-year").val()
	}
	
	var json = JSON.stringify(data);
	
	$.ajax({
		type: "POST",
		contentType: "application/json; charset=utf-8",
	    dataType : 'json',
	    async: false,
	    url: "/confirm-rate",
	    data: json,
	    success: function(result) {
	    	
	    }
	});
	
	removeTableContent("summary-table");
	updateSummaryTable();
});

$("#sel-year").change(function(){
	var year = $(this).val();
	
	var data = {
		"year": year
	}
	
	var json = JSON.stringify(data);
	
	$.ajax({
		type: "POST",
		contentType: "application/json; charset=utf-8",
	    dataType : 'json',
	    url: "/change-year",
	    data: json,
		success: function(result) {
	    	
			var slctMonth = $("#sel-month"); 
			slctMonth.empty();
			
			for(var i = 0; i < result.length; i++){
                 option = "<option value='" + result[i] + "'>" + result[i] + "</option>";
                 slctMonth.append(option);
            }
			
			var month = $("#sel-month").val();
			month.selectedIndex = 0;
			
			removeTableContent("donations-table");
			removeTableContent("summary-table");
			
			updateTable(month, year);
			updateSummaryTable();
	    }
	});
});

$("#sel-month").change(function(){ 
	var month = $(this).val();
	var year = $("#sel-year").val();
	
	removeTableContent("donations-table");
	removeTableContent("summary-table");
	
	updateTable(month, year);
	updateSummaryTable();
});

function removeTableContent(tableName) {
	var table = document.getElementById(tableName);
	var rowCount = table.rows.length;
	for (var x = rowCount - 1; x > 0; x--) {
		table.deleteRow(x);
	}
}

function updateTable(month, year) {
	
	var data = {
			"month": month,
			"year": year
		}
	
	var json = JSON.stringify(data);
	
	$.ajax({
		type: "POST",
		contentType: "application/json; charset=utf-8",
	    dataType : 'json',
	    url: "/get-donations-table",
	    data: json,
		success: function(result) {
			drawTable(result);
			updateDonationRate(result.donationRate);
	    }
	});
}

function drawTable(data) {
	
	var table = document.getElementById("donations-table").getElementsByTagName('tbody')[0];
	
	for (var i = 0; i < data.items.length; i++) {
		var row = table.insertRow(-1);
		var cell1 = row.insertCell(0);
		var cell2 = row.insertCell(1);
		var cell3 = row.insertCell(2);
		var cell4 = row.insertCell(3);
		
		cell1.innerHTML = $.format.date(data.items[i].dateTime, "yyyy-MM-dd HH:mm:ss");
		cell2.innerHTML = data.items[i].description;
		cell3.innerHTML = data.items[i].value.toFixed(2);
		cell4.innerHTML = "<div class='dropdown' onclick='dropDown(" + i + ")''> <img id='three-dots' class='three-dots' src='images/three_dots_res_2.png' alt='Three dots'> <div id='my-dropdown-" + i + "' class='dropdown-content'> <a href='update-donation-" + data.items[i].id + "'>Edytuj</a> <a href='remove-donation-" + data.items[i].id + "'>Usuń</a> </div> </div>";
	}
	
	var row = table.insertRow(-1);
	var cell1 = row.insertCell(0);
	var cell2 = row.insertCell(1);
	var cell3 = row.insertCell(2);
	var cell4 = row.insertCell(3);
	cell1.innerHTML = "";
	cell2.innerHTML = "<b>Suma:</b>";
	cell3.innerHTML = "<b>" + data.sum.toFixed(2) + "</b>";
	cell4.innerHTML = "";
	
}

function updateSummaryTable() {
	
	var data = {
			"month": $("#sel-month").val(),
			"year": $("#sel-year").val()
		}
		
	var json = JSON.stringify(data);
	
	$.ajax({
		type: "POST",
		async: false,
		contentType: "application/json; charset=utf-8",
	    dataType : 'json',
	    url: "/get-summary-donations-table",
	    data: json,
		success :function(result) {
			var table = document.getElementById("summary-table").getElementsByTagName('tbody')[0];
			var row = table.insertRow(-1);
			
			var cell1 = row.insertCell(0);
			var cell2 = row.insertCell(1);
			var cell3 = row.insertCell(2);
			var cell4 = row.insertCell(3);
			
			cell1.innerHTML = "";
			cell2.innerHTML = "<b>Do wpłacenia:</b>";
			cell3.innerHTML = "<b>" + result.totalToPay.toFixed(2) + "</b>";
			cell4.innerHTML = "";
			
			row = table.insertRow(-1);
			
			cell1 = row.insertCell(0);
			cell2 = row.insertCell(1);
			cell3 = row.insertCell(2);
			cell4 = row.insertCell(3);
			
			cell1.innerHTML = "";
			cell2.innerHTML = "<b>Pozostało:</b>";
			cell3.innerHTML = "<b>" + result.remaining.toFixed(2) + "</b>";
			cell4.innerHTML = "";
	    }
	});
}

function updateDonationRate(rate) {
	$("#donation-rate").val(rate);
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