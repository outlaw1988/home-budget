<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<div class="container">

	<h1>Edytuj podkategorię</h1>
	<h3 align="center">Kategoria: ${categoryName}</h3>

	<form:form method="post" modelAttribute="subCategory">
	
		<form:hidden path="id" />
		<form:hidden path="category" />
		
		<br/><br/>
		
		<fieldset class="form-group">
			<form:label path="name">Nazwa podkategorii:</form:label>
			<form:input path="name" type="text" class="form-control" required="required" />
			<form:errors path="name" class="error" />
		</fieldset>
		
		<br/><br/>
		
		<button id="add-sub-category" type="submit" class="btn btn-success">Zatwierdź</button>
	
	</form:form>

</div>

<%@ include file="common/footer.jspf"%>