<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>


<div class="container">

	<form:form method="post" modelAttribute="expenditure">
	
		<br/>
	
		<form:hidden path="id" />
		
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
            	<option value="${category.getId()}">${category.getName()}</option> 
            </c:forEach>
		</select>
		
		<br/><br/>
		
		<form:label path="subCategory">Podkategoria:</form:label>
		<br/>
		<form:select id="sel-subcategory" path="subCategory">
		    <option value="">--WYBIERZ--</option>
		</form:select>
		
		<br/><br/>
		
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
		<span>[RRRR-MM-DD HH:MM:SS]</span>
	
		<br/><br/>
		
		<button id="add-category" type="submit" class="btn btn-success">Dodaj</button>
	
	</form:form>

</div>

<script type="text/javascript"> 

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
            //option = "";
	    	
	    	for(var i = 0; i < result.length; i++){
               /*  option = option + "<option value='" + data[sb].name + "'>" +data[sb].name + "</option>"; */
               /* console.log("Subcategory name: " + result[i].name);
               console.log("Subcategory id: " + result[i].id); */
               option = "<option value='" + result[i].id + "'>" + result[i].name + "</option>";
               slctSubcat.append(option);
            }
	    // do what ever you want with data
	    }
    });
	
});

</script>

<%@ include file="common/footer.jspf"%>