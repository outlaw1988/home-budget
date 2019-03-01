<%@ include file="common/header.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%-- <%@ include file="common/navigation.jspf"%> --%>

<div class="container">
	
	<h1>Rejestracja</h1>
	
	<form:form method="post" modelAttribute="user">
		
		<form:hidden path="id" />
		
		<fieldset class="form-group">
			<form:label path="username">Login:</form:label>
			<form:input path="username" type="text" class="form-control"
				required="required" />
			<form:errors path="username" class="error" />
		</fieldset>
		
		<fieldset class="form-group">
			<form:label path="email">Email:</form:label>
			<form:input path="email" type="text" class="form-control"
				required="required" />
			<form:errors path="email" class="error" />
		</fieldset>
		
		<fieldset class="form-group">
			<form:label path="password">Hasło:</form:label>
			<form:input path="password" type="password" class="form-control"
				required="required" />
			<form:errors path="password" class="error" />
		</fieldset>
		
		<fieldset class="form-group">
			<form:label path="passwordConfirm">Potwierdź hasło:</form:label>
			<form:input path="passwordConfirm" type="password" class="form-control"
				required="required" />
			<form:errors path="passwordConfirm" class="error" />
		</fieldset>
		
		<br/><br/>
		
		<button id="apply" type="submit" class="btn btn-success">Zatwierdź</button>
		
	</form:form>
	
	<a id="go-back" type="button" href="/login" class="btn btn-success">Powrót do logowania</a>
	
	<br/>
	
	<span id="success-message">${successMessage}</span>
	
</div>

<%@ include file="common/footer.jspf"%>