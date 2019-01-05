<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<div class="container">

	<form:form method="post" modelAttribute="subCategory">
		<form:hidden path="id" />
	
		<br/><br/>
		
		<form:label path="category">Kategoria:</form:label>
		<br/>
		<form:select path="category">
		    <option value="">--WYBIERZ--</option>
		    <c:forEach items="${categories}" var="category"> 
            	<option value="${category.getId()}">${category.getName()} - ${category.getType()}</option> 
            </c:forEach>
		</form:select>
		
		<a id="add-main-category" type="button" class="btn btn-success" href="/add-main-category">+</a>
		
		<p style="height: 10px;"><form:errors class="error" path="category"/></p>
		
		<br/>
		
		<fieldset class="form-group">
			<form:label path="name">Podkategoria:</form:label>
			<form:input path="name" type="text" class="form-control"
				required="required" />
			<form:errors path="name" class="error" />
		</fieldset>
		
		<br/><br/>
		
		<button id="add-category" type="submit" class="btn btn-success">Dodaj</button>
		
	</form:form>

</div>

<%@ include file="common/footer.jspf"%>