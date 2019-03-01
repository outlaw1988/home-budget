<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<div class="container">

	<br/>
	<div style="text-align:center;">
		<a id="add-expenditure" type="button" class="btn btn-success" href="/add-expenditure">Dodaj</a>
	</div>
	
	<br/>
	
	<table class="table table-striped table-hover">
	  <thead>
	    <tr>
	      <th scope="col">Data</th>
	      <th scope="col">Kategoria</th>
	      <th scope="col">Podkategoria</th>
	      <th scope="col">Opis</th>
	      <th scope="col">Wartość</th>
	    </tr>
	  </thead>
	  <tbody>
	    <c:forEach items="${expenditures}" var="expenditure">
	  		<tr>
	        	<td>${expenditure.dateTime}</td>
	        	<td>${expenditure.subCategory.category.name}</td>
	        	<td>${expenditure.subCategory.name}</td>
	        	<td>${expenditure.description}</td>
	        	<td>${expenditure.value}</td>
	      	</tr>
	  	</c:forEach>
	  </tbody>
	</table>

</div>

<%@ include file="common/footer.jspf"%>