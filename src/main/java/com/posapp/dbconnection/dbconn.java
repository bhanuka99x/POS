package com.posapp.dbconnection;

import java.net.URL;
import java.sql.*;
public class dbconn {
    static String URL = "jdbc:mysql://localhost:3308/pos";
    static String Username = "root";
    static String Password = "2001";

    public static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(URL, Username, Password);
            System.out.println("Database connected!");
            return conn;
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
            return null;
        }

    }


}

