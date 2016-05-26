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
package backofficeTest;

import backoffice.data.Product;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sunny Patel
 */
public class productData {
    
    InitDBConnection dbc = new InitDBConnection();
    // Test data
    String prid = "365127"; //String barcode = "000382029748";
    String barcode = "9311218112654";
    
    Product prprid = new Product();
    Product prbarcode = new Product();
    
    public productData() {
        String prid = "365127";
        String barcode = "000382029748";
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        // TODO: Create fake product and then delete it 
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void loadProductByID() {
        prprid.setProductID(prid);
        System.out.println(prprid.barcode.length);
        boolean correctbarcodecount = prprid.barcode.length == 1;
        assertTrue(correctbarcodecount);
        System.out.println(prprid.prName);
        assertTrue(prprid.prName.equals("SUN EXOTIC FRUIT PARADISE288ML P/M"));
        System.out.println(prprid.mcid);
        assertTrue(prprid.mcid == 4647);
        System.out.println(prprid.scid);
        assertTrue(prprid.scid == 0);
        System.out.println(prprid.grid);
        assertTrue(prprid.grid == 0);
        System.out.println(prprid.taxid);
        assertTrue(prprid.taxid == 0);
        System.out.println(prprid.qk);
        assertFalse(prprid.qk);
        
        System.out.println(prprid.price.length == 1);
        assertTrue(prprid.price.length == 1);
        
        System.out.println(prprid.priceQty.length == 1);
        assertTrue(prprid.priceQty.length == 1);
    }
    
    @Test
    public void loadProductByBarcode() {
        prbarcode.setBarcode(barcode);
        System.out.println(prbarcode.barcode.length);
        boolean correctbarcodecount = prbarcode.barcode.length == 1;
        assertTrue(correctbarcodecount);
        System.out.println(prbarcode.prName);
        assertTrue(prbarcode.prName.equals("LINDEMAN'S BIN25 SPRKLING 75CL"));
        System.out.println(prbarcode.mcid);
        assertTrue(prbarcode.mcid == 4654);
        System.out.println(prbarcode.scid);
        assertTrue(prbarcode.scid == 0);
        System.out.println(prbarcode.grid);
        assertTrue(prbarcode.grid == 0);
        System.out.println(prbarcode.taxid);
        assertTrue(prbarcode.taxid == 0);
        System.out.println(prbarcode.qk);
        assertFalse(prbarcode.qk);
        
        System.out.println(prbarcode.price.length == 1);
        assertTrue(prbarcode.price.length == 1);
        
        System.out.println(prbarcode.priceQty.length == 1);
        assertTrue(prbarcode.priceQty.length == 1);
    }
}