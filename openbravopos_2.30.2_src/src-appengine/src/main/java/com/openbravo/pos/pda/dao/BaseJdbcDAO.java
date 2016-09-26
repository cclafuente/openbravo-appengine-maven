//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//
//    This file is part of Openbravo POS.
//
//    Openbravo POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Openbravo POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.pda.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author carloscol
 * @version 1.0
 */
public abstract class BaseJdbcDAO {

    public BaseJdbcDAO() {
       /* properties = new PropertyUtils();*/
    }

   /* protected Connection getConnection() throws Exception {
        try {
            Class.forName(properties.getDriverName());
            return DriverManager.getConnection(properties.getUrl(), properties.getDBUser(), properties.getDBPassword());
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return null;
    }*/
    
    protected Connection getConnection() throws Exception {
    	Connection conn;
    	String url;
        if (System.getProperty("com.google.appengine.runtime.version").startsWith("Google App Engine/")) {
          // Check the System properties to determine if we are running on appengine or not
          // Google App Engine sets a few system properties that will reliably be present on a remote
          // instance.
        	url = System.getProperty("ae-cloudsql.cloudsql-database-url");
        	try {
        		// Load the class that provides the new "jdbc:google:mysql://" prefix.
        		Class.forName("com.mysql.jdbc.GoogleDriver");
        	} catch (ClassNotFoundException e) {
        		throw new RuntimeException("Error loading Google JDBC Driver", e);
        	}
        	} else {
        		// Set the url with the local MySQL database connection url when running locally
        		url = System.getProperty("ae-cloudsql.local-database-url");
        	}
        try {
        		conn = DriverManager.getConnection(url);
    	} catch (Exception e) {
          throw new RuntimeException("SQL error", e);
        }
    	return conn;
    }
    

    protected List transformSet(ResultSet rs) throws SQLException {
        List voList = new ArrayList();
        Object vo;
        while (rs.next()) {
            vo = map2VO(rs);
            voList.add(vo);
        }
        return voList;
    }

    protected boolean isPostgre() {
        return false;
    }

    protected abstract Object map2VO(ResultSet rs) throws SQLException;
}