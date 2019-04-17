<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<div class="container">

	<c:choose>
	  <c:when test="${isInUse == true}">
	    <br>
	    <p>Usunięcie jest niemożliwe, ponieważ do podkategorii są przypisane wydatki/dochody.</p>
	    <br>
	    <a href="/categories">
	    	<button type="button" class="btn btn-success">Powrót</button>
	    </a>
	  </c:when>
	  <c:otherwise>
	    <br>
	    <p>Czy jesteś pewien, że chcesz usunąć podkategorię?</p>
	
	    <form action="?${_csrf.parameterName}=${_csrf.token}" method="post">
	
	      <button type="submit" name="yes" class="btn btn-primary">TAK</button>
	      <button type="submit" name="no" class="btn btn-primary">NIE</button>
	
	    </form>
	  </c:otherwise>
	</c:choose>

</div>

<%@ include file="common/footer.jspf"%>