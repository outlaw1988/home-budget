<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<div class="container">

	<h1>Edytuj kategorię</h1>
	
	<form:form method="post" modelAttribute="category">
	
		<form:hidden path="id" />
		<form:hidden path="user"/>
	
		<br/><br/>
		
		<form:label path="type">Rodzaj:</form:label>
		<form:select path="type">
			<option value="EXPENDITURE" <c:if test="${category.getType() == 'EXPENDITURE'}"> selected="selected" </c:if> >WYDATEK</option>
			<option value="INCOME" <c:if test="${category.getType() == 'INCOME'}"> selected="selected" </c:if>>DOCHÓD</option>
		</form:select>
	
		<br/><br/>
		
		<fieldset class="form-group">
			<form:label path="name">Nazwa kategorii:</form:label>
			<form:input path="name" type="text" class="form-control" required="required" />
			<form:errors path="name" class="error" />
		</fieldset>
		
		<br/><br/>
		
		<button id="add-category" type="submit" class="btn btn-success">Zatwierdź</button>
	
	</form:form>

</div>

<%@ include file="common/footer.jspf"%>