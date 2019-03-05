<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<div class="container">

	<h1>Dochody</h1>
	
	<div style="text-align:center;">
		<a id="add-income" type="button" class="btn btn-success" href="/add-income">Dodaj</a>
	</div>
	
	<br/>
	
	<table class="table table-striped table-hover">
	  <thead>
	    <tr>
	      <th scope="col">Data</th>
	      <th scope="col">Kategoria</th>
	      <th scope="col">Podkategoria</th>
	      <th scope="col">Opis</th>
	      <th scope="col">Wartość</th>
	      <th></th>
	    </tr>
	  </thead>
	  <tbody>
	    <c:forEach items="${incomes}" var="income" varStatus="loop">
	  		<tr>
	        	<td>${income.dateTime}</td>
	        	<td>${income.subCategory.category.name}</td>
	        	<td>${income.subCategory.name}</td>
	        	<td>${income.description}</td>
	        	<td>${income.value}</td>
	        	<td>
	        		<div class="dropdown" onclick="dropDown(${loop.index})">
		        		<img id="three-dots" class="three-dots" src="images/three_dots_res_2.png" 
		        		alt="Three dots">
		        		<div id="my-dropdown-${loop.index}" class="dropdown-content">
			            	<a href="/update-income-${income.id}">Edytuj</a>
			                <a href="/remove-income-${income.id}">Usuń</a>
			            </div>
		            </div>
	        	</td>
	      	</tr>
	  	</c:forEach>
	  </tbody>
	</table>

</div>

<script language="javascript" type="text/javascript">

    var currCounter = "";

    function dropDown(counter) {
        currCounter = counter;
        document.getElementById("my-dropdown-" + counter).style.display = "block";
    }

    window.onclick = function(event) {
        //console.log('.three-dots-' + currCounter);
        if (!event.target.matches('.three-dots')) {
            var dropdowns = document.getElementsByClassName("dropdown-content");
            var i;
            for (i = 0; i < dropdowns.length; i++) {
                var openDropdown = dropdowns[i];
                openDropdown.style.display = "none";
                /* if (openDropdown.classList.contains('show')) {
                    openDropdown.classList.remove('show');
                } */
          }
        }
    }

</script>

<%@ include file="common/footer.jspf"%>