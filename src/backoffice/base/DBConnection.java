/*
 * Copyright (C) 2015 Sunny Patel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * @author Sunny Patel
 */
package backoffice.base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sunny Patel
 */
public class DBConnection extends XMLSettings {
       
    // Declare Default Objects
    public static Connection conn = null;
    public static Statement stmt = null;
    public static PreparedStatement pstmt = null;
    public static ResultSet rs = null;
    String url = "jdbc:mysql://localhost:3306/epos";
    String un = "root";
    String pw = "1234";
    
    // Decalre external objects
    static Connection connext = null;
    Statement stmtext = null;
    ResultSet rsext = null;
    String urlext = "jdbc:mysql://localhost:3306/dataconverter";
    String unext = "reader";
    String pwext = "1234";

    void loadSettingsObjects() {
        if ( Settings.size() > 0 ) {
            // No need to load again we have everything.
        } else {
            // Just load the static object and we will be on out way.
            loadSettings();
        }
    }
    
    public Connection getConnectionext() throws SQLException {
        loadSettingsObjects();
        String extun = Settings.get("userext").toString();
        String extpw = Settings.get("passext").toString();
        String exturl = Settings.get("urlext").toString();
        // Lets process the password
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connext = DriverManager.getConnection(exturl, extun, extpw);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connext;
    }

    public Connection getConnection() {
        loadSettingsObjects();
        String password = Settings.get("password").toString();
        String user = Settings.get("user").toString();
        String url = Settings.get("url").toString();
        // Convert integer password
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            if ( conn == null ) {
                conn = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return conn;
    }
}
