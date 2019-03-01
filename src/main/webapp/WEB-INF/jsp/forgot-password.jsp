<%@ include file="common/header.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<div class="container">

	<h1>Odzyskiwanie hasła</h1>
	
	<form:form method="post">
		<div style="text-align:center;">
			<span>Wprowadź email:</span>
			<br/>
			<input type="email" id="email" name="email"/>
		</div>
		<br/>
		<div style="text-align:center;">
			<button id="send-email" type="submit" class="btn btn-success">Wyślij</button>
			<br/><br/>
			<a id="go-back" type="button" class="btn btn-success" href="/login">Powrót</a>
		</div>
		
	</form:form>
	
	<br/>
	<div style="text-align:center;">
		<span id="info-message">${infoMessage}</span>
	</div>
	
</div>

<%@ include file="common/footer.jspf"%>