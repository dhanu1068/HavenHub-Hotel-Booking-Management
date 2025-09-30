package com.dailycodework.lakesidehotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LakeSideHotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(LakeSideHotelApplication.class, args);
    }

}
