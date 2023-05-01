package com.example.cs455wwpserver;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.vecmath.Vector3d;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
//import org.json.*;


@Service
public class Ball {
    private Vector3d position;
    private Vector3d speed;
    private Vector3d acceleration;
    private static final double EARTH_RADIUS = 6378137.0; // in meters

    String weatherAPIKey = "API KEY HERE"; //set to the api key from www.weatherapi.com

    public Ball(){
        randomPosition();
    }

    public Ball(Vector3d position, Vector3d speed, Vector3d acceleration){
        this.position = position;
        this.speed = speed;
        this.acceleration = acceleration;
    }

    public void randomPosition(){
        this.position =  new Vector3d((Math.random() * 360) - 180, (Math.random() * 180) - 90, 10);
        this.speed = new Vector3d((Math.random() * 2000) - 1000, (Math.random() * 2000) - 1000, 1000);
        this.acceleration = new Vector3d(0,0,(float) -9.8);
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

        //update ball accel based on wind speed
        if(!weatherAPIKey.equals("API KEY HERE")) {
            String urlString = "https://api.weatherapi.com/v1/current.json?key=" + weatherAPIKey + "&q=" + this.position.x + "," + this.position.y + "&aqi=no";
            String weatherJsonString = "";
            try {
                URL weatherURL = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) weatherURL.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int responseCode = connection.getResponseCode();
                if(responseCode != 200)
                    throw new RuntimeException("HttpResponseCode: " + responseCode);
                else {
                    //scan the json out of the url
                    Scanner scan = new Scanner(weatherURL.openStream());
                    while(scan.hasNext()) {
                        weatherJsonString += scan.nextLine();
                    }
                    //parse the values out of the json
                    JSONObject weatherJSON = (JSONObject) new JSONParser().parse(weatherJsonString);
                    String weatherCurrentJSONString = (String) weatherJSON.get("current");
                    JSONObject weatherCurrentJSON = (JSONObject) new JSONParser().parse(weatherCurrentJSONString);
                    int windSpeed = (int) weatherCurrentJSON.get("wind_kph");
                    int windDir = (int) weatherCurrentJSON.get("wind_degree");
                    double windDirRad = Math.toRadians(windDir);

                    //math is probably off here
                    setAcceleration(new Vector3d(windSpeed * Math.cos(windDirRad), windSpeed * Math.sin(windDirRad), this.acceleration.z));

                }
            } catch (Exception e) {
                //something went wrong when trying to connect, just set accel to 0
                setAcceleration(new Vector3d(0, 0, this.acceleration.z));
            }
            /*String weatherJson = new RestTemplate().getForObject(urlString, String.class);
            new JSONParser().parse(weatherJson).getAsJsonObject()
                            .get("current").getAsJsonObject()
                            .get()*/
            setAcceleration(new Vector3d(0, 0, this.acceleration.z));
        }
        else {
            //api key not set, ignore and set accel to 0
            setAcceleration(new Vector3d(0, 0, this.acceleration.z));
        }

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
        return  this.position.x + "," + this.position.y + "," + this.position.z + "," +  "\n" +
                this.speed.x + "," + this.speed.y + "," + this.speed.z + "," +  "\n" +
                this.acceleration.x + "," + this.acceleration.y + "," + this.acceleration.z + "," +  "\n";
    }
}
