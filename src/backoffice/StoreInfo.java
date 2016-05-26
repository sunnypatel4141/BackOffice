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
package backoffice;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Sunny Patel
 */
//FIXME::: I DONT WORK 100 PC so please fix me
class StoreInfo extends adminWindow {

    HashMap storeInfoHash = new HashMap();
    JDialog storeSettingsDialog = new JDialog(frame, "Store Information Settings", true);
    JButton save, close;
    /** NAMING convention not good :( it should be <fieldname>Fld*/
    JTextField storeName, storeAddress, storeAddress2, storePostCode, 
            storeVat, storePhone, storeFax, buildingNameNo, webaddress;
    
    /**
     * Return the buttons to display on the main screen
     */
    public JPanel getMainScreenButtons() {
        JPanel mainScreenPnl = new JPanel();
        JButton storeInfoSettignsBtn = new JButton("<html><p style='text-align:center;'>Store <br>Settings</p></html>");
        storeInfoSettignsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // trigger this class to open
                loadStoreInfo();
                renderWindow();
            }
        });
        
        JButton taxRate = new JButton("Tax Rate");
        taxRate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Tax te = new Tax();
            }
        });
        
        JButton miscNonTax = new JButton("Misc Non Tax");
        miscNonTax.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MiscNonTax mnt = new MiscNonTax();
            }
        });
        
        JButton miscTax = new JButton("Misc Tax");
        miscTax.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MiscTax mt = new MiscTax();
            }
        });
        
        JButton payoutBtn = new JButton("Payout");
        payoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MiscPayout mt = new MiscPayout();
            }
        });
        
        mainScreenPnl.add(storeInfoSettignsBtn);
        mainScreenPnl.add(taxRate);
        mainScreenPnl.add(miscNonTax);
        mainScreenPnl.add(miscTax);
        mainScreenPnl.add(payoutBtn);
        mainScreenPnl.setLayout(new GridLayout(1, 5));
        
        
        return mainScreenPnl;
    }
    
    /**
     * Render the Store Info window
     */
    private void renderWindow() {
        /*storeName, storeAddress, storeAddress2, storePostCode, 
            storeVat, storePhone, storeFax*/
        storeName = new JTextField(7);
        JLabel storeNameLbl = new JLabel("Name");
        buildingNameNo = new JTextField(7);
        JLabel buildingNameLbl = new JLabel("Building Name/No.");
        storeAddress = new JTextField(7);
        JLabel storeAddressLbl = new JLabel("<html>Building <br>Name/No</html>");
        storeAddress2 = new JTextField(7);
        JLabel storeAddress2Lbl = new JLabel("<html>Street <br> Town</html>");
        storePostCode = new JTextField(7);
        JLabel storePostCodeLbl = new JLabel("Post Code");
        storeVat = new JTextField(7);
        JLabel storeVatLbl = new JLabel("VAT");
        storePhone = new JTextField(7);
        JLabel storePhoneLbl = new JLabel("Phone");
        storeFax = new JTextField(7);
        JLabel storeFaxLbl = new JLabel("Fax");
        webaddress = new JTextField(7);
        JLabel webaddressLbl = new JLabel("Web Address");
        
        JPanel fields = new JPanel();
        fields.add(storeNameLbl);fields.add(storeName);
        fields.add(buildingNameLbl);fields.add(buildingNameNo);
        fields.add(storeAddressLbl);fields.add(storeAddress);
        fields.add(storeAddress2Lbl);fields.add(storeAddress2);
        fields.add(storePostCodeLbl);fields.add(storePostCode);
        fields.add(storeVatLbl);fields.add(storeVat);
        fields.add(storePhoneLbl);fields.add(storePhone);
        fields.add(storeFaxLbl);fields.add(storeFax);
        fields.add(webaddressLbl);fields.add(webaddress);
        fields.setLayout(new GridLayout(9, 2));
        
        JPanel commandBtns = new JPanel();
        save = new JButton("Save");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if( validate() ) {
                    save();
                }
            }
        });
        close = new JButton("Exit");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAndCloseDialog();
            }
        });
        commandBtns.add(save);
        commandBtns.add(close);
        
        JPanel storeMainPnl = new JPanel();
        
        storeMainPnl.add(fields);
        storeMainPnl.add(commandBtns);
        storeMainPnl.setLayout(new BoxLayout(storeMainPnl, BoxLayout.PAGE_AXIS));
        
        setStoreInfo();
        
        storeSettingsDialog.add(storeMainPnl);
        storeSettingsDialog.setSize(700, 700);
        storeSettingsDialog.setVisible(true);
    }
    
    /**
     * Clears the storeInfo Dialog and closes it
     */
    private void clearAndCloseDialog() {
        // Clear and Close Dialog
        Component dialogComp[] = storeSettingsDialog.getComponents();
        for(int i = 0; i < dialogComp.length; i++) {
            storeSettingsDialog.remove(dialogComp[i]);
        }
        storeSettingsDialog.dispose();
    }
    
    /**
     * Runs validate on the base of this window
     * validation error outputs JOptionPane message
     */
    private boolean validate() {
        return true;
    }
    
    /**
     * Sets store info settings
     */
    private void setStoreInfo() {
        storeName.setText(storeInfoHash.get("name").toString());
        buildingNameNo.setText(storeInfoHash.get("buildingnameno").toString());
        storeAddress.setText(storeInfoHash.get("address").toString());
        storeAddress2.setText(storeInfoHash.get("address2").toString());
        storePostCode.setText(storeInfoHash.get("postcode").toString());
        storeVat.setText(storeInfoHash.get("vat").toString());
        storePhone.setText(storeInfoHash.get("phone").toString());
        storeFax.setText(storeInfoHash.get("fax").toString());
        webaddress.setText(storeInfoHash.get("web").toString());
    }
    
    /**
     * Load all the settings on the store info
     */
    private void loadStoreInfo() {
        try {
            String sql = "select * from storeinfo";
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                storeInfoHash.put("name", rs.getString("storename"));
                storeInfoHash.put("buildingnameno", rs.getString("buildingnameno"));
                storeInfoHash.put("address", rs.getString("address1"));
                storeInfoHash.put("address2", rs.getString("address2"));
                storeInfoHash.put("postcode", rs.getString("postcode"));
                storeInfoHash.put("vat", rs.getString("vat"));
                storeInfoHash.put("phone", rs.getString("phone"));
                storeInfoHash.put("fax", rs.getString("fax"));
                storeInfoHash.put("web", rs.getString("webaddress"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(StoreInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Save when the validation is successful.
     */
    private void save() {
        // Save the date
        try {
            // Delete all the rows then need to insert 
            // in the news rows with the correct information
            // therefore we are overwriting the data in
            // in the `storeinfo` table
            String sql = "delete from storeinfo";
            stmt.execute(sql);
            
            // Now lets manually update all the data
            sql = "insert into storeinfo (`storename`, `buildingnameno`, `address1`,"
                    + "`address2`, `postcode`, `vat`, `phone`, `fax`, `webaddress`) " +
                    "values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, storeName.getText());
            pstmt.setString(2, buildingNameNo.getText());
            pstmt.setString(3, storeAddress.getText());
            pstmt.setString(4, storeAddress2.getText());
            pstmt.setString(5, storePostCode.getText());
            pstmt.setString(6, storeVat.getText());
            pstmt.setString(7, storePhone.getText());
            pstmt.setString(8, storeFax.getText());
            pstmt.setString(9, webaddress.getText());
            pstmt.execute();
        } catch(SQLException ex) {
            Logger.getLogger(StoreInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
