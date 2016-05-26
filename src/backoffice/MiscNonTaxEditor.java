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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Sunny Patel
 */
class MiscNonTaxEditor extends StoreInfo {

    JDialog miscNonTaxDialog = new JDialog(frame, "Misc Non Tax Editor", true);
    private String buttonID = "0";
    private JTextField buttonNameFld, pridFld, buttonIDFld;
    private JButton closeBtn, saveBtn;
    
    public MiscNonTaxEditor(String buttonIDArg) {
        buttonID = buttonIDArg;
        renderWindow();
    }
    
    public MiscNonTaxEditor() {
        renderWindow();
    }

    private void renderWindow() {
        JPanel fields = new JPanel();
        
        closeBtn = new JButton("Close");
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                miscNonTaxDialog.dispose();
            }
        });
        saveBtn = new JButton("Save");
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (validate()) {
                    if (pridFld.getText().equals("")) {
                        createNewButton();
                    } else {
                        updateNonTaxButton();
                    }
                    miscNonTaxDialog.dispose();
                }
            }
        });
        
        JLabel buttonNameLbl = new JLabel("Button Name");
        buttonNameFld = new JTextField(7);
        JLabel pridLbl = new JLabel("Product ID");
        pridFld = new JTextField(7);
        JLabel buttonIDLbl = new JLabel("Button ID");
        buttonIDFld = new JTextField(7);
        
        fields.add(buttonNameLbl);
        fields.add(buttonNameFld);
        fields.add(pridLbl);
        fields.add(pridFld);
        fields.add(buttonIDLbl);
        fields.add(buttonIDFld);
        
        JPanel btnsPnl = new JPanel();
        
        btnsPnl.add(closeBtn);
        btnsPnl.add(saveBtn);
        btnsPnl.setLayout(new GridLayout(1, 2, 2, 2));
        btnsPnl.setPreferredSize(new Dimension(150, 70));
        
        fields.setLayout(new GridLayout(3, 2, 2, 2));
        
        loadInfo();
        
        buttonIDFld.setEnabled(false);
        miscNonTaxDialog.add(fields);
        miscNonTaxDialog.add(btnsPnl);
        
        miscNonTaxDialog.setLayout(new FlowLayout());
        miscNonTaxDialog.setSize(250, 250);
        miscNonTaxDialog.setLocation(250, 250);
        miscNonTaxDialog.setVisible(true);
    }
    
    private void loadInfo() {
        try {
            String sql = "select * from nontax where id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, buttonID);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                buttonNameFld.setText(rs.getString("name"));
                pridFld.setText(rs.getString("prid"));
                buttonIDFld.setText(rs.getString("id"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MiscNonTaxEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean validate() {
        if (buttonNameFld.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Must enter button name", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (pridFld.getText().equals("") && !buttonIDFld.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Product ID Cannot be blank", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void createNewButton() {
        try {
            String sql = "insert into product (name, taxid ) " +
                "select ?, tx.id from taxcode tx where taxpercentage='0.000' ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, buttonNameFld.getText());
            pstmt.executeUpdate();
            
            sql = "insert into nontax(name, prid) select ?, pr.id from product pr "
                    + "left join taxcode tx on tx.id=pr.taxid "
                    + "where tx.taxpercentage='0.000' and pr.taxid=tx.id and pr.name = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, buttonNameFld.getText());
            pstmt.setString(2, buttonNameFld.getText());
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Added successfully");
        } catch (SQLException ex) {
            Logger.getLogger(MiscNonTaxEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private void updateNonTaxButton() {
        try {
            String sql = "update nontax set name = ? , prid = ? where id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, buttonNameFld.getText());
            pstmt.setString(2, pridFld.getText());
            pstmt.setString(3, buttonIDFld.getText());
            pstmt.executeUpdate();
            
            sql = "update product set name = ? where id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, buttonNameFld.getText());
            pstmt.setString(2, pridFld.getText());
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Update successfully");
        } catch (SQLException ex) {
            Logger.getLogger(MiscNonTaxEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
