package ru.yandex.practicum.ewmmainservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "ru.yandex.practicum",
        "ru.practicum.client"
})
public class ExploreMainApp {

    public static void main(String[] args) {
        SpringApplication.run(ExploreMainApp.class, args);
    }
}