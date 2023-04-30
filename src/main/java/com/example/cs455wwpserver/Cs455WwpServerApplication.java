package com.example.cs455wwpserver;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.vecmath.Vector3d;
import java.time.Duration;
import java.time.Instant;

@SpringBootApplication
@RestController
public class Cs455WwpServerApplication {

    @Autowired
    private BallBean ballBean;
    public static void main(String[] args) {

        SpringApplication.run(Cs455WwpServerApplication.class, args);
    }

    @GetMapping("/getPosition")
    public String givePosition() {
        System.out.println(ballBean.getBallString());
        return ballBean.getBallString();
    }

    @PostMapping("/hitBall")
    public ResponseEntity<String> checkHit(@RequestBody String body){
        String mesage;
        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(body);
            LocationPing locationPing = new LocationPing((double) obj.get("latitude"), (double) obj.get("longitude"),
                    (String) obj.get("userId"), (String) obj.get("time"));

            Vector3d ballPosition = this.ballBean.getLastLandingPosition();
            double distance = BallBean.distance(ballPosition.x, ballPosition.y, locationPing.getLatitude(), locationPing.getLongitude());

            Instant clientTime = Instant.parse(locationPing.getTime());
            long timeDiff = Math.abs(Duration.between(clientTime, this.ballBean.lastLandingTime()).getSeconds());
            if(distance <= 100 && timeDiff <= 10){
                //TODO Actually update the score
                mesage = "Success";
            }else{
                mesage = "Fail";
            }
        } catch (org.json.simple.parser.ParseException e) {
            System.out.println(e.getMessage());
            mesage = "Fail";
        }

        return ResponseEntity.ok(mesage);
    }

}
