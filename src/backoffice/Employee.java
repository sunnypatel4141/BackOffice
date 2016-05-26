/*
 * Copyright (C) 2015 sunny
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
import java.awt.event.KeyListener;
import java.sql.SQLException;
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
class Employee extends People implements ActionListener, KeyListener {
    
    private String[] cn = {"ID", "Name", "Can Login"};
    private Object[][] data = null;
    private DefaultTableModel employeeModel = new DefaultTableModel(data, cn);
    private JTable employeeTable = new JTable(employeeModel);
    JButton addEmployee, editEmployee, deleteEmployee, close;
    
    private JDialog employeeDialog = new JDialog(frame, "Employee", true);
    
    
    Employee() {
        renderWindow();
    }
    
    /**
     * Render the employee window all the GUI 
     * components that and load any data as well
     */
    private void renderWindow() {
        JPanel btnsPnl = new JPanel();
        JPanel tablePnl = new JPanel();
        
        close = new JButton("Close");
        close.addActionListener(this);
        addEmployee = new JButton("Add Employee");
        addEmployee.addActionListener(this);
        editEmployee = new JButton("Edit Employee");
        editEmployee.addActionListener(this);
        deleteEmployee = new JButton("Delete Employee");
        deleteEmployee.addActionListener(this);
        
        btnsPnl.add(close);
        btnsPnl.add(addEmployee);
        btnsPnl.add(editEmployee);
        btnsPnl.add(deleteEmployee);
        btnsPnl.setLayout(new GridLayout(1, 4));
        btnsPnl.setPreferredSize(new Dimension(500, 70));
        
        JScrollPane jsp = new JScrollPane(employeeTable);
        jsp.setPreferredSize(new Dimension(400, 500));
        
        tablePnl.add(jsp);
        
        loadAllEmployees();
        
        employeeDialog.add(btnsPnl);
        employeeDialog.add(tablePnl);
        employeeDialog.setLayout(new FlowLayout());
        employeeDialog.setSize(700, 700);
        employeeDialog.setVisible(true);
    }
    
    /**
     * Marks employee given id as delted
     * cannot delete employees as stuff is
     * created by them else there will be nulls
     */
    public void markEmployeeAsDeleted(int employeeID) {
        try {
            String sql = "update `users` set `canlogin`='0' where `id` = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeID);
            pstmt.execute();
            JOptionPane.showMessageDialog(employeeDialog, "Deleted OK");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(employeeDialog, "Error in marking employee deleted", 
                "Employee Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Employee.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * load all the employees in the table
     */
    private void loadAllEmployees() {
        try {
            String sql = "select * from users";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                String canLoginStr = "true";
                if(rs.getString("canlogin").equals("0")) {
                    canLoginStr = "false";
                }
                String fullName = rs.getString("firstname") + " " + rs.getString("lastname");
                Object[] row = {rs.getString("id"), fullName, canLoginStr};
                employeeModel.addRow(row);
            }
            
        } catch(Exception a) {
            a.printStackTrace();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        Object trigger = ae.getSource();
        if(trigger == close) {
            employeeDialog.dispose();
        } else if(trigger == addEmployee) {
            // addEmployee
            EmployeeEditor ee = new EmployeeEditor();
        } else if(trigger == editEmployee) {
            // editEmployee
            int selectedRow = employeeTable.getSelectedRow();
            if(selectedRow == -1) {
                JOptionPane.showMessageDialog(employeeDialog, "Must selected employee to edit",
                        "Employee Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String employeeIDStr = employeeModel.getValueAt(selectedRow, 0).toString();
                int employeeIDInt = Integer.parseInt(employeeIDStr);
                EmployeeEditor ee = new EmployeeEditor(employeeIDInt);
            }
        } else if(trigger == deleteEmployee) {
            // deleteEmployee
            int selectedRow = employeeTable.getSelectedRow();
            if(selectedRow == -1) {
                JOptionPane.showMessageDialog(employeeDialog, "Must selected employee to delete",
                        "Employee Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String employeeIDStr = employeeModel.getValueAt(selectedRow, 0).toString();
                int employeeIDInt = Integer.parseInt(employeeIDStr);
                markEmployeeAsDeleted(employeeIDInt);
            }
        }
    }
}
