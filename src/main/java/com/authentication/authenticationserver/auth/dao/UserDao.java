package com.authentication.authenticationserver.auth.dao;

import com.authentication.authenticationserver.auth.model.DaoUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao extends CrudRepository<DaoUser, Long> {

    DaoUser findByUsername(String username);

}
