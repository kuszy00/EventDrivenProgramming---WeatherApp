package com.example.edp.Database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//singleton class
public class GetProperties {
    static private GetProperties _instance = null;
    static public String googleKey = null;
    static public String owmKey = null;
    static public String databaseName = null;
    static public String databaseUser = null;
    static public String databasePassword = null;
    static public String url = null;
    protected GetProperties(){
        try {
            Properties p = new Properties();
            InputStream is = new FileInputStream("config.properties");
            p.load(is);
            googleKey = p.getProperty("GoogleKey");
            owmKey = p.getProperty("OWMKey");
            databaseName = p.getProperty("databaseName");
            databaseUser = p.getProperty("databaseUser");
            databasePassword = p.getProperty("databasePassword");
            url = p.getProperty("url");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static public GetProperties instance(){
        if(_instance == null) {
            _instance = new GetProperties();
        }
        return _instance;
    }
}
