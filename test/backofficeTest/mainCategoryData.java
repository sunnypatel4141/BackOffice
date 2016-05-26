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

import backoffice.base.DBConnection;
import backoffice.data.MainCategory;
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
 * @author Sunny Patel
 */
public class mainCategoryData extends DBConnection {
    
    MainCategory mc;
    
    public mainCategoryData() {
        try {
            // have to do db stuff else we get a null point exception
            conn = getConnection();
            stmt = conn.createStatement();
            mc = new MainCategory();
        } catch (SQLException ex) {
            Logger.getLogger(mainCategoryData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void mainCategoryCount() {
        System.out.println("Count is there 120 categories");
        assertTrue(mc.CategoryInfo.size() == 120);
    }
    
    @Test
    public void mainCategorySpotCheck() {
        int mcid = mc.getMainCategoryID("BEER");
        System.out.println("Check If BEER category ID is as Expected 4646 : " + mcid);
        assertTrue(mcid == 4646);
        
        mcid = mc.getMainCategoryID("SOFT DRINKS");
        System.out.println("Check If SOFT DRINKS category ID is as Expected 4647 : " + mcid);
        assertTrue(mcid == 4647);
        
        mcid = mc.getMainCategoryID("CIGARETTES");
        System.out.println("Check If CIGARETTES category ID is as Expected 4648 :  " + mcid);
        assertTrue(mcid == 4648);
    }
    
    @Test
    public void mainCategoryIDSpotCheck() {
        String mcname = mc.getMainCategoryName("4725");
        System.out.println("Check If 4725 category Name is as Expected CREAM-del : " + mcname);
        assertTrue(mcname.equals("CREAM-DEL"));
        
        mcname = mc.getMainCategoryName("4707");
        System.out.println("Check If 4707 category Name is as Expected MILK-DEL : " + mcname);
        assertTrue(mcname.equals("MILK-DEL"));
        
        mcname = mc.getMainCategoryName("4694");
        System.out.println("Check If 4694 category ID is as Expected READY MEALS-DELW :  " + mcname);
        assertTrue(mcname.equals("READY MEALS-DELW"));
    }
}
