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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author sunny
 */
class SubCategoryEditor extends Products implements ActionListener {

    private JDialog subCateogryEditorDialog = new JDialog(frame, 
        "Sub Category Editor", true);
    private JButton save, close;
    private JTextField idFld, nameFld;
    private JComboBox mainCatCombo;
    int CategoryID = 0;
    // I want to get and filter categories
    backoffice.data.MainCategory mc = new backoffice.data.MainCategory();
    
    /**
     * When we want to create a new Category from scratch
     */
    SubCategoryEditor() {
        renderEditor();
    }
    
    SubCategoryEditor(int subCatIDArg) {
        CategoryID = subCatIDArg;
        renderEditor();
    }
    
    /**
     * This is the editor that will render all the correct info
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
        
        JLabel idLbl = new JLabel("Category ID");
        idFld = new JTextField(7);
        JLabel nameLbl = new JLabel("Category Name");
        nameFld = new JTextField(7);
        JLabel mcidLbl = new JLabel("Main Category");
        mainCatCombo = new JComboBox(mc.getCategories());
        
        detailPnl.add(idLbl);
        detailPnl.add(idFld);
        detailPnl.add(nameLbl);
        detailPnl.add(nameFld);
        detailPnl.add(mcidLbl);
        detailPnl.add(mainCatCombo);
        detailPnl.setLayout(new GridLayout(3, 2));
        
        loadCategoryInfo();
        
        subCateogryEditorDialog.add(detailPnl);
        subCateogryEditorDialog.add(btnsPnl);
        subCateogryEditorDialog.setSize(500, 200);
        subCateogryEditorDialog.setLayout(new FlowLayout());
        subCateogryEditorDialog.setVisible(true);
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object trigger = e.getSource();
        if(trigger == close) {
            subCateogryEditorDialog.dispose();
        } else if(trigger == save) {
            if(validate()) {
                if(CategoryID == 0) {
                    createSubCateogry();
                } else {
                    updateSubCategory();
                }
            }
        }
    }
    
    /**
     * If the name is blank or the main category not selected
     * then display error message
     */
    private boolean validate() {
        boolean returnVal = true;
        String subCatName = nameFld.getText();
        String mainCatName = mainCatCombo.getSelectedItem().toString();
        if(subCatName.equals("") || mainCatName.equals("")) {
            JOptionPane.showMessageDialog(subCateogryEditorDialog, 
                "A category and name must both be entered",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            returnVal = false;
        }
        
        return returnVal;
    }
    
    private void loadCategoryInfo() {
        try {
            String sql = "select * from `subcategory` where `id` = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, CategoryID);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                nameFld.setText(rs.getString("name"));
                idFld.setText(rs.getString("id"));
                mainCatCombo.setSelectedIndex(mc.getSelectedCategory(rs.getInt("mcid")));
            }
            idFld.setEditable(false);
            
        } catch (SQLException ex) {
            Logger.getLogger(SubCategoryEditor.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    private void updateSubCategory() {
        try {
            int mcidInt = mc.getMainCategoryID(mainCatCombo.getSelectedItem().toString());
            String sql = "update `subcategory` set `name` = ?, `mcid` = ? where `id` = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nameFld.getText());
            pstmt.setInt(2, mcidInt);
            pstmt.setInt(3, CategoryID);
            pstmt.execute();
            JOptionPane.showMessageDialog(subCateogryEditorDialog, 
                "Updated Subcateory successfully");
            subCateogryEditorDialog.dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(subCateogryEditorDialog, 
                "Error in updating a category",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(SubCategoryEditor.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    private void createSubCateogry() {
        try {
            int mcidInt = mc.getMainCategoryID(mainCatCombo.getSelectedItem().toString());
            String sql = "insert into `subcategory` (`name`, `mcid`) values(?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nameFld.getText());
            pstmt.setInt(2, mcidInt);
            pstmt.execute();
            JOptionPane.showMessageDialog(subCateogryEditorDialog, 
                "Created Subcateory successfully");
            subCateogryEditorDialog.dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(subCateogryEditorDialog, 
                "Error in creating cateogry",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(SubCategoryEditor.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }
    
}
