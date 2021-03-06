<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<div class="container">

	<h1>Edytuj wydatek</h1>
	
	<form:form method="post" modelAttribute="expenditure">
	
		<br/>
		<form:hidden path="id" />
		<form:hidden path="user"/>
		
		<fieldset class="form-group">
			<form:label path="value">Wartość:</form:label>
			<form:input style="width: 150px;" path="value" type="number" step="0.01" min="0.01" class="form-control"
				required="required" />
			<form:errors path="value" class="error" />
		</fieldset>
		
		<span>Kategoria:</span>
		<br/>
		<select id="sel-category">
		    <option value="">--WYBIERZ--</option>
		    <c:forEach items="${categories}" var="category"> 
            	<option value="${category.getId()}" <c:if test="${category.getName() == expenditure.getSubCategory().getCategory().getName()}"> selected="selected" </c:if> >${category.getName()}</option> 
            </c:forEach>
		</select>
		
		<br/><br/>
		
		<form:label path="subCategory">Podkategoria:</form:label>
		<br/>
		<form:select id="sel-subcategory" path="subCategory">
		    <option value="">--WYBIERZ--</option>
		    <c:forEach items="${subCategories}" var="subCategory"> 
            	<option value="${subCategory.getId()}" <c:if test="${subCategory.getName() == expenditure.getSubCategory().getName()}"> selected="selected" </c:if> >${subCategory.getName()}</option> 
            </c:forEach>
		</form:select>
		<br/>
		<p><form:errors class="error" path="subCategory"/></p>
		
		<br/>
		
		<fieldset class="form-group">
			<form:label path="description">Opis:</form:label>
			<form:input path="description" type="text" class="form-control"/>
			<form:errors path="description" class="error" />
		</fieldset>
		
		<br/>
		
		<fieldset class="form-group">
			<form:label path="dateTime">Data:</form:label>
			<fmt:formatDate value="${expenditure.getDateTime()}" pattern="yyyy/MM/dd HH:mm:ss" var="myDate" />
			<form:input style="width: 250px;" path="dateTime" class="form-control" value="${myDate}"/>
			<form:errors path="dateTime" class="error" />
		</fieldset>
		<span>[RRRR/MM/DD HH:MM:SS]</span>
	
		<br/><br/>
		
		<button id="update-expenditure" type="submit" class="btn btn-success">Zatwierdź</button>
	
	</form:form>

</div>

<script type="text/javascript"> 

$(function () {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
});

$("#sel-category").change(function(){ 
	
	var categoryId = $(this).val();
	
	var data = {
	    "categoryId": categoryId,
	}

	var json = JSON.stringify(data);
	
    $.ajax({
	    type: "POST",
	    contentType: "application/json; charset=utf-8",
	    dataType : 'json',
	    url: "/get-subcategories",
	    data: json,
	    success :function(result) {
	    	
	    	var slctSubcat = $("#sel-subcategory"); 
            slctSubcat.empty();
            var option= "<option value=''>--WYBIERZ--</option>";
            slctSubcat.append(option);
	    	
	    	for(var i = 0; i < result.length; i++){
               option = "<option value='" + result[i].id + "'>" + result[i].name + "</option>";
               slctSubcat.append(option);
            }
	    }
    });
	
});

</script>

<%@ include file="common/footer.jspf"%>