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

import backoffice.data.Product;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
class EditProduct extends Products implements ActionListener, ItemListener {
    
    String[] priceCN = {"Qty", "Unit Price"};
    Object[][] priceData = null;
    
    String[] barcodeCN = {"Barcode"};
    Object[][] barcodeData = null;
    
    DefaultTableModel priceM = new DefaultTableModel(priceData, priceCN);
    DefaultTableModel barcodeM = new DefaultTableModel(barcodeData, barcodeCN);
    
    JTable priceT = new JTable(priceM);
    JTable barcodeT = new JTable(barcodeM);
    
    JTextField productNameFld, productIDFld, currentStockFld, reorderLevelFld, notesFld;
    JComboBox mainCatCombo, subCatCombo, taxableCatCombo;
    JCheckBox quickKeyBox, discontinueBox, showNotesBox, notOnSundayBox, CheckIDBox;
    JButton saveBtn, closeBtn, printLabelBtn;
    boolean printLabelFlag = false;
    
    private JDialog editProductDialog = new JDialog(frame, "Edit Product", true);
    
    private Product data = new Product();
    
    /**
     * Edit Product simple
     * we will add a product from scratch
     */
    public EditProduct() {
        renderWindow();
    }
    
    /**
     * Edit product product id
     * where we will take a product id or a barcode
     * @param editArg
     * @param editType
     */
    public EditProduct(String editArg, int editType) {
        // There can be an error in loading the data object here
        if( editType == EDIT_VIA_PRODUCT_ID ) {
            data.setProductID(editArg);
        } else if ( editType == EDIT_VIA_BARCODE ) {
            data.setBarcode(editArg);
        }
        renderWindow();
    }
    
    /**
     * Render the product editor window
     */
    private void renderWindow() {
        
        JPanel pricePnl = new JPanel();
        JPanel barcodePnl = new JPanel();
        JPanel controlPnl = new JPanel();
        JPanel fieldPnl = new JPanel();
        
        JLabel productNameLbl = new JLabel("Name");
        productNameFld = new JTextField(7);
        JLabel productIDLbl = new JLabel("Product ID");
        productIDFld = new JTextField(7);
        JLabel productCurStockLbl = new JLabel("Current Stock");
        currentStockFld = new JTextField();
        JLabel productReOrdLvlLbl = new JLabel("Reorder Limit");
        reorderLevelFld = new JTextField(7);
        JLabel notesLbl = new JLabel("Notes");
        notesFld = new JTextField(7);
        
        JLabel mainCatLbl = new JLabel("Main Category");
        mainCatCombo = new JComboBox(data.getMainCateogries());
        mainCatCombo.addItemListener(this);
        JLabel subCatLbl = new JLabel("Sub Category");
        subCatCombo = new JComboBox(data.getSubCategories());
        JLabel taxCatLbl = new JLabel("Tax");
        taxableCatCombo = new JComboBox(data.getTaxCode());
        
        quickKeyBox = new JCheckBox("Quick Key");
        discontinueBox = new JCheckBox("Discontinue Product?"); 
        showNotesBox = new JCheckBox("Notes");
        notOnSundayBox = new JCheckBox("Not sell on Sunday");
        CheckIDBox = new JCheckBox("Check ID");
        
        // We don't want people touching the product id at any point
        productIDFld.setEnabled(false);
        
        fieldPnl.add(productIDLbl);
        fieldPnl.add(productIDFld);
        fieldPnl.add(productNameLbl);
        fieldPnl.add(productNameFld);
        fieldPnl.add(productCurStockLbl);
        fieldPnl.add(currentStockFld);
        fieldPnl.add(productReOrdLvlLbl);
        fieldPnl.add(reorderLevelFld);
        fieldPnl.add(notesLbl);
        fieldPnl.add(notesFld);
        fieldPnl.add(mainCatLbl);
        fieldPnl.add(mainCatCombo);
        fieldPnl.add(subCatLbl);
        fieldPnl.add(subCatCombo);
        fieldPnl.add(taxCatLbl);
        fieldPnl.add(taxableCatCombo);
        fieldPnl.add(quickKeyBox);
        fieldPnl.add(discontinueBox);
        fieldPnl.add(showNotesBox);
        fieldPnl.add(notOnSundayBox);
        fieldPnl.add(CheckIDBox);
        fieldPnl.setLayout(new GridLayout(13, 2));
        
        saveBtn = new JButton("Save", new ImageIcon("Icons/tick.png"));
        saveBtn.addActionListener(this);
        closeBtn = new JButton("Close", new ImageIcon("Icons/mini_close.png"));
        closeBtn.addActionListener(this);
        printLabelBtn = new JButton("Print", new ImageIcon("Icons/printer.png"));
        printLabelBtn.addActionListener(this);
        
        controlPnl.add(saveBtn);
        controlPnl.add(closeBtn);
        controlPnl.add(printLabelBtn);
        
        JButton addPrice = new JButton("Add Price");
        addPrice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEditPrice(-1);
            }
        });
        JButton editPrice = new JButton("Edit Price");
        editPrice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (getSelectedRowPrice(true) != -1) {
                    addEditPrice(getSelectedRowPrice(true));
                }
            }
        });
        JButton removePrice = new JButton("Remove Price");
        removePrice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removePriceRow();
            }
        });
        JPanel priceControlPnl = new JPanel();
        priceControlPnl.add(addPrice);
        priceControlPnl.add(editPrice);
        priceControlPnl.add(removePrice);
        priceControlPnl.setLayout(new GridLayout(1, 3));
        
        JScrollPane jspP = new JScrollPane(priceT);
        jspP.setPreferredSize(new Dimension(100, 100));
        pricePnl.add(jspP);
        pricePnl.add(priceControlPnl);
        pricePnl.setLayout(new BoxLayout(pricePnl, BoxLayout.PAGE_AXIS));
        
        JButton addBarcode = new JButton("Add Barcode");
        addBarcode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEditBarcode(getSelectedRowBarcode(false));
            }
        });
        JButton removeBarcode = new JButton("Remove Barcode");
        removeBarcode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeBarcodeRow();
            }
        });
        JPanel barcodeControlPnl = new JPanel();
        barcodeControlPnl.add(addBarcode);
        barcodeControlPnl.add(removeBarcode);
        barcodeControlPnl.setLayout(new GridLayout(1, 2));
        
        JScrollPane jspB = new JScrollPane(barcodeT);
        jspB.setPreferredSize(new Dimension(100, 100));
        barcodePnl.add(jspB);
        barcodePnl.add(barcodeControlPnl);
        barcodePnl.setLayout(new BoxLayout(barcodePnl, BoxLayout.PAGE_AXIS));
        
        JPanel dialogMainPnl = new JPanel();
        
        dialogMainPnl.add(fieldPnl);
        dialogMainPnl.add(pricePnl);
        dialogMainPnl.add(barcodePnl);
        dialogMainPnl.add(controlPnl);
        
        dialogMainPnl.setLayout(new BoxLayout(dialogMainPnl, BoxLayout.PAGE_AXIS));
        
        loadData();
        
        editProductDialog.add(dialogMainPnl);
        editProductDialog.setSize(700, 700);
        editProductDialog.setVisible(true);
        
    }
    
    /**
     * Also shows a message if no row is selected
     * @param boolean showErrors
     */
    private int getSelectedRowPrice(boolean showErrors) {
        int selectedRow = priceT.getSelectedRow();
        if ( selectedRow == -1 && showErrors ) {
            JOptionPane.showMessageDialog(editProductDialog, 
                    "Please select price row to alter", "Price error",
                    JOptionPane.ERROR_MESSAGE);
        }
        
        return selectedRow;
    }
    
    /**
     * Also shows a message if no row is selected
     * @params boolean showErrors
     */
    private int getSelectedRowBarcode(boolean showErrors) {
        int selectedRow = barcodeT.getSelectedRow();
        if(selectedRow == -1 && showErrors) {
            JOptionPane.showMessageDialog(editProductDialog, 
                    "Please select barcode row", "Row error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return selectedRow;
    }
    
    /**
     * We want to remove the price row from the table
     * and notify if there is an error
     */
    private void removePriceRow() {
        int selectedRow = getSelectedRowPrice(true);
        if(selectedRow == -1) {
        } else {
            priceM.removeRow(selectedRow);
        }
    }
    
    /**
     * We want to remvoe the barcode row from the table
     * and notify if there is an error
     */
    private void removeBarcodeRow() {
        int selectedRow = getSelectedRowBarcode(true);
        if(selectedRow == -1) {
            return;
        } else {
            barcodeM.removeRow(selectedRow);
        }
    }
    
    /**
     * Validate the input form from the inputs provided
     * We check for:
     * 1. 1 qty and price exist
     * 2. Product name Exists
     * 3. main category must be selected
     * 4. Duplicate price bands
     * 5. Duplicate Barcodes
     */
    private boolean validate() {
        // Does at least one price band exist and is there a price for 1 qty
        if (priceT.getRowCount() == 0) {
            JOptionPane.showMessageDialog(editProductDialog,
                    "Product must have at least one price band",
                    "Price Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            boolean foundOneQty = false;
            Vector prices = priceM.getDataVector();
            for(Object rowObj : prices) {
                Vector row = (Vector) rowObj;
                if(row.get(0).toString().equals("1")){
                    foundOneQty = true;
                }
            }
            
            if(foundOneQty == false) {
                JOptionPane.showMessageDialog(editProductDialog,
                    "Product must have a price for a single qty",
                    "Price Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // If tehr
            boolean duplicatePriceBands = checkDuplicatePriceBands(prices);
            if( duplicatePriceBands == false ) {
                JOptionPane.showMessageDialog(editProductDialog,
                    "Duplicate price bands exist please correct and try again",
                    "Price Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        
        boolean duplicateBarocdes = checkDuplicateBarcodes(barcodeM.getDataVector());
        if(duplicateBarocdes == false) {
            JOptionPane.showMessageDialog(editProductDialog,
                "Duplicate barcodes exist please correct and try again",
                "Barcode Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // product must have a name
        if(productNameFld.getText().equals("")) {
            JOptionPane.showMessageDialog(editProductDialog,
                    "Product must have a name",
                    "Price Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        String mainCategoryName = mainCatCombo.getSelectedItem().toString();
        if(mainCategoryName.equals("")) {
            JOptionPane.showMessageDialog(editProductDialog,
                    "Product Main Category must be selected",
                    "Price Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // By this stage any errors shound have returned
        return true;
    }
    
    /**
     * Some times duplicate barcodes get scanned 
     * so this tells if they exist
     */
    private boolean checkDuplicateBarcodes(Vector barcodes) {
        // Refer to the pricbands thing
        HashMap barcodeMap = new HashMap();
        for(Object barcodeStr : barcodes) {
            barcodeMap.put(barcodeStr, "1");
        }
        
        if (barcodeMap.size() == barcodes.size()) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Some times people will input by accident 2 price bands for the same qty
     * this checks for duplicates and then tells them off
     */
    private boolean checkDuplicatePriceBands(Vector prices) {
        // Build a hash map of the qty's then compare the size of the given thing 
        // and the hashmap if they differ then duplicates exist
        HashMap pricesMap = new HashMap();
        for(Object rowObj : prices) {
            Vector row = (Vector) rowObj;
            pricesMap.put(row.get(0), "1");
        }
        
        if (pricesMap.size() == prices.size()) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Updates the product data with what we have on the form
     * this is typically done for the purpose of wring that
     * data from form out after validating it
     */
    private void updateDataObject() {
        data.setBarcodes(barcodeM.getDataVector());
        data.setPrices(priceM.getDataVector());
        data.setProductName(productNameFld.getText());
        data.setCategory(mainCatCombo.getSelectedItem().toString());
        if(subCatCombo.getItemCount() != 0) {
            data.setSubCategory(subCatCombo.getSelectedItem().toString());
        }
        // Tax has selected index as we combine tax rate and name
        data.setTax(taxableCatCombo.getSelectedIndex());
        data.setCurrentStock(currentStockFld.getText());
        data.setReorderStock(reorderLevelFld.getText());
        data.setQuickKey(quickKeyBox.isSelected());
        data.setDiscontinuedBox(discontinueBox.isSelected());
        data.setNotesBox(showNotesBox.isSelected());
        data.setCheckIDBox(CheckIDBox.isSelected());
        data.setNotOnSundayBox(notOnSundayBox.isSelected());
        data.setNotes(notesFld.getText());
    }
    
    /**
     * Load data in to the GUI from the Product object
     */
    private void loadData() {
        // load all the barcodes in
        if (data.barcode != null) {
            for(int i = 0; i < data.barcode.length; i++) {
                Object row[] = {data.barcode[i]};
                barcodeM.addRow(row);
            }
        }
        if(data.priceQty != null) {
            // load the prices in 
            for(int i = 0; i < data.price.length; i++) {
                Object row[] = {data.priceQty[i], data.price[i]};
                priceM.addRow(row);
            }
        }
        
        // Product name
        productNameFld.setText(data.prName);
        productIDFld.setText(data.productID);
        currentStockFld.setText("" + data.currentStock); 
        reorderLevelFld.setText("" + data.reorderStock);
        mainCatCombo.setSelectedIndex(data.getSelectedMainCategory());
        // Don't bother lading a category if one is not assigned
        if(data.scid != 0) {
            subCatCombo.setSelectedIndex(data.getSelectedSubCategory());
        }
        taxableCatCombo.setSelectedIndex(data.getSelectedTaxCode());
        quickKeyBox.setSelected(data.qk);
        discontinueBox.setSelected(data.discountinued);
        showNotesBox.setSelected(data.showNotes);
        notOnSundayBox.setSelected(data.notOnSunday);
        CheckIDBox.setSelected(data.CheckId);
        notesFld.setText(data.notes);
    }
    
    /**
     * We want to get the data written out when validated
     */
    private void save() {
        
        // We need to tell the data object about our new values
        updateDataObject();
        // All Data Must be validated before being written out
        if(validate() && data.validateData()) {
            try {
                data.writeOutData();
                // If we need to print this product then trigger it
                if(printLabelFlag) {
                    data.addToPrintQueue();
                }
                JOptionPane.showMessageDialog(editProductDialog, "Product saved successfully");
                editProductDialog.dispose();
            } catch(Exception a) {
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Error invalid data\n"
                    + "Please Check the data and try again", "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 
     * This method shows a dialog box that the user can use to alter or add a price
     * WARNING DIALOG BOX CONSTRUCTED HERE AND DISPLAED FROM HERE
     */
    private void addEditPrice(int rowArg) {
        final int row = rowArg;
        
        // build the ui
        final JDialog editPriceDialog = new JDialog(editProductDialog, "Price Edit", true);
        JButton addPriceBtn = new JButton("Add Price");
        JButton closePriceBtn = new JButton("Close");
        closePriceBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                editPriceDialog.dispose();
            }
        });
        JLabel qtyLbl = new JLabel("Qty");
        JLabel priceLbl = new JLabel("Pack Price");
        final JTextField qtyValueFld = new JTextField(7);
        final JTextField priceValueFld = new JTextField(7);
        qtyValueFld.setFont(large);
        priceValueFld.setFont(large);
        
        // Can we set values here if we are in edit mode
        int selectedRow = getSelectedRowPrice(false);
        
        // -1 here is for triggering add price when add is clicked and selected made
        if (row != -1 && selectedRow != -1) {
            qtyValueFld.setText(priceM.getValueAt(selectedRow, 0).toString());
            priceValueFld.setText(priceM.getValueAt(selectedRow, 1).toString());
        }
        
        addPriceBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (row == -1) {
                    if (checkIsValidFloat(priceValueFld.getText()) && 
                            checkIsValidInt(qtyValueFld.getText())) {
                        float singleUnitPrice = getUnitPrice(qtyValueFld.getText(), priceValueFld.getText());
                        Object[] row = {qtyValueFld.getText(), singleUnitPrice};
                        priceM.addRow(row);
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid amounts entered, Please try again", 
                            "Price Invalid", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    if (checkIsValidFloat(priceValueFld.getText()) && 
                            checkIsValidInt(qtyValueFld.getText())) {
                        float singleUnitPrice = getUnitPrice(qtyValueFld.getText(), priceValueFld.getText());
                        priceM.setValueAt(qtyValueFld.getText(), row, 0);
                        priceM.setValueAt(singleUnitPrice, row, 1);
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid amounts entered, Please try again", 
                            "Price Invalid", JOptionPane.ERROR_MESSAGE);
                    }
                }
                editPriceDialog.dispose();
            }
        });
        
        editPriceDialog.add(qtyLbl);
        editPriceDialog.add(qtyValueFld);
        editPriceDialog.add(priceLbl);
        editPriceDialog.add(priceValueFld);
        editPriceDialog.add(addPriceBtn);
        editPriceDialog.add(closePriceBtn);
        editPriceDialog.setLayout(new GridLayout(4, 2));
        editPriceDialog.setSize(300, 200);
        editPriceDialog.setLocation(250, 300);
        editPriceDialog.setVisible(true);
    }
    
    /**
     * Gets the unit price of the price entered in float
     */
    private float getUnitPrice(String qtyArg, String priceStr) {
        int qtyUnitsInt = Integer.parseInt(qtyArg);
        float priceFlt = Float.parseFloat(priceStr);
        
        float unitPrice = priceFlt / qtyUnitsInt;
        
        return unitPrice;
    }
    
    /**
     * Just to check if the the string is a valid float or not 
     */
    private boolean checkIsValidFloat(String inputValue) {
        boolean returnValue = false;
        try {
            float validFloat = Float.parseFloat(inputValue);
            returnValue = true;
        } catch (Exception a) {
            a.printStackTrace();
        }
        return returnValue;
    }
    
    /**
     * Just to check if the the string is a valid int
     */
    private boolean checkIsValidInt(String inputValue) {
        boolean returnValue = false;
        try {
            int validFloat = Integer.parseInt(inputValue);
            returnValue = true;
        } catch (Exception a) {
            a.printStackTrace();
        }
        return returnValue;
    }
    
    /**
     * This class shows a dialog that the user can use to alter or add barcode
     */
    private void addEditBarcode(int rowArg) {
        final int row = rowArg;
        final JDialog editBarcodeDialog = new JDialog(editProductDialog, "Barcode Edit", true);
        JButton addBarcodeBtn = new JButton("Add Barcode");
        JButton closeBarcodeBtn = new JButton("Close");
        closeBarcodeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                editBarcodeDialog.dispose();
            }
        });
        final JLabel barcodeValLbl = new JLabel("Barcode");
        final JTextField barcodeValueFld = new JTextField(7);
        barcodeValueFld.setFont(large);
        // Can we set values here if we are in edit mode
        int selectedRow = getSelectedRowPrice(false);
        if (selectedRow != -1) {
            barcodeValueFld.setText(barcodeM.getValueAt(selectedRow, 0).toString());
        }
        
        addBarcodeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (row == -1) {
                    Object[] row = {barcodeValueFld.getText()};
                    barcodeM.addRow(row);
                } else {
                    barcodeM.setValueAt(barcodeValueFld.getText(), row, 0);
                }
                editBarcodeDialog.dispose();
            }
        });
        
        editBarcodeDialog.add(barcodeValLbl);
        editBarcodeDialog.add(barcodeValueFld);
        editBarcodeDialog.add(addBarcodeBtn);
        editBarcodeDialog.add(closeBarcodeBtn);
        editBarcodeDialog.setLayout(new GridLayout(2, 2));
        editBarcodeDialog.setSize(300, 100);
        editBarcodeDialog.setLocation(200, 500);
        editBarcodeDialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object trigger = e.getSource();
        if(trigger == closeBtn) {
            editProductDialog.dispose();
        } else if( trigger == saveBtn ) {
            save();
        } else if( trigger == printLabelBtn ) {
            // If we are adding a new prouct then
            // Flag it for print queue adding
            if (data.productID.equals("")) {
                printLabelFlag = true;
            } else {
                data.addToPrintQueue();
            }
            JOptionPane.showMessageDialog(frame, "Added to print queue");
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            subCatCombo.removeAllItems();
            String mainCatComboStr = mainCatCombo.getSelectedItem().toString();
            Object[] subCatList = data.getSubCategoryByMCID(mainCatComboStr);
            
            for(Object subCatName : subCatList) {
                subCatCombo.addItem(subCatName);
            }
        }
    }
}
