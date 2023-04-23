package com.example.cs455wwpserver;

import org.springframework.stereotype.Service;

import javax.vecmath.Vector3d;

@Service
public class Ball {
    private Vector3d position;
    private Vector3d speed;
    private Vector3d acceleration;
    private static final double EARTH_RADIUS = 6378137.0; // in meters

    public Ball(){
        this.position =  new Vector3d(45, 45, 0);
        this.speed = new Vector3d(1000, 1000, 1000);
        this.acceleration = new Vector3d(0,0,(float) -9.8);
    }

    public Ball(Vector3d position, Vector3d speed, Vector3d acceleration){
        this.position = position;
        this.speed = speed;
        this.acceleration = acceleration;
    }

    public void updatePosition(double deltaTime){

        Vector3d horizontalSpeed = new Vector3d(this.speed);
        horizontalSpeed.z = 0;

        // Calculate the distance traveled in meters
        double distanceTraveled = horizontalSpeed.length()  * deltaTime;

        // Calculate the angle of travel in radians
        double angleOfTravel = Math.atan2(this.speed.y, this.speed.x);

        // Calculate the change in latitude and longitude
        double deltaLatitude = Math.toDegrees(distanceTraveled * Math.cos(angleOfTravel) /
                EARTH_RADIUS);
        double deltaLongitude = Math.toDegrees(distanceTraveled * Math.sin(angleOfTravel) /
                (EARTH_RADIUS * Math.cos(Math.toRadians(0))));

        // Calculate the change in altitude
        double deltaAltitude = this.speed.z * deltaTime;

        Vector3d deltaPosition = new Vector3d(deltaLatitude, deltaLongitude, deltaAltitude);

        position.add(deltaPosition);

        if (Math.abs(this.position.x) > 180){
            double over = this.position.x - (180 * Math.signum(this.position.x));
            this.position.x *= -1;
            this.position.x += over;
        }
        if (Math.abs(this.position.y) > 90){
            double over = this.position.y - (90 * Math.signum(this.position.y));
            this.speed.y *= -1;
            this.position.y = (90 * Math.signum(this.position.y)) - over;
        }

        if (this.position.z < 0) this.position.z = 0;

        this.speed.scaleAdd(deltaTime, this.acceleration, this.speed);

    }

    public Vector3d getSpeed() {
        return speed;
    }

    public void setSpeed(Vector3d speed) {
        this.speed = speed;
    }

    public Vector3d getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector3d acceleration) {
        this.acceleration = acceleration;
    }

    public Vector3d getPosition() {
        return position;
    }

    public void setPosition(Vector3d position) {
        this.position = position;
    }


    public String toString(){
        return  "Position: " + this.position.toString() + "\n" +
                "Speed: " + this.speed.toString() + "\n" +
                "Acceleration: " + this.acceleration.toString();
    }
}
