package com.example.postgres.lob.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootApplication
public class PostgresLobTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostgresLobTestApplication.class, args);
    }

}