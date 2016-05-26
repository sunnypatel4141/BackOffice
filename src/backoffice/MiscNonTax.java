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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Sunny Patel
 * 
 * This class will handle all the misc non tax buttons
 * When a new button is created it will, automatically create
 * a product, to associate with.
 */
class MiscNonTax extends StoreInfo implements ActionListener {
    
    JDialog miscDialog = new JDialog(frame, "Misc Non Tax Items", true);
    JButton deleteItem, addItem, modifyItem, closeDialog;
    
    String cn[] = {"Product ID", "Button ID", "Button Name"};
    Object data[][] = null;
    private DefaultTableModel modelnontax = new DefaultTableModel(data, cn);
    JTable table = new JTable(modelnontax);
    
    MiscNonTax() {
        renderWindow();
    }
    
    private void renderWindow() {
        JPanel tablePnl = new JPanel();
        JPanel btnsPnl = new JPanel();
        // render Window
        JScrollPane jsp = new JScrollPane(table);
        tablePnl.add(jsp);
        table.setFont(h2);
        
        deleteItem = new JButton("Delete");
        deleteItem.addActionListener(this);
        addItem = new JButton("Add");
        addItem.addActionListener(this);
        modifyItem = new JButton("Modify");
        modifyItem.addActionListener(this);
        closeDialog = new JButton("Close");   
        closeDialog.addActionListener(this);
        
        btnsPnl.add(deleteItem);
        btnsPnl.add(addItem);
        btnsPnl.add(modifyItem);
        btnsPnl.add(closeDialog);
        btnsPnl.setLayout(new GridLayout(1, 4));
        btnsPnl.setPreferredSize(new Dimension(450, 70));
        
        miscDialog.add(tablePnl);
        miscDialog.add(btnsPnl);
        miscDialog.setLayout(new FlowLayout());
        
        loadData();
        
        miscDialog.setSize(500, 550);
        miscDialog.setVisible(true);
    }
    
    /**
     * Load Data that is in the table
     */
    private void loadData() {
        try {
            //
            String sql = "select * from nontax";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                Object[] row = {rs.getString("id"), rs.getString("prid"), rs.getString("name")};
                modelnontax.addRow(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MiscNonTax.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object trigger = ae.getSource();
        int selectedRow = table.getSelectedRow();
        
        if(trigger == deleteItem) {
            // Delete Item
            if(selectedRow == -1) {
                JOptionPane.showMessageDialog(miscDialog, "Select Row to delete", "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(miscDialog, "Are you sure you want to delete this",
            "Delete Button", JOptionPane.YES_NO_OPTION);
            // Delte it please
            if( confirm == 0) {
                deleteButton(selectedRow);
            }
            
        } else if (trigger == addItem) {
            
            MiscNonTaxEditor mnte = new MiscNonTaxEditor();
            reloadTable();
        } else if (trigger == modifyItem) {
            if(selectedRow == -1) {
                JOptionPane.showMessageDialog(miscDialog, "Select Row to modify", "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            String buttonID = modelnontax.getValueAt(selectedRow, 0).toString();
            MiscNonTaxEditor mnte = new MiscNonTaxEditor(buttonID);
            reloadTable();
        } else if (trigger == closeDialog) {
            miscDialog.dispose();
        }
    }
    
    /**
     * Delete the given buttons from the nontax table
     * also the prid of the given row is set to discontinued
     */
    private void deleteButton(int selectedRow) {
        try {
            String prid = modelnontax.getValueAt(selectedRow, 0).toString();
            String id = modelnontax.getValueAt(selectedRow, 0).toString();
            // Get the details of the current row
            String sql = "delete from nontax where id='" + id + "'";
            int status = stmt.executeUpdate(sql);
            
            // set the product to be discontinued
            sql = "update product set discontinue='1' where id='" + prid + "'";
            status = stmt.executeUpdate(sql);
            
            JOptionPane.showMessageDialog(miscDialog, "Button button successfully");
            reloadTable();
        } catch (SQLException ex) {
            Logger.getLogger(MiscNonTax.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * After adding, modifying or deleting
     * this table will reload it's self again
     */
    private void reloadTable() {
        int row = modelnontax.getRowCount();
        while(row > 0) {
            row--;
            modelnontax.removeRow(0);
        }
        loadData();
    }
    
}
