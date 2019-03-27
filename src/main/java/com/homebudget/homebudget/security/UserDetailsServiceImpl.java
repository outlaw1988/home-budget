package com.homebudget.homebudget.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homebudget.homebudget.model.Authorities;
import com.homebudget.homebudget.model.User;
import com.homebudget.homebudget.service.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username).get(0);
        if (user == null) throw new UsernameNotFoundException(username);

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (Authorities authority : user.getRoles()){
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName()));
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), 
        		user.getPassword(), grantedAuthorities);
    }
}