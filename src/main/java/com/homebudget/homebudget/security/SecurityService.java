package com.homebudget.homebudget.security;

public interface SecurityService {
	String findLoggedInUsername();
    void autoLogin(String username, String password);
}
