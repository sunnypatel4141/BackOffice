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
package backofficeTest;

import backoffice.base.DBConnection;
import static backoffice.base.DBConnection.conn;
import static backoffice.base.DBConnection.rs;
import static backoffice.base.DBConnection.stmt;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author User
 */
public class StockIn extends DBConnection {
    
    backoffice.StockIn si;
    int stockbeforeModify = 0;
    int reorderStock = 0;
    String stockprid;
    
    // old things
    int oldCurrentStock = 0;
    int oldReorderStock = 0;
    
    public StockIn() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        si = new backoffice.StockIn();
    }
    
    @After
    public void tearDown() {
        // Restore everything
        si = new backoffice.StockIn();
        boolean foundProduct = si.loadStockItem(stockprid);
        boolean savedProduct = si.saveItem(stockprid, oldCurrentStock, oldReorderStock);
        boolean saveSuccessful = si.save();
        getStockPrid();
        assertEquals("Current stock was correctly reset", oldCurrentStock, stockbeforeModify);
        assertEquals("Reorder stock was correctly reset", oldReorderStock, reorderStock);
    }
    
    @Test
    public void modifyStock() {
        boolean foundProduct = si.loadStockItem(stockprid);
        assertEquals("Found product " + stockprid, foundProduct, true);
        
        // prid, current_qty, reorder_qty
        boolean savedProduct = si.saveItem(stockprid, 5, 5);
        assertEquals("Saved product Successfully", savedProduct, true);
        
        boolean saveSuccessful = si.save();
        assertEquals("Changes saved successfully", saveSuccessful, true);
        
        oldCurrentStock = stockbeforeModify;
        oldReorderStock = reorderStock;
        
        // reload the product to see if it worked
        getStockPrid();
        assertEquals("Current stock was correctly updated", oldCurrentStock, stockbeforeModify);
        assertEquals("Reorder stock was correctly updated", oldReorderStock, reorderStock);
    }

   private void getStockPrid() {
       try {
            // Get the 3rd product
            String sql = "select * from product limit 3, 1";
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                stockprid = rs.getString("id");
                stockbeforeModify = rs.getInt("currentstock");
                reorderStock = rs.getInt("reorderlimit");
            }
        } catch (SQLException ex) {
            Logger.getLogger(StockIn.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
}
