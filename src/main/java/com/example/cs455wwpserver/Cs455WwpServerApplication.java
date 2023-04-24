package com.example.cs455wwpserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Cs455WwpServerApplication {
    private long lastTime = System.nanoTime();

    @Autowired
    private Ball gameBall;
    public static void main(String[] args) {

        SpringApplication.run(Cs455WwpServerApplication.class, args);
    }

    @GetMapping("/getPosition")
    public String givePosition() {
        System.out.println("connection");
        long curTime = System.nanoTime();
        gameBall.updatePosition((curTime - this.lastTime) / 1E9);
        this.lastTime = curTime;
        return gameBall.toString();
    }



}
