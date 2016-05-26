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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author Sunny Patel
 */
class EmployeeEditor extends Products implements ActionListener {
    
    private int employeeID = 0;
    
    JDialog employeeEditordialog = new JDialog(frame, "Employee Editer", true);
    JTextField idFld, firstNameFld, lastNameFld, loginIDFld, 
            loginPassFld, loginPassConfirmFld;
    JCheckBox canLoginChk, isDeletedChk;
    JCheckBox[] applications = new JCheckBox[77];
    JButton saveBtn, closeBtn;
    String[] cn = {"ID", "Name", "Right"};
    Object[][] data = null;
    DefaultTableModel employeeModel = new DefaultTableModel(data, cn);
    JTable employeeTable = new JTable(employeeModel);
    
    EmployeeEditor(int employeeIDArg) {
        // Employee Editor ID
        employeeID = employeeIDArg;
        renderWindow();
    }
    
    EmployeeEditor() {
        // Employee Editor Standard
        renderWindow();
    }
    
    /**
     * Render the main dialog
     */
    private void renderWindow() {
        JPanel buttonsPnl = new JPanel();
        JPanel mainPnl = new JPanel();
        
        JLabel idLbl = new JLabel("ID");
        JLabel firstNameLbl = new JLabel("First Name");
        JLabel lastNameLbl = new JLabel("Last Name");
        JLabel loginIDLbl = new JLabel("Login ID");
        JLabel loginPassLbl = new JLabel("Password");
        JLabel loginPassConfirmLbl = new JLabel("Password Confirm");
        
        // First Set the table column Editor
        TableColumn rightsModel = employeeTable.getColumnModel().getColumn(2);
        rightsModel.setCellEditor(new DefaultCellEditor(new JCheckBox()));
        
        idFld = new JTextField(7);
        firstNameFld = new JTextField(7);
        lastNameFld = new JTextField(7);
        loginIDFld = new JTextField(7);
        loginPassFld = new JTextField(7);
        loginPassConfirmFld = new JTextField(7);
        canLoginChk = new JCheckBox();
        JLabel canLoginLbl = new JLabel("Can Login");
        
        JScrollPane jspEmployee = new JScrollPane(employeeTable);
        jspEmployee.setPreferredSize(new Dimension(350, 450));
        
        mainPnl.add(idLbl);
        mainPnl.add(idFld);
        mainPnl.add(firstNameLbl);
        mainPnl.add(firstNameFld);
        mainPnl.add(lastNameLbl);
        mainPnl.add(lastNameFld);
        mainPnl.add(loginIDLbl);
        mainPnl.add(loginIDFld);
        mainPnl.add(loginPassLbl);
        mainPnl.add(loginPassFld);
        mainPnl.add(loginPassConfirmLbl);
        mainPnl.add(loginPassConfirmFld);
        mainPnl.add(canLoginLbl);
        mainPnl.add(canLoginChk);
        mainPnl.setLayout(new GridLayout(7, 2));
        
        saveBtn = new JButton("Save");
        saveBtn.addActionListener(this);
        closeBtn = new JButton("Close");
        closeBtn.addActionListener(this);
        
        buttonsPnl.add(saveBtn);
        buttonsPnl.add(closeBtn);
        buttonsPnl.setLayout(new GridLayout(1, 2));
        buttonsPnl.setPreferredSize(new Dimension(200, 70));
        
        loadTableModel();
        loadEmployeeInfo();
        
        idFld.setEditable(false);
        
        // First Set the table column Editor
        rightsModel = employeeTable.getColumnModel().getColumn(2);
        rightsModel.setCellEditor(new DefaultCellEditor(new JCheckBox()));
        
        employeeEditordialog.add(buttonsPnl);
        employeeEditordialog.add(mainPnl);
        employeeEditordialog.add(jspEmployee);
        employeeEditordialog.setLayout(new FlowLayout());
        employeeEditordialog.setSize(650, 650);
        employeeEditordialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object trigger = e.getSource();
        if(trigger == saveBtn) {
            if(validate()) {
                if(employeeID == 0) {
                    createEmployee();
                } else {
                    updateEmployee();
                }
                employeeEditordialog.dispose();
            }
        } else if(trigger == closeBtn) {
            employeeEditordialog.dispose();
        }
    }

    private boolean validate() {
        boolean returnVal = true;
        String firstNameStr = firstNameFld.getText();
        String lastNameStr = lastNameFld.getText();
        String loginPassStr = loginPassFld.getText();
        String loginPassConfirmStr = loginPassConfirmFld.getText();
        String loginIDStr = loginIDFld.getText();
        
        if(firstNameStr.equals("") || lastNameStr.equals("")) {
            JOptionPane.showMessageDialog(employeeEditordialog, "First and Last is mandatory",
                "Error", JOptionPane.ERROR_MESSAGE);
            returnVal = false;
        }
        
        if(loginIDStr.equals("") || loginPassStr.equals("")) {
            JOptionPane.showMessageDialog(employeeEditordialog, "Login ID and password is mandatory",
                "Error", JOptionPane.ERROR_MESSAGE);
            returnVal = false;
        }
        
        if(!loginPassStr.equals(loginPassConfirmStr)){
            JOptionPane.showMessageDialog(employeeEditordialog, "Passwords do not match",
                    "Password Error", JOptionPane.ERROR_MESSAGE);
            returnVal = false;
        }
        
        return returnVal;
    }

    private void createEmployee() {
        try {
            String firstNameStr = firstNameFld.getText();
            String lastNameStr = lastNameFld.getText();
            String loginPassStr = loginPassFld.getText();
            String loginIDStr = loginIDFld.getText();
            String userID = ""; // becasue this is the user id
            
            // check that the user does not alreasy exist
            String sql = "select `id` from `users` where `loginID` = ? and `loginpass` = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, loginIDStr);
            pstmt.setString(2, loginPassStr);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                userID = rs.getString(1);
            }
            
            if (userID.equals("")) {
                sql = "insert into `users` (`firstname`, `lastname`, " +
                    "`loginID`, `loginpass`, `canlogin`) " +
                    "select ?,?,?,?,? from `users` where not exists " +
                    "(select * from `users` where `loginID` = ?) limit 1";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, firstNameStr);
                pstmt.setString(2, lastNameStr);
                pstmt.setString(3, loginIDStr);
                pstmt.setString(4, loginPassStr);
                pstmt.setInt(5, canLoginChk.isSelected() ? 1 : 0);
                pstmt.setString(6, loginIDStr);
                pstmt.execute();

                sql = "select `id` from `users` where `loginID` = ? and `loginpass` = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, loginIDStr);
                pstmt.setString(2, loginPassStr);
                rs = pstmt.executeQuery();
                while(rs.next()) {
                    loginIDStr = rs.getString(1);
                }

                createApplicationRights(loginIDStr);
            }

            JOptionPane.showMessageDialog(null, "Employee Created");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Employee Creation Error", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(EmployeeEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateEmployee() {
        try {
            String firstNameStr = firstNameFld.getText();
            String lastNameStr = lastNameFld.getText();
            String loginPassStr = loginPassFld.getText();
            String loginIDStr = loginIDFld.getText();
            boolean canLoginBol = canLoginChk.isSelected();
            String userID = ""; // becasue this is the user id
            
            String sql = "update `users` set `firstname` = ?,`lastname` = ?, " +
                "`loginID` = ?,`loginpass` = ?,`canlogin` = ? where `id` = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, firstNameStr);
            pstmt.setString(2, lastNameStr);
            pstmt.setString(3, loginIDStr);
            pstmt.setString(4, loginPassStr);
            pstmt.setBoolean(5, canLoginBol);
            pstmt.setInt(6, employeeID);
            pstmt.execute();
            
            updateApplicationRights();
            
            JOptionPane.showMessageDialog(null, "Employee Updated");
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * This now loads the employee information
     */
    private void loadEmployeeInfo() {
        try {
            String sql = "select * from `users` where `id` = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeID);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                idFld.setText(rs.getString("id"));
                firstNameFld.setText(rs.getString("firstname"));
                lastNameFld.setText(rs.getString("lastname"));
                loginIDFld.setText(rs.getString("loginID"));
                loginPassFld.setText(rs.getString("loginpass"));
                loginPassConfirmFld.setText(rs.getString("loginpass"));
                canLoginChk.setSelected(rs.getBoolean("canlogin"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Load all the rows in the model
     */
    private void loadTableModel() {
        try {
            /*String sql = "select `ar`.`id`, `ap`.`description`, `ar`.`r` " +
                "from `application` `ap` left join `applicationright` `ar` on " +
                "`ap`.`id` = `ar`.`apid`";
             */
            
            String sql = "select * from application";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                Object[] row = {rs.getString(1), rs.getString(3), false};
                employeeModel.addRow(row);
            }
            
            // Get the specific rights for the given employee
            HashMap rights = new HashMap();
            sql = "select * from applicationright where userid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeID);
            rs = pstmt.executeQuery();
            
            while(rs.next()) {
                // Build a HashMap Pairs of apid and read right
                rights.put(rs.getString("apid"), rs.getString("r"));
            }
            
            // Go through the table and update as needed
            for(int i = 0; i < employeeModel.getRowCount(); i++) {
                String applicationID = employeeModel.getValueAt(i, 0).toString();
                
                if(rights.containsKey(applicationID)) {
                    if(rights.get(applicationID).toString().equals("1")) {
                        employeeModel.setValueAt(true, i, 2);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void createApplicationRights(String loginIDStr) {
        try {
            String sql = "insert into `applicationright` "+
                "(`apid`, `userid`, `r`) values(?, ?, ?) ";
            for(int i = 0; i < employeeModel.getRowCount(); i++) {
                String apidStr = employeeModel.getValueAt(i, 0).toString();
                String rightStr = employeeModel.getValueAt(i, 2).toString();
                // Update the table with the new data
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, apidStr);
                pstmt.setString(2, loginIDStr);
                pstmt.setInt(3, rightStr.equals("true") ? 1 : 0);
                pstmt.execute();
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateApplicationRights() {
        try {
            String sqlUpdate = "update `applicationright` set `r` = ? where `apid` = ? and `userid` = ? ";
            String sql = "insert into `applicationright` (`id`, `apid`,`userid`, `r`) select null, ?, ?, ? " +
                    "from applicationright where not exists (select `id` from `appicationright` where `userid`= ? and `apid` = ?) limit 1 ";
            for(int i = 0; i < employeeModel.getRowCount(); i++) {
                String apidStr = employeeModel.getValueAt(i, 0).toString();
                int rightStr = employeeModel.getValueAt(i, 2).toString().equals("true") ? 1 : 0;
                
                // Update the table with the data
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, apidStr);
                pstmt.setInt(2, employeeID);
                pstmt.setInt(3, rightStr);
                pstmt.setInt(4, employeeID);
                pstmt.setString(5, apidStr);
                pstmt.execute();
                pstmt.close();
                
                pstmt = conn.prepareStatement(sqlUpdate);
                pstmt.setInt(1, rightStr);
                pstmt.setString(2, apidStr);
                pstmt.setInt(3, employeeID);
                pstmt.execute();
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
