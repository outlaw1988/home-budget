package com.homebudget.homebudget.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

      auth.jdbcAuthentication().dataSource(dataSource)
          .usersByUsernameQuery("select username, password, enabled"
              + " from user where username=?")
          .authoritiesByUsernameQuery("select username, authority "
              + "from role where username=?")
          .passwordEncoder(new BCryptPasswordEncoder());
    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/login")
				.permitAll()
				.antMatchers("/registration")
				.permitAll()
				.antMatchers("/forgot-password", "/reset**")
				.permitAll()
				.antMatchers("/**")
				.access("hasRole('ROLE_USER')")
				.and()
			.formLogin()
				.loginPage("/login")
				.defaultSuccessUrl("/")
				.usernameParameter("username")
				.passwordParameter("password");
//		http
//			.authorizeRequests()
//				.antMatchers("/login")
//				.permitAll()
//				.antMatchers("/registration")
//				.permitAll()
//				.antMatchers("/forgot-password", "/reset**")
//				.permitAll()
//				.antMatchers("/**")
//				.access("hasRole('ROLE_USER')")
//				.and()
//			.formLogin()
//				.loginPage("/login")
//				.defaultSuccessUrl("/")
//				.usernameParameter("username")
//				.passwordParameter("password");
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
	    web.ignoring().antMatchers("/css/**");
	}

}
