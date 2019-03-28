<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<div class="container">

	<form:form method="post" modelAttribute="category">
	
		<form:hidden path="id" />
	
		<br/><br/>
		
		<form:label path="type">Rodzaj:</form:label>
		<form:select path="type">
			<option value="EXPENDITURE">WYDATEK</option>
			<option value="INCOME">DOCHÓD</option>
		</form:select>
	
		<br/><br/>
		
		<fieldset class="form-group">
			<form:label path="name">Nazwa kategorii:</form:label>
			<form:input path="name" type="text" class="form-control" required="required" />
			<form:errors path="name" class="error" />
		</fieldset>
		
		<br/><br/>
		
		<button id="add-category" type="submit" class="btn btn-success">Dodaj</button>
	
	</form:form>

</div>

<%@ include file="common/footer.jspf"%>