package com.jegan.blogapplication.dao;

import com.jegan.blogapplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User,Integer> {

    @Query("SELECT u FROM User u WHERE name = :name")
    User findUserNyName(@Param("name") String name);
}
