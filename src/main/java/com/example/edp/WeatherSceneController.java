package com.example.edp;

import com.example.edp.Database.GetProperties;
import com.example.edp.TimeZones.TimeZones;
import com.example.edp.Weather.CurrentWeather;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class WeatherSceneController {
    GetProperties getProperties = GetProperties.instance();
    private String key = getProperties.owmKey;
    private String googleKey = getProperties.googleKey;
    private String URL = "https://api.openweathermap.org/data/2.5/weather?";
    private String IconUrl = "https://openweathermap.org/img/wn/";
    private String timeZoneURL = "https://maps.googleapis.com/maps/api/timezone/json?location=";
    DecimalFormat df = new DecimalFormat("###.##");
    public double lat;
    public double lng;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private double tmp;
    private double press;
    private double hum;
    private double wind;
    private String desc;
    private String iconID;
    private String iconLink;
    private volatile boolean stop = false;
    private String timeZoneID;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    @FXML
    Label descLabel;
    @FXML
    Label tmpLabel;
    @FXML
    Label pressLabel;
    @FXML
    Label humLabel;
    @FXML
    Label windLabel;
    @FXML
    ImageView imgView;
    @FXML
    Label timeLabel;

    public void getCoordinates(double latitude, double longitude) {
        lat = latitude;
        lng = longitude;
        getJSON();
        setWeather();
        setClock();
    }

    private void setClock() {
        getTimeZoneJSON();
        new Thread(()->{

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone(timeZoneID));
            while(!stop){
                try{
                    Thread.sleep(1000);
                } catch (Exception e){
                    System.out.println(e);
                }
                final String time = sdf.format(new Date());

                Platform.runLater(() -> {
                    timeLabel.setText(time);
                });
            }
        }).start();
    }
    public void getTimeZoneJSON(){
        try {
            Client client = Client.create();
            WebResource webResource = client.resource(getGoogleURL());
            ClientResponse clientResponse = webResource.get(ClientResponse.class);
            if(clientResponse.getStatus() != 200) {
                throw new RuntimeException("HTTP exception" + clientResponse.getStatus());
            }
            String jsonTimeZone = clientResponse.getEntity(String.class);

            ObjectMapper mapper = new ObjectMapper();
            TimeZones timeZones = mapper.readValue(jsonTimeZone, TimeZones.class);
            timeZoneID = timeZones.getTimeZoneId();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getGoogleURL() {
        return timeZoneURL + lat + "%2C" + lng + "&timestamp=1331161200" + "&key=" + googleKey;
    }

    public void setWeather() {
        tmpLabel.setText(Double.toString(tmp)+"°C");
        pressLabel.setText(Double.toString(press)+ " hPa");
        humLabel.setText(Double.toString(hum) + "%");
        windLabel.setText(String.valueOf(df.format(wind)) + " km/h");
        descLabel.setText(desc);

        Image image = new Image(iconLink);
        imgView.setImage(image);
    }
    public void goBack(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("MapsScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public String getURL() {
        return URL + "lat=" + lat + "&lon=" + lng + "&units=metric&appid=" + key;
    }

    public void getJSON(){
        try {
            Client client = Client.create();
            WebResource webResource = client.resource(getURL());
            ClientResponse clientResponse = webResource.get(ClientResponse.class);
            if(clientResponse.getStatus() != 200) {
                throw new RuntimeException("HTTP exception" + clientResponse.getStatus());
            }
            String jsonWeather = clientResponse.getEntity(String.class);

            String desc1, desc2;
            ObjectMapper mapper = new ObjectMapper();
            CurrentWeather currentWeather = mapper.readValue(jsonWeather, CurrentWeather.class);
            iconID = currentWeather.getWeather().get(0).getIcon();
            iconLink = IconUrl + iconID + "@2x.png";
            desc1 = currentWeather.getWeather().get(0).getMain();
            desc2 = currentWeather.getWeather().get(0).getDescription();
            desc = desc1 + ", " + desc2;
            tmp = currentWeather.getMain().getTemp();
            press = currentWeather.getMain().getPressure();
            hum = currentWeather.getMain().getHumidity();
            wind = currentWeather.getWind().getSpeed()*3.6;
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
