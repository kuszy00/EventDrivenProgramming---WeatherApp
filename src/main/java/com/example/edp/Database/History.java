package com.example.edp.Database;

import javafx.beans.property.*;

public class History {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final DoubleProperty lat = new SimpleDoubleProperty();
    private final DoubleProperty lng = new SimpleDoubleProperty();

    public int getId(){
        return id.get();
    }
    public void setId(int value){
        id.set(value);
    }
    public IntegerProperty idProperty(){
        return id;
    }

    public String getName(){
        return name.get();
    }
    public void setName(String value){
        name.set(value);
    }
    public StringProperty nameProperty(){
        return name;
    }

    public Double getLat(){
        return lat.get();
    }
    public void setLat(Double value){
        lat.set(value);
    }
    public DoubleProperty latProperty(){
        return lat;
    }

    public Double getLng(){
        return lng.get();
    }
    public void setLng(Double value){
        lng.set(value);
    }
    public DoubleProperty lngProperty(){
        return lng;
    }
}
