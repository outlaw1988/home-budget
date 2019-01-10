<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<div class="container">

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
	
	<h3>Dochody</h3>
	
	<table class="table table-striped table-hover">
	  <thead>
	    <tr>
	      <th scope="col">Kategoria</th>
	      <th scope="col">Podkategoria</th>
	      <th scope="col">Wartość</th>
	    </tr>
	  </thead>
	  <tbody>
	    <c:forEach items="${incomes}" var="income">
	  		<tr>
	        	<td>${income.subCategory.category.name}</td>
	        	<td>${income.subCategory.name}</td>
	        	<td>${income.sumValue}</td>
	      	</tr>
	  	</c:forEach>
	  </tbody>
	</table>

</div>

<script type="text/javascript">

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
	    }
	});
});

$("#sel-month").change(function(){ 
	console.log("Month has changed...");
});

</script>

<%@ include file="common/footer.jspf"%>