<%@ include file="common/header.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<h1>Reset password</h1>

<c:choose>
	<c:when test="${invalidToken == true}">
		
		<div style="text-align:center;">
			<span>Niepoprawny token</span>
			<br/><br/>
			<a id="go-back" type="button" class="btn btn-success" href="/login">Go to login page</a>
		</div>
		
		
	</c:when>
	<c:otherwise>
	
		<div style="text-align:center;">
			<form:form method="post">
				<span>Nowe hasło:</span>
				<br/>
				<input type="password" id="password" name="password" required="required"/>
				<br/>
				<span>Potwierdź hasło:</span>
				<br/>
				<input type="password" id="password-confirm" name="password-confirm" required="required"/>
				<br/>
				<div style="text-align:center;">
					<span id="password-error" class="error">${passwordError}</span>
				</div>
				<br/>
				<button id="change-password" type="submit" class="btn btn-success">Zmień</button>
			</form:form>
			<div style="text-align:center;">
				<a id="go-back" type="button" class="btn btn-success" href="/login">Przejdź do strony logowania</a>
			</div>
		</div>
		<p style="text-align: center;">
	      	<span id="success-message">${successMessage}</span>
	    </p>
	
	</c:otherwise>
</c:choose>

<%@ include file="common/footer.jspf"%>