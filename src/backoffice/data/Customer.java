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
package backoffice.data;

import backoffice.base.DBConnection;
import static backoffice.base.DBConnection.conn;
import static backoffice.base.DBConnection.pstmt;
import static backoffice.base.DBConnection.rs;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Sunny Patel
 */
public class Customer extends DBConnection {
    
    String customerID = "";
    String cFirstName = "";
    String cLastName = "";
    String cAddress1 = "";
    String cAddress2 = "";
    String cPostcode = "";
    String cPhone1 = "";
    String cMobile = "";
    String cLimit = "";
    
    Vector paymentsData = new Vector();
    /**
     * Inititlaises this class with a customer
     */
    public Customer(String customerIDArg) {
        setCustomer(customerIDArg);
    }

    /**
     * Creates an empty customer Data object which we can pass information to
     */
    public Customer() {
        // Does Nothing...
    }
    
    /**
     * Sets customer id and loads their information
     * @param String customerID
     */
    public void setCustomer(String customerIDArg) {
        customerID = customerIDArg;
        loadCustomer();
    }
    
    public String getCustomerID() {
        return customerID;
    }
    
    public String getCustomerFirstName() {
        return cFirstName;
    }
    
    public String getCustomerLastName() {
        return cLastName;
    }
    
    public String getCustomerAddess1() {
        return cAddress1;
    }
    
    public String getCustomerAddress2() {
        return cAddress2;
    }
    
    public String getCustomerPostcode() {
        return cPostcode;
    }
    
    public String getCustomerMobile() {
        return cMobile;
    }
    
    public String getCustomerLimit() {
        return cLimit;
    }
    
    public Vector getPaymentsData() {
        return paymentsData;
    }
    
    public void setCustomerFirstName(String arg) {
         cFirstName = arg;
    }
    
    public void setCustomerLastName(String arg) {
        cLastName = arg;
    }
    
    public void setCustomerAddess1(String arg) {
        cAddress1 = arg;
    }
    
    public void setCustomerAddress2(String arg) {
        cAddress2 = arg;
    }
    
    public void setCustomerPostcode(String arg) {
        cPostcode = arg;
    }
    
    public void setCustomerMobile(String arg) {
        cMobile = arg;
    }
    
    public void setCustomerLimit(String arg) {
        cLimit = arg;
    }
    
    public void saveData() {
        // If we are adding a new customer then ...
        if (customerID.equals("")) {
            createNewCustomer();
        } else {
            updateCustomer();
        }
    }
    
    private void createNewCustomer() {
                    try {
                String sql = "insert into customer ("
                        + "first_name, last_name, address1, "
                        + "address2, postcode, phone1, "
                        + "mobile, limit) "
                        + "values ( ?, ?, ?, ?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, cFirstName);
                pstmt.setString(2, cLastName);
                pstmt.setString(3, cAddress1);
                pstmt.setString(4, cAddress2);
                pstmt.setString(5, cPostcode);
                pstmt.setString(6, cPhone1);
                pstmt.setString(7, cMobile);
                pstmt.setString(8, cLimit);
                pstmt.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    private void updateCustomer() {
        try {
            String sql = "update customer set first_name = ?, "
                    + "last_name = ?,"
                    + "address1 = ?,"
                    + "address2 = ?,"
                    + "postcode = ?,"
                    + "phone1 = ?,"
                    + "mobile = ?,"
                    + "limit = ?,"
                    + "where id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cFirstName);
            pstmt.setString(2, cLastName);
            pstmt.setString(3, cAddress1);
            pstmt.setString(4, cAddress2);
            pstmt.setString(5, cPostcode);
            pstmt.setString(6, cPhone1);
            pstmt.setString(7, cMobile);
            pstmt.setString(8, cLimit);
            pstmt.setString(9, customerID);
            pstmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Check if the customer exists
     */
    private void loadCustomer() {
        boolean customerExists = false;
        try {
            String sql = "select * from customer where id= ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, customerID);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                customerExists = true;
                cFirstName = rs.getString("first_name");
                cLastName = rs.getString("last_name");
                cAddress1 = rs.getString("address1");
                cAddress2 = rs.getString("address2");
                cPostcode = rs.getString("postcode");
                cPhone1 = rs.getString("phone1");
                cMobile = rs.getString("mobile");
                cLimit = rs.getString("limit");
            }
            
            if (!customerExists) {
                JOptionPane.showMessageDialog(null, "Customer not found", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                // Sends us back if there was an issue
                return;
            }
            
            sql = "select * from customerpayment where cuid = ? and paid= '1'";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String[] row = {rs.getString("saleid"), 
                    rs.getString("amount"), 
                    rs.getString("created")
                };
                paymentsData.add(row);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}
