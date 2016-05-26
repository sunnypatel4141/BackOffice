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

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Sunny Patel
 */
class TaxEditor extends StoreInfo implements ActionListener {

    JDialog taxEditorDialog = new JDialog(frame, "Tax Code Editor", true);
    
    private JButton saveBtn, closeBtn;
    private JTextField idFld, nameFld, percentFld;
    private String taxID = "";
    private String taxName = "";
    private String taxPercentage = "";
    /**
     * For an existing tax code
     */
    TaxEditor(String taxCodeID) {
        taxID = taxCodeID;
        loadTaxCode();
        renderWindow();
    }

    TaxEditor() {
        renderWindow();
    }
    
    private void renderWindow() {
        JPanel btnPnl = new JPanel();
        JPanel fldsPnl = new JPanel();
        
        saveBtn = new JButton("Save");
        saveBtn.addActionListener(this);
        closeBtn = new JButton("Close");
        closeBtn.addActionListener(this);
        
        btnPnl.add(saveBtn);
        btnPnl.add(closeBtn);
        btnPnl.setLayout(new GridLayout(1, 2));
        
        JLabel idLbl = new JLabel("ID");
        idFld = new JTextField(7);
        idFld.setText(taxID);
        idFld.setEditable(false);
        
        JLabel nameLbl = new JLabel("Name");
        nameFld = new JTextField(7);
        nameFld.setText(taxName);
        
        JLabel percentLbl = new JLabel("Percent");
        percentFld = new JTextField(7);
        percentFld.setText(taxPercentage);
        
        fldsPnl.add(idLbl);
        fldsPnl.add(idFld);
        fldsPnl.add(nameLbl);
        fldsPnl.add(nameFld);
        fldsPnl.add(percentLbl);
        fldsPnl.add(percentFld);
        fldsPnl.setLayout(new GridLayout(3, 2));
        
        taxEditorDialog.add(fldsPnl);
        taxEditorDialog.add(btnPnl);
        
        taxEditorDialog.setLayout(new GridLayout(2, 1));
        taxEditorDialog.setSize(300, 200);
        taxEditorDialog.setVisible(true);
        
    }
    
    public void actionPerformed(ActionEvent ae) {
        Object trigger = ae.getSource();
        
        if (trigger == saveBtn) {
            // Must be valid
            if (validate()) {
                // Do the save
                if (taxID.equals("")) {
                    createTaxCode();
                } else {
                    saveTaxCode();
                }
                JOptionPane.showMessageDialog(null, "Saved successfully");
                taxEditorDialog.dispose();
            }
        } else if (trigger == closeBtn) {
            taxEditorDialog.dispose();
        }
    }
    
    private void createTaxCode() {
        try {
            String sql = "insert into taxcode (`taxcodename`, `taxpercentage`) values (?, ?)";            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nameFld.getText());
            pstmt.setString(2, percentFld.getText());
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(TaxEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    private void saveTaxCode() {
        try {
            String sql = "update taxcode set `taxcodename` = ? , `taxpercentage` = ? where `id` = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nameFld.getText());
            pstmt.setString(2, percentFld.getText());
            pstmt.setString(3, idFld.getText());
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(TaxEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean validate() {
        boolean isValid = true;
        try {
            String sql = "select * from taxcode where taxpercentage = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, idFld.getText());
            rs = pstmt.executeQuery();
            boolean found = false;
            while(rs.next()) {
                found = true;
            }
            
            // If a new tax band is being added then do this
            /*if (found && taxID.equals("")) {
                isValid = false;
                JOptionPane.showMessageDialog(null, "Tax Band Percentage already exists", "Tax Band error", JOptionPane.ERROR_MESSAGE);
            }*/
        } catch (SQLException ex) {
            Logger.getLogger(TaxEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isValid;
    }
    
    private void loadTaxCode() {
        try {
            String sql = "select * from taxcode where id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, taxID);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                taxName = rs.getString("taxcodename");
                taxPercentage = rs.getString("taxpercentage");
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaxEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
