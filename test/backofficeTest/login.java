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
public class login {
    
    backoffice.login lgn;
    
    public login() {
        lgn = new backoffice.login();
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void checkLogin() {
        lgn.un = "1";
        lgn.pw = "1";
        boolean authenticated = lgn.authenticateAndLogin();
        assertTrue(authenticated);
    }
    
    @Test
    public void checkIncorrectLogin() {
        lgn.un = "donotenter";
        lgn.pw = "donotenter";
        boolean authenticated = lgn.authenticateAndLogin();
        assertFalse(authenticated);
    }
}
