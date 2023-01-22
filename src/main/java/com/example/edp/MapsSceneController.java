package com.example.edp;

import com.example.edp.Database.DatabaseConnection;
import com.example.edp.Database.GetProperties;
import com.example.edp.Database.History;
import com.example.edp.Maps.MapsAddress;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.sql.Connection;

public class MapsSceneController implements Initializable {
    GetProperties getProperties = GetProperties.instance();
    private String key = getProperties.googleKey;
    private String URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    public double lat;
    public double lng;
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private TextField textSearch;
    @FXML
    private Label errorLabel;
    @FXML
    private WebView webView;
    @FXML
    private TableView<History> table;
    @FXML
    private TableColumn<History, String> placeColumn;
    @FXML
    private TableColumn deleteColumn;
    @FXML
    private TableColumn selectColumn;
    private WebEngine engine;
    ObservableList<History> history;

    public void search(ActionEvent event) {
        try {
            String address = textSearch.getText();
            getJSON(address.replaceAll(" ", "%20"));
        } catch (Exception e) {
            errorLabel.setText("Error");
        }
        loadMap();
    }

    public void loadMap() {
        engine.load("https://maps.googleapis.com/maps/api/staticmap?center="+lat+","+lng+"&size=600x450&markers=%7C"+lat+","+lng+"&zoom=15&key="+key);
    }

    public void showForecast(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WeatherScene.fxml"));
        root = loader.load();

        WeatherSceneController weatherSceneController = loader.getController();
        weatherSceneController.getCoordinates(lat,lng);

        saveToDataBase();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private Task saveToDataBase() {
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();
            if (checkDataBase(connectDB)) {
                //connectDB.close();
            } else {
                try {
                    String query = "INSERT INTO history (name, lat, lng) VALUES (?, ?, ?)";

                    PreparedStatement preparedStmt = connectDB.prepareStatement(query);
                    preparedStmt.setString(1, textSearch.getText());
                    preparedStmt.setDouble(2, lat);
                    preparedStmt.setDouble(3, lng);
                    preparedStmt.execute();
                    connectDB.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            return null;
    }

    private boolean checkDataBase(Connection connectDB) {
        String connectQuery = "SELECT * FROM history WHERE name = '" + textSearch.getText() + "'";
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(connectQuery);
            if(queryOutput.next() == false){
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public String getURL(String address) {
        return URL + address + "&key=" + key;
    }

    public void getJSON(String address){
        try {
            Client client = Client.create();
            WebResource webResource = client.resource(getURL(address));
            ClientResponse clientResponse = webResource.get(ClientResponse.class);
            if(clientResponse.getStatus() != 200) {
                errorLabel.setText("Error");
            }
            String jsonMaps = clientResponse.getEntity(String.class);

            ObjectMapper mapper = new ObjectMapper();
            MapsAddress mapsAddress = mapper.readValue(jsonMaps, MapsAddress.class);
            System.out.println(mapsAddress.getResults().get(0).getGeometry().getLocation().getLat());
            System.out.println(mapsAddress.getResults().get(0).getGeometry().getLocation().getLng());
            lat = mapsAddress.getResults().get(0).getGeometry().getLocation().getLat();
            lng = mapsAddress.getResults().get(0).getGeometry().getLocation().getLng();
        } catch (Exception e){
            errorLabel.setText("Error");
        }
    }

    @Override
    public void initialize(java.net.URL url, ResourceBundle resourceBundle) {
        new Thread(()->{
            showHistory();
        }).start();

        engine = webView.getEngine();
        engine.load("https://maps.googleapis.com/maps/api/staticmap?center=poland&size=600x450&key="+key);
    }

    private void showHistory() {
        history = FXCollections.observableArrayList();
        //Get history from database
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        String connectQuery = "SELECT * FROM history ORDER BY id desc";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(connectQuery);
            while(queryOutput.next()){
                History hist = new History();
                hist.setId(queryOutput.getInt("id"));
                hist.setName(queryOutput.getString("name"));
                hist.setLat((queryOutput.getDouble("lat")));
                hist.setLng((queryOutput.getDouble("lng")));

                history.add(hist);
            }
            connectDB.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        //Insert history to table
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        //Adding buttons
        new Thread(()->{
            Callback<TableColumn<History,String>, TableCell<History,String>> cellFactoryDelete = (param) -> {
                final TableCell<History,String> cell = new TableCell<History,String>(){
                    @Override
                    public void updateItem(String item, boolean empty){
                        super.updateItem(item, empty);

                        if(empty){
                            setGraphic(null);
                            setText(null);
                        } else {
                            final Button deleteButton = new Button("X");
                            deleteButton.setOnAction(event -> {
                                History h = getTableView().getItems().get(getIndex());

                                deleteHistoryItem(h.getId());
                                showHistory();
                            });
                            setGraphic(deleteButton);
                            setText(null);
                        }
                    }
                };
                return cell;
            };
            deleteColumn.setCellFactory(cellFactoryDelete);
        }).start();
        new Thread(()->{
            Callback<TableColumn<History,String>, TableCell<History,String>> cellFactorySelect = (param) -> {
                final TableCell<History,String> cell = new TableCell<History,String>(){
                    @Override
                    public void updateItem(String item, boolean empty){
                        super.updateItem(item, empty);

                        if(empty){
                            setGraphic(null);
                            setText(null);
                        } else {
                            final Button selectButton = new Button("SELECT");
                            selectButton.setOnAction(event -> {
                                History h = getTableView().getItems().get(getIndex());
                                lat = h.getLat();
                                lng = h.getLng();
                                textSearch.setText(h.getName());
                                loadMap();
                            });
                            setGraphic(selectButton);
                            setText(null);
                        }
                    }
                };
                return cell;
            };
            selectColumn.setCellFactory(cellFactorySelect);
        }).start();

        table.setItems(history);
    }

    private void deleteHistoryItem(int id) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        String query = "DELETE FROM history WHERE id = ?";
        try{
            PreparedStatement preparedStmt = connectDB.prepareStatement(query);
            preparedStmt.setInt(1, id);
            preparedStmt.execute();
            connectDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
