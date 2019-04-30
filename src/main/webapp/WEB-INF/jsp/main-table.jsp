<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<!-- style="transform: scale(0.8); transform-origin: top left;" -->

<div id="table-cont" class="container">

	<h1>Główna tabela</h1>
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
	
	<h3 align="center">Dochody</h3>
	
	<!-- zoom: 50 %; transform: scale(calc(2/3)); table-layout: fixed; width: 100%; -->
	
	<table id="incomes-table" class="table table-striped table-hover">
		<col width="20%">
  		<col width="20%">
  		<col width="20%">
  		<col width="20%">
	  <thead>
	    <tr>
	      <th scope="col">Kategoria</th>
	      <th scope="col">Podkategoria</th>
	      <th scope="col">Wartość</th>
	      <th scope="col">Średnia</th>
	    </tr>
	  </thead>
	  <tbody>
	    <c:forEach items="${incomes}" var="income">
	  		<tr>
	        	<td>${income.subCategory.category.name}</td>
	        	<td>${income.subCategory.name}</td>
	        	<td>${income.sumValue}</td>
	        	<td>${income.average}</td>
	      	</tr>
	  	</c:forEach>
	  	<tr>
	  		<td></td>
	  		<td><b>Suma brutto: </b></td>
	  		<td><b>${incomesSum}</b></td>
	  		<td><b>${incomesAverage}</b></td>
	  	</tr>
	  </tbody>
	</table>
	
	<h3 align="center">Wydatki</h3>
	
	<table id="expenditures-table" class="table table-striped table-hover">
		<col width="20%">
  		<col width="20%">
  		<col width="20%">
  		<col width="20%">
	  <thead>
	    <tr>
	      <th scope="col">Kategoria</th>
	      <th scope="col">Podkategoria</th>
	      <th scope="col">Wartość</th>
	      <th scope="col">Średnia</th>
	    </tr>
	  </thead>
	  <tbody>
	    <c:forEach items="${expenditures}" var="expenditure">
	  		<tr>
	        	<td>${expenditure.subCategory.category.name}</td>
	        	<td>${expenditure.subCategory.name}</td>
	        	<td>${expenditure.sumValue}</td>
	        	<td>${expenditure.average}</td>
	      	</tr>
	  	</c:forEach>
	  	<tr>
	  		<td></td>
	  		<td><b>Suma brutto: </b></td>
	  		<td><b>${expendituresSum}</b></td>
	  		<td><b>${expendituresAverage}</b></td>
	  	</tr>
	  </tbody>
	</table>
	
	<h3 align="center">Podsumowanie</h3>
	
	<table id="summary-table" class="table table-striped table-hover">
		<col width="20%">
  		<col width="20%">
  		<col width="20%">
  		<col width="20%">
		<thead>
			<tr>
				<th scope="col"></th>
				<th scope="col"></th>
				<th scope="col"><b>Wartość</b></th>
				<th scope="col"><b>Średnia</b></th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td></td>
				<td><b>Bilans:</b></td>
				<td><b>${diff}</b></td>
				<td><b>${diffAverage}</b></td>
			</tr>
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

$("#sel-year").change(function(){ 
	var year = $(this).val();
	
	var data = {
		"year": year
	}
	
	var json = JSON.stringify(data);
	
	$.ajax({
		type: "POST",
		async: false,
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
			
			removeTableContent("incomes-table");
			removeTableContent("expenditures-table");
			removeTableContent("summary-table");
			
			updateTable("incomes", month, year);
			updateTable("expenditures", month, year);
			updateSummaryTable();
	    }
	});
	
});

$("#sel-month").change(function(){ 
	var month = $(this).val();
	var year = $("#sel-year").val();
	
	removeTableContent("incomes-table");
	removeTableContent("expenditures-table");
	removeTableContent("summary-table");
	
	updateTable("incomes", month, year);
	updateTable("expenditures", month, year);
	updateSummaryTable();
});

function updateTable(tableType, month, year) {
	
	var data = {
		"month": month,
		"year": year
	}
	
	var json = JSON.stringify(data);
	var urlData = "";
	
	if (tableType == "incomes") {
		urlData = "/get-accumulated-incomes-table";
	} else if (tableType == "expenditures") {
		urlData = "/get-accumulated-expenditures-table";
	}
	
	$.ajax({
		type: "POST",
		async: false,
		contentType: "application/json; charset=utf-8",
	    dataType : 'json',
	    url: urlData,
	    data: json,
		success :function(result) {
			drawTable(result, tableType);
	    }
	});
}

function drawTable(data, tableType) {
	
	var tableId = "";
	
	if (tableType == "incomes") {
		tableId = "incomes-table";
	} else if (tableType == "expenditures") {
		tableId = "expenditures-table";
	}
	
	var table = document.getElementById(tableId).getElementsByTagName('tbody')[0];
	
	for (var i = 0; i < data.accumulatedItems.length; i++) {
		var row = table.insertRow(-1);
		var cell1 = row.insertCell(0);
		var cell2 = row.insertCell(1);
		var cell3 = row.insertCell(2);
		var cell4 = row.insertCell(3);
		cell1.innerHTML = data.accumulatedItems[i].subCategory.category.name;
		cell2.innerHTML = data.accumulatedItems[i].subCategory.name;
		cell3.innerHTML = data.accumulatedItems[i].sumValue.toFixed(2);
		cell4.innerHTML = data.accumulatedItems[i].average.toFixed(2);
	}
	
	var row = table.insertRow(-1);
	var cell1 = row.insertCell(0);
	var cell2 = row.insertCell(1);
	var cell3 = row.insertCell(2);
	var cell4 = row.insertCell(3);
	cell1.innerHTML = "";
	cell2.innerHTML = "<b>Suma brutto:</b>";
	cell3.innerHTML = "<b>" + data.sum.toFixed(2) + "</b>";
	cell4.innerHTML = "<b>" + data.average.toFixed(2) + "</b>";
}

function updateSummaryTable() {
	$.ajax({
		type: "POST",
		async: false,
		contentType: "application/json; charset=utf-8",
	    dataType : 'json',
	    url: "/get-summary-table",
		success :function(result) {
			var table = document.getElementById("summary-table").getElementsByTagName('tbody')[0];
			var row = table.insertRow(-1);
			
			var cell1 = row.insertCell(0);
			var cell2 = row.insertCell(1);
			var cell3 = row.insertCell(2);
			var cell4 = row.insertCell(3);
			
			cell1.innerHTML = "";
			cell2.innerHTML = "<b>Bilans:</b>";
			cell3.innerHTML = "<b>" + result.diffValue.toFixed(2) + "</b>";
			cell4.innerHTML = "<b>" + result.average.toFixed(2) + "</b>";
	    }
	});
}

function removeTableContent(tableId) {
	var table = document.getElementById(tableId);
	var rowCount = table.rows.length;
	for (var x = rowCount - 1; x > 0; x--) {
		table.deleteRow(x);
	}
}

function convertNumberTwoDecimalPlaces(number) {
	var num = result.diffValue;
	var n = num.toFixed(2);
	document.write(n);
}

</script>

<%@ include file="common/footer.jspf"%>