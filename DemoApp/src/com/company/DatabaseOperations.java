package com.company;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseOperations {
    Connection conn;

    public DatabaseOperations() {
        connect();
    }
    /**
     * Connect to a sample database
     */
    public void connect() {
        conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:database.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ResultSet getFaults() {
        String sqlString = "SELECT name FROM Categories WHERE type='ariza';";
        return getResultSet(sqlString);
    }
    public ResultSet getOrders() {
        String sqlString = "SELECT name FROM Categories WHERE type='siparis';";
        return getResultSet(sqlString);
    }

    public ResultSet getFaultsDetail() {
        String sqlString = "SELECT * FROM Categories WHERE type='ariza';";
        return getResultSet(sqlString);
    }
    public ResultSet getOrdersDetail() {
        String sqlString = "SELECT * FROM Categories WHERE type='siparis';";
        return getResultSet(sqlString);
    }

    private ResultSet getResultSet(String sqlString) {
        Statement statement = null;
        try {
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlString);
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertData(String sqlString) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(sqlString);
        statement.executeUpdate();
    }

    public boolean checkIfUpdated() {
        String sqlString = "SELECT 1 FROM Datas WHERE date=date('now')";
        Statement statement = null;
        try {
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlString);
            if(resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int calculateNecessaryJobNumber(String jobName,int jobNumber) {
        String sqlString = "SELECT percentage FROM Categories WHERE name='"+jobName+"';";
        int percentage,necessaryJob=jobNumber;
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlString);
            if(resultSet.next()) {
                percentage = resultSet.getInt("percentage");
                necessaryJob = (jobNumber * percentage) / 100;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return necessaryJob;
    }

    public ResultSet sortByPriorities() {
        String sqlString = "SELECT priority,name FROM Categories;";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlString);
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Integer> getAllAutoNumbers(boolean isFault) {
        ArrayList<Integer> generatedNumbers = new ArrayList<>();
        if(isFault) {
            ResultSet faults = getFaultsDetail();
            try {
                while (faults.next()) {
                    generatedNumbers.add(getAutoNumber(faults.getString("codename")));
                }
            } catch (SQLException e) {
                e.getStackTrace();
            }
        } else {
            ResultSet orders = getOrdersDetail();
            try {
                while (orders.next()) {
                    generatedNumbers.add(getAutoNumber(orders.getString("codename")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return generatedNumbers;
    }

    public int getAutoNumber(String columnName) {
        String sqlString = "SELECT SUM("+columnName+") FROM Datas;";
        int number=0;
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlString);
            while(resultSet.next()) {
                number = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return number;
    }

}