<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<div class="container">

	<h1>Dochody</h1>
	
	<div style="text-align:center;">
		<a id="add-income" type="button" class="btn btn-success" href="/add-income">Dodaj</a>
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
	
	<table id="incomes-table" class="table table-striped table-hover">
	  <thead>
	    <tr>
	      <th scope="col">Data</th>
	      <th scope="col">Kategoria</th>
	      <th scope="col">Podkategoria</th>
	      <th scope="col">Opis</th>
	      <th scope="col">Wartość</th>
	      <th></th>
	    </tr>
	  </thead>
	  <tbody>
	    <c:forEach items="${incomes}" var="income" varStatus="loop">
	  		<tr>
	        	<td>
					<fmt:formatDate value="${income.dateTime}" pattern="yyyy-MM-dd HH:mm:ss" var="dateFormatted" />
	        		${dateFormatted}
				</td>
	        	<td>${income.subCategory.category.name}</td>
	        	<td>${income.subCategory.name}</td>
	        	<td>${income.description}</td>
	        	<td>${income.value}</td>
	        	<td>
	        		<div class="dropdown" onclick="dropDown(${loop.index})">
		        		<img id="three-dots" class="three-dots" src="images/three_dots_res_2.png" 
		        		alt="Three dots">
		        		<div id="my-dropdown-${loop.index}" class="dropdown-content">
			            	<a href="/update-income-${income.id}">Edytuj</a>
			                <a href="/remove-income-${income.id}">Usuń</a>
			            </div>
		            </div>
	        	</td>
	      	</tr>
	  	</c:forEach>
	  </tbody>
	</table>

</div>

<script language="javascript" type="text/javascript">

    var currCounter = "";

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
    		contentType: "application/json; charset=utf-8",
    	    dataType : 'json',
    	    url: "/change-year",
    	    data: json,
    		success :function(result) {
    	    	
    			var slctMonth = $("#sel-month"); 
    			slctMonth.empty();
    			
    			for(var i = 0; i < result.length; i++){
                     option = "<option value='" + result[i] + "'>" + result[i] + "</option>";
                     slctMonth.append(option);
                }
    			
    			var month = $("#sel-month").val();
    			month.selectedIndex = 0;
    			
    			removeTableContent("incomes-table");
    			updateTable(month, year);
    	    }
    	});
    });
    
    $("#sel-month").change(function(){ 
    	var month = $(this).val();
    	var year = $("#sel-year").val();
    	
    	removeTableContent("incomes-table");
    	updateTable(month, year);
    });
    
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
    	    url: "/get-incomes-table",
    	    data: json,
    		success :function(result) {
    			drawTable(result);
    	    }
    	});
    }
    
	function drawTable(data) {
    	
    	var table = document.getElementById("incomes-table").getElementsByTagName('tbody')[0];
    	
    	for (var i = 0; i < data.items.length; i++) {
    		var row = table.insertRow(-1);
    		var cell1 = row.insertCell(0);
    		var cell2 = row.insertCell(1);
    		var cell3 = row.insertCell(2);
    		var cell4 = row.insertCell(3);
    		var cell5 = row.insertCell(4);
    		var cell6 = row.insertCell(5);
    		
    		cell1.innerHTML = $.format.date(data.items[i].dateTime, "yyyy-MM-dd HH:mm:ss");
    		cell2.innerHTML = data.items[i].subCategory.category.name;
    		cell3.innerHTML = data.items[i].subCategory.name;
    		cell4.innerHTML = data.items[i].description;
    		cell5.innerHTML = data.items[i].value.toFixed(2);
    		cell6.innerHTML = "<div class='dropdown' onclick='dropDown(" + i + ")''> <img id='three-dots' class='three-dots' src='images/three_dots_res_2.png' alt='Three dots'> <div id='my-dropdown-" + i + "' class='dropdown-content'> <a href='update-income-" + data.items[i].id + "'>Edytuj</a> <a href='remove-income-" + data.items[i].id + "'>Usuń</a> </div> </div>";
    	}
    	
    }
    
    function removeTableContent(tableName) {
    	var table = document.getElementById(tableName);
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
        //console.log('.three-dots-' + currCounter);
        if (!event.target.matches('.three-dots')) {
            var dropdowns = document.getElementsByClassName("dropdown-content");
            var i;
            for (i = 0; i < dropdowns.length; i++) {
                var openDropdown = dropdowns[i];
                openDropdown.style.display = "none";
                /* if (openDropdown.classList.contains('show')) {
                    openDropdown.classList.remove('show');
                } */
          }
        }
    }

</script>

<%@ include file="common/footer.jspf"%>