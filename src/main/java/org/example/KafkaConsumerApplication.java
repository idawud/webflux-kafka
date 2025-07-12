package org.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KafkaConsumerApplication {
    public static void main(String[] args) {
        System.out.println("Starting KafkaConsumerApplication...");
        SpringApplication.run(KafkaConsumerApplication.class, args);
    }

    @Bean
    public CommandLineRunner printHelloOnStartup(ApplicationContext context) {
        // log // log app port
        int port = context.getEnvironment().getProperty("local.server.port", Integer.class, 8080);
        System.out.println("Application is running on port: " + port);


        return args -> System.out.println("Hello");
    }
}
