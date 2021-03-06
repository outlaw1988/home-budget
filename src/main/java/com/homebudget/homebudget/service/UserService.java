package com.homebudget.homebudget.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.homebudget.homebudget.model.Authorities;
import com.homebudget.homebudget.model.User;


@Service("userService")
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public List<User> findUserByUsername(String login) {
    	return userRepository.findByUsername(login);
    }
    
    public List<User> findUserByEmail(String email) {
    	return userRepository.findByEmail(email);
    }
    
    public List<User> findUserByResetToken(String resetToken) {
    	return userRepository.findByResetToken(resetToken);
    }
    
    public void saveUserAndSet(User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setEnable(1);
        user.setRoles(new HashSet<Authorities>(roleRepository.findAll()));
        //User userReturned = userRepository.save(user);
        userRepository.save(user);
        
//        if (!updateMode) {
//        	Authorities role = new Authorities();
////            role.setUsername(userReturned.getUsername());
////            role.setAuthority("ROLE_USER");
//            
//            roleRepository.save(role);
//        }
    }
    
    public void saveUser(User user) {
    	userRepository.save(user);
    }
    
}

