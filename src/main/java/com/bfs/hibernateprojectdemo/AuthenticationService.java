package com.bfs.hibernateprojectdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class AuthenticationService {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationService.class, args);
    }

}
