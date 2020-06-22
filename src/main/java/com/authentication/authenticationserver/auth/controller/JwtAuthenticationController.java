package com.authentication.authenticationserver.auth.controller;

import com.authentication.authenticationserver.auth.ResourceNotFoundException;
import com.authentication.authenticationserver.auth.config.JwtTokenUtil;
import com.authentication.authenticationserver.auth.dao.UserDao;
import com.authentication.authenticationserver.auth.model.DaoUser;
import com.authentication.authenticationserver.auth.model.JwtRequest;
import com.authentication.authenticationserver.auth.model.JwtResponse;
import com.authentication.authenticationserver.auth.model.UserDTO;
import com.authentication.authenticationserver.auth.service.JwtUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Function;

/*
Expose a POST API /authenticate using the JwtAuthenticationController.
The POST API gets the username and password in the body. Using the
Spring Authentication Manager, we authenticate the username and password.
If the credentials are valid, a JWT token is created using the JWTTokenUtil
 and is provided to the client.*/

@RestController
@CrossOrigin
@RequestMapping("/api/v1/auth")
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private UserDao userDao;


    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> getUser(Authentication authentication) throws ResourceNotFoundException {

        Map<String, String> response = new HashMap<>();
        response.put("Username", authentication.getName());
        return response;

    }

    @RequestMapping(value = "/user/all", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?>  getUserAll() throws ResourceNotFoundException {
        return ResponseEntity.ok(userDao.findAll());
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?>  getUserById(@PathVariable(value = "id") Long id ) throws ResourceNotFoundException {
        DaoUser daoUser = userDao.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found for this id :: " + id));
        return ResponseEntity.ok(daoUser);
    }


    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Map<String, Boolean>  deleteUserById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        DaoUser daoUser = userDao.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found for this id :: " + id));

        userDao.delete(daoUser);
        Map<String, Boolean> response = new HashMap<>();
        response.put("Deleted", Boolean.TRUE);
        return  response;

        //return ResponseEntity.ok(this.userDao.save(daoUser));
    }


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest, String token) throws Exception {

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String generateToken = jwtTokenUtil.generateToken(userDetails);
        final String username = authenticationRequest.getUsername();

        return ResponseEntity.ok(new JwtResponse(generateToken,username));
    }

    @RequestMapping(value = "/user/search", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getUser(@RequestBody UserDTO user) throws ResourceNotFoundException {
        return ResponseEntity.ok(userDetailsService.loadUserByUsername(user.getUsername()));

        /* Map<String,String> response = new HashMap<>();
        response.put("username",user.getUsername());
        //response.put("Date", new Date().toString());
        for (String f: response.keySet()) {
            ResponseEntity.ok(userDetailsService.loadUserByUsername(f));
        }
        return response;*/
        //return ResponseEntity.ok(userDetailsService.save(user));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {
        return ResponseEntity.ok(userDetailsService.save(user));
    }


    private void authenticate(String username, String password) throws ResourceNotFoundException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new ResourceNotFoundException("USER DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new ResourceNotFoundException("INVALID CREDENTIALS", e);
        }
    }


}