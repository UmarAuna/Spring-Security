package com.authentication.authenticationserver.auth.model;

import java.io.Serializable;
import java.util.Date;

//This class is required for creating a response containing
// the JWT to be returned to the user.
public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;
    private final String username;


    public JwtResponse(String jwttoken, String username ) {
        this.jwttoken = jwttoken;
        this.username = username;
    }

    public String getToken() {
        return this.jwttoken;
    }

    public String getUsername() {
        return username;
    }

}
