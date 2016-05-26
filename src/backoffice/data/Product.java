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
package backoffice.data;

import backoffice.base.DBConnection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * STEP 1 CHECK DATA is it correct are there any issues?
 * STEP 2 UPDATE DATA update any existing data
 * STEP 3 DELETE DATA that should not exist anymore
 * SETP 4 INSERT DATA new data that has been added to the product
 */

/**
 *
 * @author Sunny Patel
 */
public class Product extends DBConnection {
    
    // All the attributes we want our object to have
    public String productID = "";
    public String prName = "";
    public String prPrice = ""; // Price for a single qty
    public int currentStock = 0;
    public int reorderStock = 0;
    public int mcid = 0;
    public int scid = 0;
    public int grid = 0;
    public int taxid = 0;
    public boolean qk = false;
    public boolean discountinued = false;
    public boolean showNotes = false;
    public boolean CheckId = false;
    public boolean notOnSunday = false;
    public String notes;
    public String[] barcode = null;
    public String[] price;
    public String[] priceQty;
    Object[] barcodeCategories = new Object[2];
    Object[] priceCategories = new Object[2];
    
    MainCategory mc = new MainCategory();
    SubCategory sc = new SubCategory();
    Tax tax = new Tax();
    
    /**
     * Load the product based on the product id
     */
    public void setProductID(String productIDArg) {
        productID = productIDArg;
        loadProduct(productIDArg);
    }
    
    /**
     * load the product based on the barcode
     */
    public void setBarcode(String barcodeArg) {
        try {
            String sql = "select * from `barcode` where `barcode` = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, barcodeArg);
            rs = pstmt.executeQuery();
            boolean found = false;
            String barcodeprid = "";
            while (rs.next()) {
                found = true;
                barcodeprid = rs.getString("prid");
            }
            
            if ( found ) {
                // Load all the data in this product
                loadProduct(barcodeprid);
            } else {
                // Populate the barcode field
                barcode = new String[1];
                barcode[0] = barcodeArg;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Here we will add the new data about the product
     * then we will update the data on the product
     */
    public void writeOutData() {
        if (productID.equals("")) {
            // We know that this is a new product so add it
            createProduct();
        } else {
            // Existing product please add it in
            updateExistingProduct();
        }
    }

    /**
     * Create product This is where the product will be check 
     * Validate this product via the validate() function
     */
    private void createProduct() {
        Savepoint createProduct = null;
        try {
            // Is check ID Based on category
            if (CheckId == false) {
                CheckId = mc.isCategoryAgeRestricted(mcid);
            }
            
            // Work out what to do with no on sunday and stuff
            String notifyStr = null;
            if(notOnSunday) {
                notifyStr = "nosunday";
            } else if (CheckId) {
                notifyStr = "age";
            } else if (showNotes) {
                notifyStr = "shownotes";
            }
            
            conn.setAutoCommit(false);
            createProduct = conn.setSavepoint();
            // Get the data written out
            String sql = "insert into product " +
                    "(`name`, `mcid`, `scid`, `grid`, `qk`, " +
                    "`discontinue`, `shownotes`, `notes`, `notifytype`, " +
                    "`currentstock`, `reorderlimit`, `taxid`) " +
                    "values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
            
            if(notifyStr == null) {
                sql = "insert into product " +
                    "(`name`, `mcid`, `scid`, `grid`, `qk`, " +
                    "`discontinue`, `shownotes`, `notes`, " +
                    "`currentstock`, `reorderlimit`, `taxid`) " +
                    "values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, prName);
            pstmt.setInt(2, mcid);
            pstmt.setInt(3, scid);
            pstmt.setInt(4, grid);
            pstmt.setInt(5, qk ? 1 : 0);
            pstmt.setInt(6, discountinued ? 1 : 0);
            pstmt.setInt(7, showNotes ? 1 : 0);
            pstmt.setString(8, notes);
            if (notifyStr == null) {
                pstmt.setInt(9, currentStock);
                pstmt.setInt(10, reorderStock);
                pstmt.setInt(11, taxid);
            } else {
                pstmt.setString(9, notifyStr);
                pstmt.setInt(10, currentStock);
                pstmt.setInt(11, reorderStock);
                pstmt.setInt(12, taxid);
            }
            pstmt.executeUpdate();
            conn.commit();
            
            // Lets get the product id of the product just created
            sql = "select last_insert_id()";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                productID = rs.getString(1);
            }
            
            // Product Price add
            for ( int i = 0; i < price.length; i++ ) {
                // I don't why I am inserting safely
                sql = "insert into `productprice` (`price`, `qty`, `prid`) " +
                    "values(?, ?, ?) ";
                PreparedStatement pstmtprice = conn.prepareStatement(sql);
                pstmtprice.setString(1, price[i]);
                pstmtprice.setString(2, priceQty[i]);
                pstmtprice.setString(3, productID);
                pstmtprice.execute();
                conn.commit();
            }
            
            // Add all the barcodes becuase it is allowed through the validate method
            if (barcode != null) {
                for(String barcodeStr : barcode) {
                    
                    sql = "insert into `barcode` (`barcode`, `prid`) values(?, ?)";
                    PreparedStatement pstmtbarcode = conn.prepareStatement(sql);
                    pstmtbarcode.setString(1, barcodeStr);
                    pstmtbarcode.setString(2, productID);
                    pstmtbarcode.execute();
                    conn.commit();
                }
            }
            conn.setAutoCommit(true);
        } catch(Exception a) {
            // We know something wrong has happened please roll us back
            try {
                System.err.println("Rolling back .... to createProduct Savepoint");
                conn.rollback(createProduct);
                conn.setAutoCommit(true);
                conn.releaseSavepoint(createProduct);
            } catch(Exception e) {
                
            }
            a.printStackTrace();
        }
    }
        
    public void updateExistingProduct() {
        try {
            
            // Is check ID Based on category
            if (CheckId == false) {
                CheckId = mc.isCategoryAgeRestricted(mcid);
            }
            
            // Set the notify type flag
            String notifyStr = null;
            if(notOnSunday) {
                notifyStr = "nosunday";
            } else if (CheckId) {
                notifyStr = "age";
            } else if (showNotes) {
                notifyStr = "shownotes";
            }
            
            String sql = "update `product` set `name` = ? , `mcid` = ?, " +
                    "`scid` = ?, `grid` = ?, `qk` = ?, `discontinue` = ?, " +
                    "`shownotes` = ?, `notes` = ?, `notifytype` = ?, `currentstock` = ?, " +
                    "`reorderlimit` = ?, `taxid` = ? where `id` = ?";
            
            if(notifyStr == null) {
                sql = "update `product` set `name` = ? , `mcid` = ?, " +
                    "`scid` = ?, `grid` = ?, `qk` = ?, `discontinue` = ?, " +
                    "`shownotes` = ?, `notes` = ?, `currentstock` = ?, " +
                    "`reorderlimit` = ?, `taxid` = ? where `id` = ?";
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, prName);
            pstmt.setInt(2, mcid);
            pstmt.setInt(3, scid);
            pstmt.setInt(4, grid);
            pstmt.setInt(5, qk ? 1 : 0);
            pstmt.setInt(6, discountinued ? 1 : 0);
            pstmt.setInt(7, showNotes ? 1 : 0);
            pstmt.setString(8, notes);
            if(notifyStr == null) {
                pstmt.setInt(9, currentStock);
                pstmt.setInt(10, reorderStock);
                pstmt.setInt(11, taxid);
                pstmt.setString(12, productID);
            } else {
                pstmt.setString(9, notifyStr);
                pstmt.setInt(10, currentStock);
                pstmt.setInt(11, reorderStock);
                pstmt.setInt(12, taxid);
                pstmt.setString(13, productID);
            }
            
            pstmt.execute();
            
            for(int i = 0; i < barcode.length; i++) {
                String barcodeStr = barcode[i].toString();
                                
                // Try and update whatever this is
                sql = "update `barcode` `br` set `br`.`barcode` = ? , " +
                        "`br`.`prid` = ? where `br`.`barcode` = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, barcodeStr);
                pstmt.setString(2, productID);
                pstmt.setString(3, barcodeStr);
                pstmt.execute();
            }
            
            addOrRemoveUnusedBarcode();
            
            // Product Price
            for(int i = 0; i < price.length; i++) {
                sql = "update `productprice` `pp` set `price` = ? , `qty` = ? " +
                        "where `prid` = ? and `qty` = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, price[i]);
                pstmt.setString(2, priceQty[i]);
                pstmt.setString(3, productID);
                pstmt.setString(4, priceQty[i]);
                pstmt.execute();
            }
            
            addOrRemoveUnusedPrice();
            
        } catch(Exception a) {
            a.printStackTrace();
        }
    }
    
    /**
     * Sometimes some barcodes need to be deleted for a particular product
     */
    public void addOrRemoveUnusedBarcode() {
        try {
            // Get the current barcodes in the database
            Vector productAssignedBarcode = new Vector();
            String sql = "select * from `barcode` where `prid` = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, productID);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                productAssignedBarcode.add(rs.getString("barcode"));
            }
            
            
            // Now just delete the barocdes that no longer need to exist
            for(Object barcodeExisting : productAssignedBarcode) {
                boolean toDelete = true;
                for(String barcodeNew : barcode) {
                    if (barcodeNew.equals(barcodeExisting.toString())) {
                        toDelete = false;
                    }
                }
                if (toDelete) {
                    sql = "delete from `barcode` where `barcode` = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, barcodeExisting.toString());
                    pstmt.executeUpdate();
                }
            }
            
            // Now add any new barcodes
            // Now add the barcodes that don't exist in the database
            for(Object newBarcode : barcode) {
                if (!productAssignedBarcode.contains(newBarcode)) {
                    sql = "insert into `barcode` (`barcode`, `prid`) values(?, ?)";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, newBarcode.toString());
                    pstmt.setString(2, productID);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Product.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Delete unused Price ranges as they are no longer required
     */
    private void addOrRemoveUnusedPrice() {
        try {
            Vector productQty = new Vector();
            String sql = "select * from `productprice` where `prid` = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, productID);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                productQty.add(rs.getString("qty"));
            }
            
            // Delete any qty's that are no longer in the GUI table
            for(Object priceQtyExisting : productQty) {
                boolean toDelete = true;
                for(String priceQtyNew : priceQty) {
                    if(priceQtyNew.equals(priceQtyExisting.toString())) {
                        toDelete = false;
                    }
                }
                
                if(toDelete) {
                    sql = "delete from `productprice` where `prid` ? and `qty` = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, productID);
                    pstmt.setString(2, priceQtyExisting.toString());
                    pstmt.executeUpdate();
                }
            }
            
            /** If there is new qty in the GUI
             * and it does not exist in the db 
             * then you need to add from here
             */
            int counter = 0;
            for(String newPriceQty : priceQty) {
                if(!productQty.contains(newPriceQty)) {
                    sql = "insert into `productprice` (`prid`, `qty`, `price`) values (?, ?, ?)";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, productID);
                    pstmt.setString(2, newPriceQty);
                    pstmt.setString(3, price[counter]);
                    pstmt.executeUpdate();
                }
                counter++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Product.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Validate the data for the product
     */
    public boolean validateData() {
        
        boolean dataValid = false;
        
        try {
            dataValid = validateBarcode();
            
        } catch(Exception ex) {
            dataValid = false;
        }
        
        return dataValid;
    }
    
    /**
     * We want to check if the barcodes provided is assigned to anything else
     */
    private boolean validateBarcode() {
        boolean barcodeValid = true;
        
        if (barcode == null) {
            return true;
        }
        try {
            // Check if the barcode is asigned to another product
            for(int i = 0; i < barcode.length; i++) {
                boolean foundBarcode = false;
                String thisProductIDStr = "";
                String sql = "select * from `barcode` where `barcode` = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, barcode[i]);
                rs = pstmt.executeQuery();
                while(rs.next()) {
                    thisProductIDStr = rs.getString("prid");
                    foundBarcode = true; 
                }
                
                // If the barcode is assigned to another product
                if(foundBarcode && !thisProductIDStr.equals(productID)) {
                    JOptionPane.showMessageDialog(null,
                            "Barcode already exists for " + thisProductIDStr,
                            "Price Error", JOptionPane.ERROR_MESSAGE);
                    barcodeValid = false;
                    break; // Because we found a faulty barcode

                }
            }
        } catch(Exception a) {
            a.printStackTrace();
        }
        
        return barcodeValid;
    }
    
    /**
     * Load a product from the given productID
     * @param productIDArg
     */
    private void loadProduct(String productIDArg) {
        try {
            String notifyStr = "";
            // First Load from the product table
            String sql = "SELECT * FROM `product` where `id` = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, productIDArg);
            rs = pstmt.executeQuery();
            
            //5000213006481
            while(rs.next()) {
                productID = rs.getString("id");
                prName = rs.getString("name");
                currentStock = rs.getInt("currentstock");
                reorderStock = rs.getInt("reorderlimit");
                mcid = rs.getInt("mcid");
                scid = rs.getInt("scid");
                grid = rs.getInt("grid");
                taxid = rs.getInt("taxid");
                qk = rs.getBoolean("qk") ? true : false;
                discountinued = rs.getString("discontinue").equals("1") ? true : false;
                notes = rs.getString("notes");
                showNotes = rs.getString("shownotes").equals("1") ? true : false;
                notifyStr = rs.getString("notifytype");
            }
            
            if (notifyStr != null) {
                if(notifyStr.equals("age")) {
                    CheckId = true;
                } else if (notifyStr.equals("noSunday")) {
                    notOnSunday = true;
                }
            }
            
            // Load from the barcode table
            sql = "select * from `barcode` where `prid` = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, productID);
            rs = pstmt.executeQuery();
            
            /** The array list will expand dynamically
             * and it will allow us to add all the barcodes
             * to the array list. 
             * then we can convert the array list to an array
             */
            ArrayList<String> barcodeAL = new ArrayList<String>();
            
            while(rs.next()) {
                barcodeAL.add(rs.getString("barcode"));
            }
            
            int barcodeCount = 0;
            barcode = new String[barcodeAL.size()];
            for(String barcodeStr : barcodeAL) {
                barcode[barcodeCount] = barcodeStr;
                barcodeCount++;
            }
            
            // Load from the price table
            sql = "select * from `productprice` where `prid` = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, productID);
            rs = pstmt.executeQuery();
            
            ArrayList<String> priceAL = new ArrayList<String>();
            ArrayList<String> priceQtyAL = new ArrayList<String>();
            
            while(rs.next()) {
                priceAL.add(rs.getString("price"));
                priceQtyAL.add(rs.getString("qty"));
            }
            
            int priceCount = 0;
            price = new String[priceAL.size()];
            priceQty = new String[priceQtyAL.size()];
            
            for (String priceStr : priceAL) {
                price[priceCount] = priceStr;
                priceQty[priceCount] = priceQtyAL.get(priceCount);
                priceCount++;
            }
        } catch(Exception a) {
            JOptionPane.showMessageDialog(null, "Unknown error occured \n while loading the product",
                    "Product Load Error", JOptionPane.ERROR_MESSAGE);
            a.printStackTrace();
        }
    }

    /**
     * update the current barcodes array with the given barcodes
     * this is typically a direct dump of barcodes table dump
     */
    public void setBarcodes(Vector data) {
        if (data.size() == 0) {
            return;
        }

        // Clear the current barcodes
        barcode = new String[data.size()];
        int counter = 0;
        for(int i = 0; i < data.size(); i++) {
            Vector barcodeArg = (Vector) data.get(i);
            barcode[counter] = barcodeArg.get(0).toString();
            counter++;
        }
        
        return;
    }
    
    /**
     * Returns 3 categories of barcodes
     * in an object array of hashmaps
     * ones to delete
     * ones to keep
     * ones to create
     * on the respectable order
     */
    private void categorisebarcodes(String[] barcodesArg) {
        HashMap barcodesToKeep = new HashMap();
        HashMap barcodesToDelete = new HashMap();
        HashMap barcodesToCreate = new HashMap();
        
        // Loop throught he barcodes inputted (Might not be a word)
        for(String barcodeArg : barcodesArg) {
            for(String barcode2Arg : barcode) {
                // Are we keeping this barcode?
                if (barcode2Arg.equals(barcodeArg)) {
                    barcodesToKeep.put(barcodeArg, "1");
                }
            }
                
            // Does the input barcode exist in the original set
            if(!barcodesToKeep.containsKey(barcodeArg)) {
                // No so we need to create it
                barcodesToCreate.put(barcodeArg, "1");
            }
        }
        
        // Check for barcodes to delete
        
        // Loop through the original set of barcodes against the input set
        for(String barcodeArg : barcode) {
            
            // by default assume the new barcode needs to be deleted
            boolean barcodeToDelete = true;
            for(String barcode2Arg : barcodesArg) {
                // Are keeping this barcode?
                if (barcode2Arg.equals(barcodeArg)) {
                    barcodeToDelete = false;
                }
            }
            
            if(barcodeToDelete) {
                barcodesToDelete.put(barcodeArg, "1");
            }
        }
        
        // Add the hashMaps to barcode categories
        barcodeCategories[0] = barcodesToDelete;
        barcodeCategories[1] = barcodesToKeep;
        barcodeCategories[2] = barcodesToCreate;
    }

    /**
     * Update the price and qty array with the vector
     */
    public void setPrices(Vector data) {
        int counter = 0;
        
        // clear down the prices so that we can start again
        priceQty = null;
        price = null;
        priceQty = new String[data.size()];
        price = new String[data.size()];
        
        for(Object rowObj : data) {

            Vector row = (Vector) rowObj;
            priceQty[counter] = row.get(0).toString();
            price[counter] = row.get(1).toString();
            counter++;
        }
    }
    
    /**
     * Returns 3 categories of barcodes
     * in an array of hashmaps
     * ones to delete 
     * ones to keep
     * ones to create
     */
    private void categoriseprices(String[] pricesArg) {
        // Need to split the price in 3 categories
        HashMap pricesToKeep = new HashMap();
        HashMap pricesToDelete = new HashMap();
        HashMap pricesToCreate = new HashMap();
        
        // Loop throught he barcodes inputted (Might not be a word)
        for(String priceArg : pricesArg) {
            for(String price2Arg : price) {
                // Are we keeping this barcode?
                if (price2Arg.equals(priceArg)) {
                    pricesToKeep.put(priceArg, "1");
                }
            }
                
            // Does the input barcode exist in the original set
            if(!pricesToKeep.containsKey(priceArg)) {
                // No so we need to create it
                pricesToCreate.put(priceArg, "1");
            }
        }
        
        // Get the prices to delete
        
        // Loop through the original set of barcodes against the input set
        for(String barcodeArg : barcode) {
            
            // by default assume the new barcode needs to be deleted
            boolean barcodeToDelete = true;
            for(String barcode2Arg : pricesArg) {
                // Are keeping this barcode?
                if (barcode2Arg.equals(barcodeArg)) {
                    barcodeToDelete = false;
                }
            }
            
            if(barcodeToDelete) {
                pricesToDelete.put(barcodeArg, "1");
            }
        }
        
        priceCategories[0] = pricesToDelete;
        priceCategories[1] = pricesToKeep;
        priceCategories[2] = pricesToCreate;
    }

    public void setProductName(String text) {
        prName = text;
    }

    public void setCategory(String selectedIndex) {
        mcid = mc.getMainCategoryID(selectedIndex);
    }

    public void setSubCategory(String selectedIndex) {
        scid = sc.getSubCategoryID(selectedIndex);
    }

    public void setTax(int selectedIndex) {
        taxid = tax.getTaxID(selectedIndex);
    }

    public void setQuickKey(boolean selected) {
        qk = selected;
    }

    public void setDiscontinuedBox(boolean selected) {
    discountinued = selected;
    }

    public void setNotesBox(boolean selected) {
        showNotes = selected;
    }
    
    public void setCheckIDBox(boolean selected) {
        CheckId = selected;
    }
    
    public void setNotOnSundayBox(boolean selected) {
        notOnSunday = selected;
    }
    
    public String[] getMainCateogries() {
        return mc.getCategories();
    }

    public String[] getSubCategories() {
        return sc.getAllCategories();
    }
    
    public int getSelectedMainCategory() {
        return mc.getSelectedCategory(mcid);
    }
    
    public Object[] getSubCategoryByMCID(String mcidNameArg) {
        
        int categoryID = mc.getMainCategoryID(mcidNameArg);
        
        return sc.getCategoryByMCID(categoryID);
    }
    
    public int getSelectedSubCategory() {
        return sc.getSelectedCategory(scid, mcid);
    }

    public String[] getTaxCode() {
        return tax.getCategories();
    }
    
    public int getSelectedTaxCode() {
        return tax.getSelectedCategory(taxid);
    }

    public void setNotes(String text) {
        notes = text;
    }

    public void setCurrentStock(String currentStockArg) {
        int currentStockInt = Integer.parseInt(currentStockArg);
        currentStock = currentStockInt;
    }

    public void setReorderStock(String reorderStockArg) {
        int reorderStockInt = Integer.parseInt(reorderStockArg);
        reorderStock = reorderStockInt;
    }

    /**
     * This method will take the productID given to this class and 
     * add it to the printqueue table ready for printing
     */
    public void addToPrintQueue() {
        try {
            String sql = "insert into printqueue (prid) values (?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, productID);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Product.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
