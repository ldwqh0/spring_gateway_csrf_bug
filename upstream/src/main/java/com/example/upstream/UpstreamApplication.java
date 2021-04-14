package com.example.upstream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class UpstreamApplication {


    @PostMapping("/test")
    public String postData(Dto dto) {
        return "post success";
    }

    public static void main(String[] args) {
        SpringApplication.run(UpstreamApplication.class, args);
    }

}
