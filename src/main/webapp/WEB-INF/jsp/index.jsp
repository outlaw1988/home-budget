<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<div class="container">

	<h1>Domowy budżet</h1>
	<br/>
	<div style="text-align:center;">
		<a id="add-expenditure" type="button" class="btn btn-success" href="/add-expenditure">Dodaj wydatek</a>
	</div>
	
	<br/>
	<div style="text-align:center;">
		<a id="add-income" type="button" class="btn btn-success" href="/add-income">Dodaj dochód</a>
	</div>
	
	<br/>
	<div style="text-align:center;">
		<a id="add-donation" type="button" class="btn btn-success" href="/add-donation">Dodaj dotację</a>
	</div>

</div>

<%@ include file="common/footer.jspf"%>