package com.homebudget.homebudget.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
        	.userDetailsService(userDetailsService)
        	.passwordEncoder(new BCryptPasswordEncoder());
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/resources/**", "/registration", "/forgot-password", "/reset**")
                	.permitAll()
                .anyRequest()
                .authenticated()
            .and()
            	.formLogin()
                	.loginPage("/login")
                	.permitAll()
            .and()
            	.logout()
                	.permitAll();
    }
    
    
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
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
//			.and()
//				.formLogin()
//				.loginPage("/login")
//				.defaultSuccessUrl("/")
//				.usernameParameter("username")
//				.passwordParameter("password")
//			.and()
//				.rememberMe()
//				.rememberMeCookieName("home-budget-login-cookie")
//				.tokenValiditySeconds(365 * 24 * 60 * 60)
//				.rememberMeParameter("remember-me")
//				.tokenRepository(persistentTokenRepository())
//			.and()
//				.logout()
//				.permitAll()
//				.deleteCookies("auth_code", "home-budget-login-cookie");
//				
//	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
	    web.ignoring().antMatchers("/css/**", "/jquery/**");
	}
	
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

}
