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
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Sunny Patel
 */
class MainCateogryEditor extends Products implements ActionListener {
    private JDialog mainCategoryEditorDialog = new JDialog(frame, 
            "Main Category Editor", true);
    private JButton save, close;
    private JTextField idFld, nameFld;
    private JCheckBox ageRestrictBox;
    private int CategoryID = 0;
    
    /**
     * This is when we don't have a category and it's band new
     */
    MainCateogryEditor() {
        renderEditor();
    }
    
    
    /**
     * THis is for when we have a category
     */
    MainCateogryEditor(int categoryIDArg) {
        CategoryID = categoryIDArg;
        renderEditor();
    }
    
    /**
     * Render the category Editor
     */
    private void renderEditor() {
        JPanel btnsPnl = new JPanel();
        JPanel detailPnl = new JPanel();
        
        save = new JButton("Save");
        save.addActionListener(this);
        close = new JButton("Close");
        close.addActionListener(this);
        
        btnsPnl.add(save);
        btnsPnl.add(close);
        btnsPnl.setLayout(new GridLayout(1, 2));
        btnsPnl.setPreferredSize(new Dimension(200, 60));
        
        JLabel nameLbl = new JLabel("Category Name");
        JLabel idLbl = new JLabel("ID");
        idFld = new JTextField(7);
        idFld.setFont(h1);
        nameFld = new JTextField(7);
        nameFld.setFont(h1);
        
        ageRestrictBox = new JCheckBox("Age Restricted Products?");
        
        detailPnl.add(idLbl);
        detailPnl.add(idFld);
        detailPnl.add(nameLbl);
        detailPnl.add(nameFld);
        detailPnl.add(ageRestrictBox);
        detailPnl.setLayout(new GridLayout(3, 2));
        
        loadCategoryInfo();
        
        mainCategoryEditorDialog.add(detailPnl);
        mainCategoryEditorDialog.add(btnsPnl);
        mainCategoryEditorDialog.setSize(400, 300);
        mainCategoryEditorDialog.setLayout(new FlowLayout());
        mainCategoryEditorDialog.setVisible(true);
        
    }
    
    private void loadCategoryInfo() {
        try {
            String sql = "select * from `maincategory` where `id` = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, CategoryID);
            rs = pstmt.executeQuery();
            
            int ageRestrictInt = 0;
            while(rs.next()) {
                nameFld.setText(rs.getString("name"));
                idFld.setText(rs.getString("id"));
                ageRestrictInt = rs.getInt("agerestrict");
            }
            idFld.setEditable(false);
            
            if (ageRestrictInt == 1) {
                ageRestrictBox.setSelected(true);
            }
            
        } catch(Exception a) {
            Logger.getLogger(MainCateogryEditor.class.getName()).log(Level.SEVERE, null, a);
        }
    }
    
    private void createCategory() {
        try {
            String sql = "insert into `maincategory` (name, agerestrict) values ( ?, ? )";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nameFld.getText());
            pstmt.setInt(2, ageRestrictBox.isSelected() ? 1 : 0);
            pstmt.execute();
            JOptionPane.showMessageDialog(mainCategoryEditorDialog, "Categetory Created successfully");
            mainCategoryEditorDialog.dispose();
        } catch (SQLException ex) {
            Logger.getLogger(MainCateogryEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void updateCategory() {
        try {
            String sql = "update `maincategory` set `name` = ?, `agerestrict` = ? where `id` = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nameFld.getText());
            pstmt.setInt(2, ageRestrictBox.isSelected() ? 1 : 0);
            pstmt.setInt(3, CategoryID);
            pstmt.execute();
            JOptionPane.showMessageDialog(mainCategoryEditorDialog, "Categetory Updated successfully");
            mainCategoryEditorDialog.dispose();
        } catch (SQLException ex) {
            Logger.getLogger(MainCateogryEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean validate() {
        String inputValue = nameFld.getText();
        boolean returnVal = true;
        if(inputValue.equals("") || inputValue.equals("")) {
            JOptionPane.showMessageDialog(mainCategoryEditorDialog, 
                    "Name Cannot be Empty", "Input Error", JOptionPane.ERROR_MESSAGE);
            returnVal = true;

        }
        return returnVal;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object trigger = e.getSource();
        if(trigger == save) {
            if(validate()) {
                if(CategoryID == 0) {
                    createCategory();
                } else {
                    updateCategory();
                }
            }
        } else if (trigger == close) {
            mainCategoryEditorDialog.dispose();
        }
    }
}
