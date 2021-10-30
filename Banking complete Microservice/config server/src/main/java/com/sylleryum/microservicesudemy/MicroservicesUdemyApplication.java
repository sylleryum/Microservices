package com.sylleryum.microservicesudemy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class MicroservicesUdemyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroservicesUdemyApplication.class, args);
    }

}
