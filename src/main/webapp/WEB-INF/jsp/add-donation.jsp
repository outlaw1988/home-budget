<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>


<div class="container">

	<h1>Dodaj dotację</h1>
	
	<form:form method="post" modelAttribute="donation">
	
		<br/>
	
		<form:hidden path="id" />
		
		<fieldset class="form-group">
			<form:label path="value">Wartość:</form:label>
			<form:input style="width: 150px;" path="value" type="number" step="0.01" min="0.01" class="form-control"
				required="required" />
			<form:errors path="value" class="error" />
		</fieldset>
		
		<br/>
		
		<fieldset class="form-group">
			<form:label path="description">Opis:</form:label>
			<form:input path="description" type="text" class="form-control"/>
			<form:errors path="description" class="error" />
		</fieldset>
		
		<br/>
		
		<fieldset class="form-group">
			<form:label path="dateTime">Data:</form:label>
			<fmt:formatDate value="${currentDate}" pattern="yyyy/MM/dd HH:mm:ss" var="myDate" />
			<form:input style="width: 250px;" path="dateTime" class="form-control" value="${myDate}"/>
			<form:errors path="dateTime" class="error" />
		</fieldset>
		<span>[RRRR/MM/DD HH:MM:SS]</span>
	
		<br/><br/>
		
		<button id="add-category" type="submit" class="btn btn-success">Dodaj</button>
	
	</form:form>

</div>

<%@ include file="common/footer.jspf"%>