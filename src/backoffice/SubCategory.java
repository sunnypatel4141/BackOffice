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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.util.Vector;
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
class SubCategory extends Products implements ActionListener, KeyListener {
    
    private String[] cn = {"ID", "Name", "MCID"};
    private Object[][] data = null;
    private DefaultTableModel categoryModel = new DefaultTableModel(data, cn);
    private JTable categoryTable = new JTable(categoryModel);
    private JButton close, addCat, editCat, deleteCat;
    private JTextField categoryName;
    
    public JDialog subCategoryDialog = new JDialog(frame, "Main Category", true);
    
    SubCategory() {
        renderWindow();
    }
    
    /**
     * Render the sub cat window
     */
    private void renderWindow() {
        JPanel btnsPnl = new JPanel();
        JPanel tablePnl = new JPanel();
        JPanel inputPnl = new JPanel();
        
        close = new JButton("Close");
        close.addActionListener(this);
        addCat = new JButton("<html><div style='text-align:center;'>Add<br>Category</div></html>");
        addCat.addActionListener(this);
        editCat = new JButton("<html><div style='text-align:center;'>Edit<br>Category</div></html>");
        editCat.addActionListener(this);
        deleteCat = new JButton("<html><div style='text-align:center;'>Delete<br>Category</div></html>");
        deleteCat.addActionListener(this);
        
        //btnsPnl.add(save);
        btnsPnl.add(close);
        btnsPnl.add(addCat);
        btnsPnl.add(editCat);
        btnsPnl.add(deleteCat);
        btnsPnl.setLayout(new GridLayout(1, 4));
        btnsPnl.setPreferredSize(new Dimension(500, 70));
        
        JScrollPane jspMainCat = new JScrollPane(categoryTable);
        jspMainCat.setPreferredSize(new Dimension(400, 500));
        
        tablePnl.add(jspMainCat);
        
        JLabel catNameLbl = new JLabel("Category Name");
        categoryName = new JTextField(11);
        categoryName.addKeyListener(this);
        categoryName.setFont(h1);
        
        inputPnl.add(catNameLbl);
        inputPnl.add(categoryName);
        
        loadAllCategories();
        
        subCategoryDialog.add(btnsPnl);
        subCategoryDialog.add(inputPnl);
        subCategoryDialog.add(tablePnl);
        subCategoryDialog.setLayout(new FlowLayout());
        subCategoryDialog.setSize(700, 700);
        subCategoryDialog.setVisible(true);     
    }
    
    private void loadAllCategories() {
        while(categoryModel.getRowCount() > 0) {
            categoryModel.removeRow(0);
        }
        
        backoffice.data.SubCategory sc = new backoffice.data.SubCategory();
        
        for(Object row : sc.CategoryInfo) {
            categoryModel.addRow((Object[]) row) ;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object trigger = e.getSource();
        if(trigger == close) {
            subCategoryDialog.dispose();
        } else if(trigger == addCat) {
            SubCategoryEditor sce = new SubCategoryEditor();
            loadAllCategories();
        } else if (trigger == editCat) {
            String categoryIDStr = categoryModel.getValueAt(categoryTable.getSelectedRow(), 0).toString();
            int categoryIDInt = Integer.parseInt(categoryIDStr);
            SubCategoryEditor sce = new SubCategoryEditor(categoryIDInt);
            loadAllCategories();
        } else if (trigger == deleteCat) {
            deleteCategory(categoryTable.getSelectedRow());
            loadAllCategories();
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        String currentNameStr;
        String newNameStr;
        if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            int keyLength = categoryName.getText().length();
            if( keyLength > 0) {
                currentNameStr = categoryName.getText().toLowerCase();
                newNameStr = currentNameStr.substring(0, keyLength);
            } else {
                newNameStr = "";
                loadAllCategories();
            }
        } else {
            currentNameStr = categoryName.getText().toLowerCase();
            char keyPressedChar = e.getKeyChar();
            newNameStr = new StringBuffer(currentNameStr).toString();
        }
        
        Vector newDataVector = new Vector();
        for(Object row : categoryModel.getDataVector()) {
            Vector rowVector = (Vector) row;
            String rowStr = rowVector.get(1).toString().toLowerCase();
            
            // Check if the Row Exists
            if(rowStr.contains(newNameStr)) {
                newDataVector.add(row);
            }
        }
        
        categoryName.setText(newNameStr);
        Vector columnidentifiers = new Vector();
        columnidentifiers.add("ID");
        columnidentifiers.add("Name");
        categoryModel.setDataVector(newDataVector, columnidentifiers);
    }

    private void deleteCategory(int selectedRow) {
        try {
            int productCount = 0;
            String scidStr = categoryTable.getValueAt(selectedRow, 0).toString();
            String sql = "select count(*) from `product` where `scid` = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, scidStr);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                productCount = rs.getInt(1);
            }
            if(productCount > 0) {
                JOptionPane.showMessageDialog(subCategoryDialog, 
                    "Cannot delete SubCategory it has product allocated to it", 
                    "Delete Error", JOptionPane.ERROR_MESSAGE);
            } else {
                sql = "delete from `subcategory` where `id` = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, scidStr);
                pstmt.execute();
                JOptionPane.showMessageDialog(subCategoryDialog, 
                    "Sub category Deleted Successfully");
            }
        } catch (SQLException ex) {
            Logger.getLogger(SubCategory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
