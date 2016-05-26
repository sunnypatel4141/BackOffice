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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Sunny Patel
 */
class Products extends adminWindow implements KeyListener, FocusListener {
    
    JTextField barcodeFld, productCodeFld;
    private int selectedInputFiled = 0;// barocde = 0, productcode = 1
    private int testPRID = 0;
    
    public int EDIT_VIA_PRODUCT_ID = 0;
    public int EDIT_VIA_BARCODE = 1;

    public JPanel getMainScreenButtons() {
        JPanel mainScreenPnl = new JPanel();
        
        JLabel barcodeLabel = new JLabel("Product Barcode");
        barcodeFld = new JTextField(15);
        barcodeFld.setFont(h1);
        JLabel productCodeLabel = new JLabel("Product Code");
        productCodeFld = new JTextField(15);
        productCodeFld.setFont(h1);
        
        barcodeFld.addKeyListener(this);
        barcodeFld.addFocusListener(this);
        productCodeFld.addKeyListener(this);
        productCodeFld.addFocusListener(this);
        
        JButton productsBtn = new JButton("Add Product");
        productsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditProduct ep = new EditProduct();
            }
        });
        
        JButton mainCategory = new JButton("<html><div style='text-align:center;'>Manage<br>Main Category</div></html>");
        mainCategory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainCategory mc = new MainCategory();
            }
        });
        
        JButton subCategory = new JButton("<html><div style='text-align:center;'>Manage<br>Sub Category</div></html>");
        subCategory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SubCategory sc = new SubCategory();
            }
        });
        
        JButton searchProduct = new JButton("<html><div style='text-align:center;'>Search<br>Product</div></html>");
        searchProduct.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SearchProduct sp = new SearchProduct();
            }
        });
        
        JButton familyProductBtn = new JButton("<html><div style='text-align:center;'>Family<br>Product</div></html>");
        familyProductBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FamilyEditor fe = new FamilyEditor();
            }
        });
        
        JPanel buttonsPnl = new JPanel();
        JPanel fieldsPnl = new JPanel();
        
        fieldsPnl.add(barcodeLabel);
        fieldsPnl.add(barcodeFld);
        fieldsPnl.add(productCodeLabel);
        fieldsPnl.add(productCodeFld);
        fieldsPnl.setLayout(new GridLayout(2, 2));
        
        buttonsPnl.add(productsBtn);
        buttonsPnl.add(mainCategory);
        buttonsPnl.add(subCategory);
        buttonsPnl.add(searchProduct);
        buttonsPnl.add(familyProductBtn);
        buttonsPnl.setLayout(new GridLayout(1, 5));
        mainScreenPnl.add(fieldsPnl);
        mainScreenPnl.add(buttonsPnl);
        mainScreenPnl.setLayout(new BoxLayout(mainScreenPnl, BoxLayout.PAGE_AXIS));
        
        return mainScreenPnl;
    }
    
    private boolean getProductByBarcode(String barcodeArg) {
        boolean found = false;
        
        try {
            String sql = "select * from `barcode` where `barcode`= ? ";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, barcodeArg);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                found = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Products.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return found;
    }
    
    /**
     * So when a product is scanned we want to check if it 
     * exists if so then open it in editproduct else 
     * display a message showing the issues
     */
    private void findScannedProduct(String productBarcode) {
        // Edit Product
        boolean found = getProductByBarcode(productBarcode);
        
        // If product not found then convert barcode
        if (found == false) {
            UPCConverter upc = new UPCConverter();
            String UPCABarcode = upc.convertUPCEtoA(productBarcode);
            found = getProductByBarcode(UPCABarcode);
            
            // if upca works then change current barcode
            if(found) {
                productBarcode = UPCABarcode;
            }
        }
        
        if (found) {
            EditProduct ep = new EditProduct(productBarcode, EDIT_VIA_BARCODE);
        } else {
            Object message = "Would you like to add this product";
            int n = JOptionPane.showConfirmDialog(frame, message,
                    "Product Not Found", JOptionPane.YES_NO_OPTION);

            if (n == 0) {
                EditProduct ep = new EditProduct(productBarcode, 1);
            }
        }
    }
    
    private void findScannedCode(String productCode) {
        // Edit Product
        try {
            String sql = "select * from `product` where `id`= ? ";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, productCode);
            rs = pstmt.executeQuery();
            boolean found = false;
            while(rs.next()) {
                found = true;
                testPRID = rs.getInt("id");
            }
            if (found) {
                EditProduct ep = new EditProduct(productCode, EDIT_VIA_PRODUCT_ID);
            } else {
                Object message = "Would you like to add this product";
                int n = JOptionPane.showConfirmDialog(frame, message,
                        "Product Not Found", JOptionPane.YES_NO_OPTION);
                
                if (n == 0) {
                    EditProduct ep = new EditProduct(productCode, 1);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        if( e.getKeyCode() == 10 ) {
            // We have scanned it and saved it
            String barcode = barcodeFld.getText();
            String productCode = productCodeFld.getText();
            if(selectedInputFiled == 1 && !productCode.equals("")) { // Product Code
                
                productCodeFld.setText("");
                findScannedCode(productCode);
                
            } else if (selectedInputFiled == 0 && !barcode.equals("")) { // barcode
                
                barcodeFld.setText("");
                findScannedProduct(barcode);
                
            }
            
            // Clear it to stop further triggers
            
            
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void focusGained(FocusEvent fe) {
        Object trigger = fe.getSource();
        if (trigger == productCodeFld) {
            selectedInputFiled = 1;
        } else if (trigger == barcodeFld) {
            selectedInputFiled = 0;
        }
    }

    @Override
    public void focusLost(FocusEvent fe) {
    }
}
