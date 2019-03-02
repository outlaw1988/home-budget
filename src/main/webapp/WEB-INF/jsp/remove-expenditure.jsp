<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<div class="container">

	<br>
	<p>Czy jesteś pewien, że chcesz usunąć wydatek?</p>
	
	<form action="?${_csrf.parameterName}=${_csrf.token}" method="post">
	
	  <button type="submit" name="yes" class="btn btn-primary">TAK</button>
	  <button type="submit" name="no" class="btn btn-primary">NIE</button>
	
	</form>

</div>

<%@ include file="common/footer.jspf"%>