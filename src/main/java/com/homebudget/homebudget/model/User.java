package com.homebudget.homebudget.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user")
public class User {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private int id;
	
	@Column(name = "username")
    @NotNull
    private String username;
	
	@Column(name = "email")
	@Email(message = "Please provide a valid e-mail")
	@NotNull
	private String email;
	
    @Column(name = "password")
    @NotNull
    private String password;
    
    @Column(name = "password_confirm")
    @NotNull
    @Transient
    private String passwordConfirm;
    
    @Column(name = "enabled")
    private int enable;
    
    @Column(name = "reset_token")
	private String resetToken;
    
    @ManyToMany
    private Set<Authorities> roles;
    
    public User() {
    	// empty
    }

    public User(User user) {
    	this.id = user.getId();
    	this.username = user.getUsername();
    	this.password = user.getPassword();
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public int getEnable() {
		return enable;
	}

	public void setEnable(int enable) {
		this.enable = enable;
	}

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public Set<Authorities> getRoles() {
		return roles;
	}

	public void setRoles(Set<Authorities> roles) {
		this.roles = roles;
	}

	
}
