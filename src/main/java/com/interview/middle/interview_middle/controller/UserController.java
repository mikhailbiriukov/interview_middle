package com.interview.middle.interview_middle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private Map<User, Integer> userHashes = new HashMap<>();

    @Autowired
    private DataSource dataSource;

    @GetMapping("/getUser")
    @Transactional
    List<User> getAllUsers() {
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            List<User> result = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery("select * from users");
            while (resultSet.next()) {
                User user = User.builder()
                        .id(resultSet.getInt(1))
                        .name(resultSet.getString(2))
                        .age(resultSet.getInt(3))
                        .build();
                result.add(user);
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @PostMapping("/createNewUser")
    @Transactional
    Integer newUser(@RequestBody User newUser) {
        if (newUser.getId() != null) {
            throw new RuntimeException();
        }
        if (newUser.getAge() < 10 || newUser.getAge() > 100) {
            throw new RuntimeException();
        }
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            String query = "INSERT INTO users (name, age) VALUES ('" + newUser.getName() + "', " + newUser.getAge() + ") RETURNING ID";
            System.out.println(query);
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                userHashes.put(newUser, newUser.hashCode());
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @GetMapping("/getUser/{id}")
    @Transactional
    User one(@PathVariable Integer id) {
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            List<User> result = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery("select * from users where id = " + id);
            if (resultSet.next()) {
                User user = User.builder()
                        .id(resultSet.getInt(1))
                        .name(resultSet.getString(2))
                        .age(resultSet.getInt(3))
                        .build();
                return user;
            }
            throw new RuntimeException();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
