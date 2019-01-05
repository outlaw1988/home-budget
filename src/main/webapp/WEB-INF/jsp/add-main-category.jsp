<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<div class="container">

	<form:form method="post" modelAttribute="category">
	
		<form:hidden path="id" />
	
		<br/><br/>
		
		<form:label path="type">Typ:</form:label>
		<br/>
		<form:select path="type">
			<option value="wydatek">Wydatek</option>
			<option value="dochód">Dochód</option>
		</form:select>
	
		<br/><br/>
		
		<fieldset class="form-group">
			<form:label path="name">Nazwa kategorii:</form:label>
			<form:input path="name" type="text" class="form-control"
				required="required" />
			<form:errors path="name" class="error" />
		</fieldset>
		
		<br/><br/>
		
		<button id="add-category" type="submit" class="btn btn-success">Dodaj</button>
	
	</form:form>

</div>

<%@ include file="common/footer.jspf"%>