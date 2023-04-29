package com.example.cs455wwpserver;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.vecmath.Vector3d;
import java.time.Instant;

@Component
public class BallBean {

    private final Ball gameBall;
    private long lastTime;
    private Instant lastLandingTime;
    private Vector3d lastLandingPosition;
    public static final double EARTH_RADIUS = 6378137.0; // Radius of the Earth in meters

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;

    }
    public BallBean(){
        this.gameBall = new Ball();
        this.lastTime = System.nanoTime();
        this.lastLandingPosition = gameBall.getPosition();
        this.lastLandingTime = Instant.now();
    }
    @Scheduled(fixedRate = 100)
    public void run(){
        long curTime = System.nanoTime();
        gameBall.updatePosition((curTime - this.lastTime) / 1E9);

        if(gameBall.getPosition().z <= 0){
            this.lastLandingTime = Instant.now();
            this.lastLandingPosition = this.getBallPosition();
            gameBall.randomPosition();
        }

        this.lastTime = curTime;
    }

    public String getBallString(){
        return this.gameBall.toString();
    }

    public Vector3d getBallPosition(){
        return gameBall.getPosition();
    }


    public Vector3d getLastLandingPosition() {
        return lastLandingPosition;
    }

    public Instant lastLandingTime() {
        return lastLandingTime;
    }
}
