package com.openbravo.pos.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import junit.framework.TestCase;

public class DatabaseConnectionipv6Test extends TestCase{

public void testConnection(){
	System.out.println("-------- MySQL JDBC Connection Testing ------------");

	try {
		Class.forName("com.mysql.jdbc.Driver");
	} catch (ClassNotFoundException e) {
		System.out.println("Where is your MySQL JDBC Driver?");
		e.printStackTrace();
		return;
	}

	System.out.println("MySQL JDBC Driver Registered!");
	Connection connection = null;

	try {
		connection = DriverManager
		.getConnection("jdbc:mysql://173.194.233.193/openbravopos2","testuser", "testpass");

		if (connection != null) {
			System.out.println("You made it, take control your database now!");
			
			String createTableSQL = "CREATE TABLE IF NOT EXISTS `applications` (" +
					"`ID` varchar(255) NOT NULL, " +
					"`NAME` varchar(255) NOT NULL," +
					"`VERSION` varchar(255) NOT NULL," +
					" PRIMARY KEY (`ID`)" +
					") ENGINE=InnoDB DEFAULT CHARSET=utf8; ";
			
			PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL);

			System.out.println(createTableSQL);

			// execute create SQL stetement
			preparedStatement.executeUpdate();

			System.out.println("Table \"dbuser\" is created!");
			
		} else {
			System.out.println("Failed to make connection!");
		}
		
	} catch (SQLException e) {
		System.out.println("Connection Failed! Check output console");
		e.printStackTrace();
		return;
	}

	
  }
}

