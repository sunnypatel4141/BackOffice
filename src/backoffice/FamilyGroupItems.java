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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Sunny Patel
 */
class FamilyGroupItems extends Products implements ActionListener, KeyListener {
    
    JDialog familyGroupItemsDialog = new JDialog(frame, "Family Group", true);
    
    String FamilyGroupID = "";
    String FamilyGroupName = "";
    JButton closeBtn, saveBtn, deleteBtn;
    JTextField barcodeFld, groupNameFld, groupIDFld;
    String[] fgicn = {"Product ID", "Name"};
    Object[][] fgidata = null;
    DefaultTableModel fgidtm = new DefaultTableModel(fgidata, fgicn);
    JTable fgitable = new JTable(fgidtm);
    JScrollPane fgisp = new JScrollPane(fgitable);
    HashMap pridHash = new HashMap();
    
    /**
     * When we want to view an existing family Group of items
     * Also a boolean to show if to show value or not
     */
    public FamilyGroupItems(String familyID, boolean showWindow) {
        FamilyGroupID = familyID;
        if (showWindow && loadData()) {
            renderWindow();
        }
    }
    
    public FamilyGroupItems(String GroupNameArg) {
        FamilyGroupName = GroupNameArg;
        renderWindow();
    }    
    
    private void renderWindow() {
        JPanel inputPnl = new JPanel();
        JPanel tablePnl = new JPanel();
        JPanel controlPnl = new JPanel();
        
        JLabel productNameLbl = new JLabel("Group Name");
        groupNameFld = new JTextField(7);
        groupNameFld.setFont(h2);
        groupNameFld.setText(FamilyGroupName);
        
        groupIDFld = new JTextField(7);
        groupIDFld.setFont(h2);
        groupIDFld.setText(FamilyGroupID);
        groupIDFld.setEnabled(false);
        
        JLabel barcodeLbl = new JLabel("Barcode");
        barcodeFld = new JTextField(7);
        barcodeFld.addKeyListener(this);
        barcodeFld.setFont(h3);
        
        inputPnl.add(new JLabel("Family Group ID"));
        inputPnl.add(groupIDFld);
        inputPnl.add(productNameLbl);
        inputPnl.add(groupNameFld);
        inputPnl.add(barcodeLbl);
        inputPnl.add(barcodeFld);
        
        inputPnl.setLayout(new GridLayout(3, 2));
        
        closeBtn = new JButton("Close");
        closeBtn.addActionListener(this);
        saveBtn = new JButton("Save");
        saveBtn.addActionListener(this);
        deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(this);
        
        if(FamilyGroupID.equals("")) {
            deleteBtn.setEnabled(false);
        }
        
        controlPnl.add(deleteBtn);
        controlPnl.add(saveBtn);
        controlPnl.add(closeBtn);
        controlPnl.setPreferredSize(new Dimension(300, 70));
        controlPnl.setLayout(new GridLayout(1, 3));
        
        tablePnl.add(fgisp);
        
        familyGroupItemsDialog.add(inputPnl, BorderLayout.NORTH);
        familyGroupItemsDialog.add(tablePnl, BorderLayout.CENTER);
        familyGroupItemsDialog.add(controlPnl, BorderLayout.SOUTH);
        
        //familyGroupItemsDialog.setLayout(new FlowLayout());
        familyGroupItemsDialog.setSize(700, 600);
        familyGroupItemsDialog.setVisible(true);
    }
    
    private boolean loadData() {
        boolean validFamily = false;
        try {
            String sql = "select * from familygroup where id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, FamilyGroupID);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                validFamily = true;
                FamilyGroupName = rs.getString("name");
            }
            
            if(validFamily) {
                sql = "select * from searchproducts where prid in "
                        + "(select prid from familygroupitems where grid = ? )";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, FamilyGroupID);
                rs = pstmt.executeQuery();
                while(rs.next()) {
                    pridHash.put(rs.getString("prid"), 1);
                    Object[] row  = {rs.getString("prid"), rs.getString("name") };
                    fgidtm.addRow(row);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid FamilyGroup", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FamilyGroupItems.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return validFamily;
    }
    
    
    /**
     * Deletes the items given by the id that init this class
     */
    public void deleteFamilyGroupItems() {
        
    }
    
    /**
     * Currently we only check for the name being present
     */
    //TODO: Add more validation
    private boolean validate() {
        boolean valid = true;
        if (groupNameFld.getText().equals("")) {
            valid = false;
            JOptionPane.showMessageDialog(null, "Name cannot be blank",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return valid;
    }
    
    /**
     * Saves Family Group Items
     * to the familygroupitems table
     */
    private void saveFamilyGroupItems() {
        // update or create product
        if (FamilyGroupID.equals("")) {
            // Create this group
            createFamilyGroup();
            createFamilyGroupItems();
        } else {
            updateFamilyGroupItems();
        }
        try {
            for(int i = 0; i < fgidtm.getRowCount(); i++) {
                String pridStr = fgidtm.getValueAt(i, 0).toString();
                // If this is a new product add it
                if (!pridHash.containsKey(pridStr)) {
                    String sql = "select * from familygroupitems where prid = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, pridStr);
                    rs = pstmt.executeQuery();
                    boolean found = false;
                    String grid = "";
                    while(rs.next()) {
                        found = true;
                        grid = rs.getString("grid");
                    }
                    if (found && !grid.equals(FamilyGroupID)) {
                        JOptionPane.showMessageDialog(frame, "Product already exists for Family Group ID " + grid +
                            " Please remove it and reassign", "Update Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(FamilyGroupItems.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Find Scanned Product else display message
     */
    private void findAndAddProduct(String barcodeArg) {
        try {
            String sql = "select * from searchproducts where barcode = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, barcodeArg);
            rs = pstmt.executeQuery();
            boolean notFound = true;
            Object row[] = new Object[2];
            while(rs.next()) {
                notFound = false;
                row[0] = rs.getString("prid");
                row[1] = rs.getString("name");
            }
            
            if (notFound) {
                JOptionPane.showMessageDialog(familyGroupItemsDialog, "Product Not Found",
                        "Product not found", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // If this product is elsewhere then it will not be inserted
            if (isAssignmentElsewhere(row[0])) {
                JOptionPane.showMessageDialog(familyGroupItemsDialog, 
                        "Product assigned to another familygroup", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                if (isAlreadyInTable(row[0].toString())) {
                    fgidtm.addRow(row);
                } else {
                    JOptionPane.showMessageDialog(familyGroupItemsDialog, 
                        "Product already in the table", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(FamilyGroupItems.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Checks if the prid is already in the table
     * @param String prid
     */
    private boolean isAlreadyInTable(String pridArg) {
        
        boolean addThisID = true;
        for(int i = 0; i < fgitable.getRowCount(); i++) {
            String pridStr = fgitable.getValueAt(i, 0).toString();
            // If the id's match then its in the table
            if(pridArg.equals(pridStr) ){
                addThisID = false;
                break;
            }
        }
        
        return addThisID;
    }
    
    /**
     * Checks if this product is assigned to another family group
     */
    private boolean isAssignmentElsewhere(Object prid) {
        boolean assignedElsewhere = false;
        try {
            String sql = "select * from familygroupitems where prid = ? "
                    + "and ifnull(grid, 'void') != ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setObject(1, prid);
            pstmt.setObject(2, FamilyGroupID);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                assignedElsewhere = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(FamilyGroupItems.class.getName()).log(Level.SEVERE, null, ex);
        }
        return assignedElsewhere;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        Object trigger = ae.getSource();
        if (trigger == closeBtn) {
            familyGroupItemsDialog.dispose();
        } else if (trigger == saveBtn) {
            if (validate()) {
                saveFamilyGroupItems();
            }
            familyGroupItemsDialog.dispose();
        } else if (trigger == deleteBtn) {
            deleteFamilyGroupItems();
            familyGroupItemsDialog.dispose();
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
            // We have scanned it and saved it
            String barcode = barcodeFld.getText();
            // Clear it to stop further triggers
            if(!barcode.equals("")) {
                findAndAddProduct(barcode);
            }
            barcodeFld.setText("");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Adds the family group table to the database
     * no validation is done here 
     * assumption is it is not required here 
     * it should be done elsewhere
     */
    private void createFamilyGroupItems() {
        try {
            String sql = "insert into familygroupitems(prid, grid) values (?, ?)";
            pstmt = conn.prepareStatement(sql);
            
            String sql2 = "update product set grid = ? where id = ?";
            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            for(int i = 0; i < fgitable.getRowCount(); i++) {
                String pridStr = fgidtm.getValueAt(i, 0).toString();
                pstmt.setString(1, pridStr);
                pstmt.setString(2, FamilyGroupID);
                pstmt.executeUpdate();
                
                // now the product
                pstmt2.setString(1, FamilyGroupID);
                pstmt2.setString(2, pridStr);
                pstmt2.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(FamilyGroupItems.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Creates the family groups and sets it in this class
     */
    private void createFamilyGroup() {
        try {
            String sql = "insert into familygroup (name) values(?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, groupNameFld.getText());
            pstmt.executeUpdate();
            
            sql = "select * from familygroup order by id desc limit 1";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                FamilyGroupID = rs.getString("id");
            }
        } catch (SQLException ex) {
            Logger.getLogger(FamilyGroupItems.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * This does thee update of the group name and items
     */
    private void updateFamilyGroupItems() {
        try {
            String sql = "update familygroup set name = ? where id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, groupNameFld.getText());
            pstmt.setString(2, FamilyGroupID);
            pstmt.executeUpdate();
            
            // Before proceeding we need to update the product table
            sql = "update product set grid = '0' where id in ( select prid from familygroupitems where grid = ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, FamilyGroupID);
            pstmt.executeUpdate();
            
            // Just remove all the current products for this family and reinsert them
            sql = "delete from familygroupitems where grid = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, FamilyGroupID);
            pstmt.executeUpdate();
            createFamilyGroupItems();
        } catch (SQLException ex) {
            Logger.getLogger(FamilyGroupItems.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
