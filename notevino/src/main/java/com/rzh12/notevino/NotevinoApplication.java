package com.rzh12.notevino;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NotevinoApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotevinoApplication.class, args);
    }

}
