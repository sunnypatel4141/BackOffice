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
class MainCategory extends Products implements ActionListener, KeyListener {
    
    private String[] cn = {"ID", "Name"};
    private Object[][] data = null;
    private DefaultTableModel categoryModel = new DefaultTableModel(data, cn);
    private JTable categoryTable = new JTable(categoryModel);
    private JButton close, addCat, editCat, deleteCat;
    private Vector categoryVector = new Vector();
    private JTextField categoryName;
    
    public JDialog mainCategoryDialog = new JDialog(frame, "Main Category", true);
    
    MainCategory() {
        renderWindow();
    }

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
        
        mainCategoryDialog.add(btnsPnl);
        mainCategoryDialog.add(inputPnl);
        mainCategoryDialog.add(tablePnl);
        mainCategoryDialog.setLayout(new FlowLayout());
        mainCategoryDialog.setSize(700, 700);
        mainCategoryDialog.setVisible(true);     
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object trigger = e.getSource();
        if (trigger == close) {
            mainCategoryDialog.dispose();
        } else if(trigger == addCat) {
            createCategory();
            loadAllCategories();
        } else if (trigger == editCat) {
            editCat(categoryTable.getSelectedRow());
            loadAllCategories();
        } else if (trigger == deleteCat) {
            deleteCat(categoryTable.getSelectedRow());
            loadAllCategories();
        }
    }
    
    /**
     * Create a New Cateory
     */
    private void createCategory() {
        //
        MainCateogryEditor mce = new MainCateogryEditor();
    }
    
    /**
     * Edit the category
     */
    private void editCat(int selectedCat) {
        // Edit Category
        try {
            String catIDStr = categoryModel.getValueAt(selectedCat, 0).toString();
            int catIDInt = Integer.parseInt(catIDStr);
            MainCateogryEditor mce = new MainCateogryEditor(catIDInt);
        } catch(Exception ex) {
            Logger.getLogger(MainCateogryEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Delete the category
     */
    private void deleteCat(int selectedRow) {
        // Delete the category
        Object message = "Would you like to delete this category?";
        int n = JOptionPane.showConfirmDialog(mainCategoryDialog, message,
            "Delete Category", JOptionPane.YES_NO_OPTION);
        if (n == 0) {
            try {
                // Need to work out category ID
                String catIDStr = categoryModel.getValueAt(selectedRow, 0).toString();
                int catID = Integer.parseInt(catIDStr);
                boolean productsexist = false;
                String sql = "select count(*) from `product` where " +
                        "`mcid` = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, catID);
                rs = pstmt.executeQuery();
                int productCount = 0;
                while (rs.next()) {
                    productsexist = true;
                    productCount = rs.getInt(1);
                }
                
                if (productsexist && productCount > 0) {
                    JOptionPane.showMessageDialog(mainCategoryDialog, 
                        "Cannot delete this category as it has products allocated to it :" + 
                        productCount, "Delete Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    sql = "delete from `maincategory` where `id` = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, catID);
                    int success = pstmt.executeUpdate();
                    String messageStr = "";
                    if(success == 1) {
                        messageStr = "Deleted Category Successfully";
                    } else {
                        messageStr = "Unable to delete category " + catID;
                    }
                    
                    JOptionPane.showMessageDialog(mainCategoryDialog, messageStr);
                }
            } catch(Exception a) {
                a.printStackTrace();
            }
        }
    }

    private void loadAllCategories() {
        while(categoryModel.getRowCount() > 0) {
            categoryModel.removeRow(0);
        }
        
        backoffice.data.MainCategory mc = new backoffice.data.MainCategory();
        
        for(Object row : mc.CategoryInfo) {
            categoryModel.addRow((Object[]) row);
        }
    }
    
    /**
     * The Objective here is filter easily
     */
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
    
}
