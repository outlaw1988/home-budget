<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<div class="container">

	<br/>
	<div style="text-align:center;">
		<a id="add-income" type="button" class="btn btn-success" href="/add-income">Dodaj</a>
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
	    <c:forEach items="${incomes}" var="income">
	  		<tr>
	        	<td>${income.dateTime}</td>
	        	<td>${income.subCategory.category.name}</td>
	        	<td>${income.subCategory.name}</td>
	        	<td>${income.description}</td>
	        	<td>${income.value}</td>
	      	</tr>
	  	</c:forEach>
	  </tbody>
	</table>

</div>

<%@ include file="common/footer.jspf"%>