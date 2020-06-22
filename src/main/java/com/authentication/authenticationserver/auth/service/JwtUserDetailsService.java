package com.authentication.authenticationserver.auth.service;

import com.authentication.authenticationserver.auth.ResourceNotFoundException;
import com.authentication.authenticationserver.auth.dao.UserDao;
import com.authentication.authenticationserver.auth.model.DaoUser;
import com.authentication.authenticationserver.auth.model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//the JwtUserDetailsService, autowire the UserDao bean and the BcryptEncoder
// bean. Also define the saveUser function for inserting user details-
@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws ResourceNotFoundException {
        DaoUser user = userDao.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                new ArrayList<>());
    }

    public DaoUser save(UserDTO user) {
        DaoUser newUser = new DaoUser();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        if(userExists(newUser.getUsername())){
            throw new ResourceNotFoundException("There is an account with that username: " + newUser.getUsername());
        }
        return userDao.save(newUser);
    }

    private boolean userExists(String user) {
        return userDao.findByUsername(user) != null;
    }
}
