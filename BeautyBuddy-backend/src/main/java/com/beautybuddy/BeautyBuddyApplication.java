package com.beautybuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BeautyBuddyApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeautyBuddyApplication.class, args);
    }
}
