package com.example.edp.Database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public Connection databaseLink;
    public Connection getConnection(){
        GetProperties getProperties = GetProperties.instance();
        String databaseName = getProperties.databaseName;
        String databaseUser = getProperties.databaseUser;
        String databasePassword = getProperties.databasePassword;
        String url = getProperties.url + databaseName;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        } catch (Exception e){
            e.printStackTrace();
        }
        return databaseLink;
    }
}
