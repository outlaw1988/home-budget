<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>

<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Domowy budżet</title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M" crossorigin="anonymous">
    <link href="https://getbootstrap.com/docs/4.0/examples/signin/signin.css" rel="stylesheet" crossorigin="anonymous"/>
  </head>
  <body>
     <div class="container">
	      <form class="form-signin" method="post" action="/login?${_csrf.parameterName}=${_csrf.token}">
	        <h2 class="form-signin-heading">Logowanie</h2>
	        <p>
	          <label for="username" class="sr-only">Login</label>
	          <input type="text" id="username" name="username" class="form-control" placeholder="Login" required autofocus>
	        </p>
	        <p>
	          <label for="password" class="sr-only">Hasło</label>
	          <input type="password" id="password" name="password" class="form-control" placeholder="Hasło" required>
	        </p>
				<input name="_csrf" type="hidden" value="21720837-cb10-4314-9350-76e2dc4660fa" />
	        <button id="sign-in" class="btn btn-lg btn-primary btn-block" type="submit">Zaloguj się</button>
	      </form>
	      
	      <p style="text-align: center; color: red;">
	      		<span class="error">${credentialsError}</span>
	      </p>
	      
	      <p style="text-align: center;">
	      		<a id="forgot-password" href="/forgot-password">Zapomniałeś hasła?</a>
	      </p>
	      
	      <p style="text-align: center;">
	      		<a id="sign-up" href="/registration">Zarejestruj się</a>
	      </p>
	      
      </div>
</body>
</html>