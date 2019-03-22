# Home-budget web application
Home budget is a web application based on Java Spring Boot/Hibernate frameworks. It allows to enter expenditures and incomes to control home finances. Application also provides splitting into categories. 
Home budget interface currently is in Polish. Application URL: http://domowy-budzet.herokuapp.com/

## How to run the server
To run from command line type:
`mvn spring-boot:run`

## Enviromental variables

Before deploying the application, several enviroment variables are needed:

Database parameters:

- DB_URL
- DB_USERNAME
- DB_PASSWORD

Known issue on the Linux is to set variables in `.bashrc` and restart the OS.

Email (forgot password) parameters:

- MAIL_URL
- MAIL_LOGIN
- MAIL_PASSWORD

## Setting database

1. Create database and assign the url to DB_URL, password to DB_PASSWORD, username to DB_USERNAME
2. When database is correctly set, then hibernate will create tables automatically based on parameters defined in model
3. Database coding must be set to UTF-8, use commands:

`ALTER DATABASE <databasename> CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`

`ALTER TABLE <tablename> CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`

4. Create `persistence_logins` table (remember me functionality): 

`create table persistent_logins (
	username varchar(64) not null, 
	series varchar(64) primary key, 	
	token varchar(64) not null,
	last_used timestamp not null
)`