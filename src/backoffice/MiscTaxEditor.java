/*
 * Copyright (C) 2015 User
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

import static backoffice.base.DBConnection.conn;
import static backoffice.base.DBConnection.pstmt;
import static backoffice.base.DBConnection.rs;
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
 * @author User
 */
class MiscTaxEditor extends StoreInfo {

    JDialog miscTaxDialog = new JDialog(frame, "Misc Tax Editor", true);
    private String buttonID = "0";
    private JTextField buttonNameFld, pridFld, buttonIDFld;
    private JButton closeBtn, saveBtn;
    private JComboBox taxRate;
    backoffice.data.Tax tax;
    
    private int taxID = 0;
    
    public MiscTaxEditor(String buttonIDArg) {
        buttonID = buttonIDArg;
        renderWindow();
    }
    
    public MiscTaxEditor() {
        renderWindow();
    }

    private void renderWindow() {
        JPanel fields = new JPanel();
        
        tax = new backoffice.data.Tax();
        closeBtn = new JButton("Close");
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                miscTaxDialog.dispose();
            }
        });
        saveBtn = new JButton("Save");
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (validate()) {
                    // Set the tax id 
                    taxID = tax.getTaxID(taxRate.getSelectedIndex());
                    if (buttonID.equals("") || buttonID.equals("0")) {
                        createNewButton();
                    } else {
                        updateTaxButton();
                    }
                    miscTaxDialog.dispose();
                }
            }
        });
        
        JLabel buttonNameLbl = new JLabel("Button Name");
        buttonNameFld = new JTextField(7);
        JLabel pridLbl = new JLabel("Product ID");
        pridFld = new JTextField(7);
        JLabel buttonIDLbl = new JLabel("Button ID");
        buttonIDFld = new JTextField(7);
        JLabel taxRateLbl = new JLabel("Tax Rate");
        taxRate = new JComboBox(tax.getCategories());
        
        fields.add(buttonNameLbl);
        fields.add(buttonNameFld);
        fields.add(pridLbl);
        fields.add(pridFld);
        fields.add(buttonIDLbl);
        fields.add(buttonIDFld);
        fields.add(taxRateLbl);
        fields.add(taxRate);
        
        JPanel btnsPnl = new JPanel();
        
        btnsPnl.add(closeBtn);
        btnsPnl.add(saveBtn);
        btnsPnl.setLayout(new GridLayout(1, 2, 2, 2));
        btnsPnl.setPreferredSize(new Dimension(250, 70));
        
        fields.setLayout(new GridLayout(4, 2, 2, 2));
        
        loadInfo();
        
        buttonIDFld.setEnabled(false);
        
        miscTaxDialog.add(fields);
        miscTaxDialog.add(btnsPnl);
        
        miscTaxDialog.setLayout(new FlowLayout());
        miscTaxDialog.setSize(400, 250);
        miscTaxDialog.setLocation(250, 250);
        miscTaxDialog.setVisible(true);
    }
    
    private void loadInfo() {
        try {
            String sql = "select * from tax where id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, buttonID);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                buttonNameFld.setText(rs.getString("name"));
                pridFld.setText(rs.getString("prid"));
                buttonIDFld.setText(rs.getString("id"));
                taxRate.setSelectedIndex(tax.getSelectedCategoryByRate(rs.getString("taxrate")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MiscTaxEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean validate() {
        if (buttonNameFld.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Must enter button name", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (pridFld.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Product ID Cannot be blank default is 0", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void createNewButton() {
        try {
            String sql = "insert into product (name, taxid ) values(?, ?) ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, buttonNameFld.getText());
            pstmt.setInt(2, taxID);
            pstmt.executeUpdate();
            
            sql = "insert into tax(name, prid, taxrate) (select ?, pr.id, ? from product pr "
                    + "where pr.taxid= ? and pr.name = ? limit 1)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, buttonNameFld.getText());
            pstmt.setString(2, tax.getTaxRate(taxID));
            pstmt.setInt(3, taxID);
            pstmt.setString(4, buttonNameFld.getText());
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Added successfully");
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(MiscTaxEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private void updateTaxButton() {
        try {
            String sql = "update tax set name = ? , prid = ?, taxrate = ? where id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, buttonNameFld.getText());
            pstmt.setString(2, pridFld.getText());
            pstmt.setString(3, tax.getTaxRate(taxID));
            pstmt.setString(4, buttonIDFld.getText());
            pstmt.executeUpdate();
            
            sql = "update product set name = ?, taxid = ? where id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, buttonNameFld.getText());
            pstmt.setInt(2, taxID);
            pstmt.setString(3, pridFld.getText());
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Update successfully");
        } catch (SQLException ex) {
            Logger.getLogger(MiscTaxEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}
