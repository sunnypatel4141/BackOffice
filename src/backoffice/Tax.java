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
 */
class Tax extends StoreInfo implements ActionListener {
        
    private String[] cn = {"ID", "Percentage", "Tax Name"};
    private Object[][] data = null;
    private DefaultTableModel categoryModel = new DefaultTableModel(data, cn);
    private JTable categoryTable = new JTable(categoryModel);
    private JButton close, addBand, editBand, deleteBand;
    
    public JDialog taxDialog = new JDialog(frame, "Tax Editor", true);
    
    public Tax() {
        renderWindow();
    }
    
    /**
     * Render the tax editor window
     */
    private void renderWindow() {
        JPanel btnsPnl = new JPanel();
        JPanel tablePnl = new JPanel();
        
        JScrollPane jsp = new JScrollPane(categoryTable);
        tablePnl.add(jsp);
        
        close = new JButton("Close");
        close.addActionListener(this);
        addBand = new JButton("<html><p style='text-align:center;'>Add <br>Tax Band</p></html>");
        addBand.addActionListener(this);
        editBand = new JButton("<html><p style='text-align:center;'>Edit <br>Tax Band</p></html>");
        editBand.addActionListener(this);
        deleteBand = new JButton("<html><p style='text-align:center;'>Delete <br>Tax Band</p></html>");
        deleteBand.addActionListener(this);
        
        btnsPnl.add(close);
        btnsPnl.add(addBand);
        btnsPnl.add(editBand);
        btnsPnl.add(deleteBand);
        btnsPnl.setLayout(new GridLayout(1, 4));
        btnsPnl.setPreferredSize(new Dimension(500, 70));
        
        loadCateogries();
        
        taxDialog.add(tablePnl);
        taxDialog.add(btnsPnl);
        taxDialog.setLayout(new FlowLayout());
        taxDialog.setSize(650, 650);
        taxDialog.setVisible(true);
    }
    
    /**
     * Loads all the tax categories from the database
     */
    private void loadCateogries() {
        try {
            String sql = "select * from taxcode";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Object[] row = {rs.getString("id"), rs.getString("taxpercentage"),
                rs.getString("taxcodename")};
                categoryModel.addRow(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Tax.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object trigger = e.getSource();
        
        if(trigger == close) {
            taxDialog.dispose();
        } else if (trigger == addBand) {
            // Add Category
            TaxEditor te = new TaxEditor();
        } else if (trigger == editBand) {
            // Edit Category
            int selectedRow = categoryTable.getSelectedRow();
            if(selectedRow == -1 ) {
                JOptionPane.showMessageDialog(taxDialog, "Must select a tax band to edit", 
                    "Tax Band Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String taxCodeID = categoryModel.getValueAt(selectedRow, 0).toString();
                TaxEditor te = new TaxEditor(taxCodeID);
            }
        } else if (trigger == deleteBand) {
            // Delete Category
            int selectedRow = categoryTable.getSelectedRow();
            if(selectedRow == -1 ) {
                JOptionPane.showMessageDialog(taxDialog, "Must select a tax band to edit", 
                    "Tax Band Error", JOptionPane.ERROR_MESSAGE);
            } else {
                int selection = JOptionPane.showConfirmDialog(taxDialog, 
                    "Are you sure you want to delete", "Delete Tax Band", JOptionPane.YES_NO_OPTION);
                // Yes Option Selected
                if(selection == 0) {
                    String taxCodeID = categoryModel.getValueAt(selectedRow, 0).toString();
                    deleteTaxBand(taxCodeID);
                }
            }
        }
    }
    
    /**
     * Tries to delete category
     */
    private void deleteTaxBand(String categoryIDArg) {
        try {
            final String categoryID = categoryIDArg;
            // Determine if there are products attached to this tax band
            String sql = "select count(*) from product where taxid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, categoryID);
            rs = pstmt.executeQuery();
            int productCount = 0;
            while(rs.next()) {
                productCount = rs.getInt(1);
            }
            
            // If products use this band then don't delete it
            if (productCount > 0) {
                JOptionPane.showMessageDialog(taxDialog, "Cannot Delete Tax Band as it has products associated with it",
                    "Tax Band delete error", JOptionPane.ERROR_MESSAGE);
            } else {
                sql = "delete from taxcode where id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, categoryID);
                int returnVal = pstmt.executeUpdate();
                JOptionPane.showMessageDialog(taxDialog, "Tax Band Deleted");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Tax.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
